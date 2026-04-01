package com.shadowsurf.plugin.ui

import com.intellij.ide.ui.LafManagerListener
import com.intellij.openapi.Disposable
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.components.service
import com.intellij.openapi.ui.popup.Balloon
import com.intellij.openapi.ui.popup.JBPopupFactory
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.Messages
import com.intellij.ui.JBColor
import com.intellij.ui.awt.RelativePoint
import com.intellij.ui.components.JBLabel
import com.intellij.ui.jcef.JBCefApp
import com.intellij.ui.jcef.JBCefBrowser
import com.intellij.util.ui.JBUI
import com.shadowsurf.plugin.browser.BrowserTabManager
import com.shadowsurf.plugin.browser.DarkModeInjector
import com.shadowsurf.plugin.browser.SelectionCaptureBridge
import com.shadowsurf.plugin.notes.ReadingNoteComposer
import com.shadowsurf.plugin.notes.ReadingNoteContext
import com.shadowsurf.plugin.notes.ReadingNoteFormatter
import com.shadowsurf.plugin.notes.ReadingNoteWriteMode
import com.shadowsurf.plugin.notes.ReadingNoteWriter
import com.shadowsurf.plugin.settings.ReadingNotesSettings
import org.cef.browser.CefBrowser
import org.cef.browser.CefFrame
import org.cef.handler.CefDisplayHandlerAdapter
import org.cef.handler.CefLoadHandlerAdapter
import java.awt.BorderLayout
import java.awt.FlowLayout
import java.awt.KeyEventDispatcher
import java.awt.KeyboardFocusManager
import java.awt.Toolkit
import java.awt.event.InputEvent
import java.awt.event.KeyEvent
import java.nio.file.Files
import java.nio.file.Path
import java.net.URI
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import javax.swing.JButton
import javax.swing.JFileChooser
import javax.swing.JPanel
import javax.swing.JTabbedPane
import javax.swing.JTextField
import javax.swing.KeyStroke
import javax.swing.JToggleButton
import javax.swing.SwingUtilities

class ShadowSurfPanel(private val project: Project) : JPanel(BorderLayout()), Disposable {

    private val tabManager = BrowserTabManager(maxTabs = 5)
    private val darkModeInjector = DarkModeInjector()
    private val browsers = linkedMapOf<String, JBCefBrowser>()
    private val selectionBridges = linkedMapOf<String, SelectionCaptureBridge>()
    private val readingNotesSettings = service<ReadingNotesSettings>()
    private val readingNoteFormatter = ReadingNoteFormatter()
    private val readingNoteComposer = ReadingNoteComposer {
        LocalDateTime.now().format(TIMESTAMP_FORMATTER)
    }
    private val noteBar = ReadingNoteBar()
    private val noteShortcut = KeyStroke.getKeyStroke(
        KeyEvent.VK_M,
        Toolkit.getDefaultToolkit().menuShortcutKeyMaskEx or InputEvent.SHIFT_DOWN_MASK,
    )
    private val noteDispatcher = KeyEventDispatcher { event ->
        if (event.id != KeyEvent.KEY_PRESSED) {
            return@KeyEventDispatcher false
        }
        if (KeyStroke.getKeyStrokeForEvent(event) != noteShortcut) {
            return@KeyEventDispatcher false
        }
        val focusOwner = KeyboardFocusManager.getCurrentKeyboardFocusManager().focusOwner ?: return@KeyEventDispatcher false
        if (!SwingUtilities.isDescendingFrom(focusOwner, this)) {
            return@KeyEventDispatcher false
        }
        triggerReadingNoteCapture()
        true
    }

    private val addressField = JTextField()
    private val tabbedPane = JTabbedPane()
    private val darkToggle = JToggleButton("Dark Page")
    private var pendingNoteContext: ReadingNoteContext? = null

    init {
        border = JBUI.Borders.empty()
        if (!JBCefApp.isSupported()) {
            add(createUnsupportedPanel(), BorderLayout.CENTER)
        } else {
            layout = BorderLayout(JBUI.scale(0), JBUI.scale(8))
            add(createToolbar(), BorderLayout.NORTH)
            add(tabbedPane, BorderLayout.CENTER)
            add(noteBar, BorderLayout.SOUTH)

            tabbedPane.addChangeListener {
                syncAddressBarFromSelection()
            }

            darkToggle.isSelected = !JBColor.isBright()
            darkToggle.addActionListener {
                applyDarkModeToAllTabs()
            }

            project.messageBus.connect(this).subscribe(
                LafManagerListener.TOPIC,
                LafManagerListener {
                    applyTheme()
                    applyDarkModeToAllTabs()
                },
            )

            noteBar.onSave { noteText, tagsText ->
                saveReadingNote(noteText, tagsText, forceChooseTarget = false)
            }
            noteBar.onSaveAs { noteText, tagsText ->
                saveReadingNote(noteText, tagsText, forceChooseTarget = true)
            }
            noteBar.onCancel {
                pendingNoteContext = null
            }

            KeyboardFocusManager.getCurrentKeyboardFocusManager().addKeyEventDispatcher(noteDispatcher)
            applyTheme()
            openNewTab(DEFAULT_URL)
        }
    }

    private fun createToolbar(): JPanel {
        val panel = JPanel(BorderLayout(JBUI.scale(8), 0))
        panel.border = JBUI.Borders.empty(8)

        val navPanel = JPanel(FlowLayout(FlowLayout.LEFT, JBUI.scale(6), 0))
        val backButton = JButton("←")
        val forwardButton = JButton("→")
        val refreshButton = JButton("⟳")
        val openButton = JButton("Open")
        val newTabButton = JButton("+")
        val closeTabButton = JButton("×")

        backButton.addActionListener { currentBrowser()?.cefBrowser?.goBack() }
        forwardButton.addActionListener { currentBrowser()?.cefBrowser?.goForward() }
        refreshButton.addActionListener { currentBrowser()?.cefBrowser?.reload() }
        openButton.addActionListener { openAddressFieldUrl() }
        newTabButton.addActionListener { openNewTab(DEFAULT_URL) }
        closeTabButton.addActionListener { closeCurrentTab() }
        addressField.addActionListener { openAddressFieldUrl() }

        navPanel.add(backButton)
        navPanel.add(forwardButton)
        navPanel.add(refreshButton)
        navPanel.add(newTabButton)
        navPanel.add(closeTabButton)

        panel.add(navPanel, BorderLayout.WEST)
        panel.add(addressField, BorderLayout.CENTER)

        val actionPanel = JPanel(FlowLayout(FlowLayout.RIGHT, JBUI.scale(6), 0))
        actionPanel.add(darkToggle)
        actionPanel.add(openButton)
        panel.add(actionPanel, BorderLayout.EAST)

        return panel
    }

    private fun createUnsupportedPanel(): JPanel {
        return JPanel(BorderLayout()).apply {
            border = JBUI.Borders.empty(16)
            add(JBLabel("JCEF is not available in this IDE runtime."), BorderLayout.CENTER)
        }
    }

    private fun openAddressFieldUrl() {
        val current = currentTabId() ?: return
        val normalizedUrl = normalizeUrl(addressField.text)
        val browser = browsers[current] ?: return
        browser.loadURL(normalizedUrl)
        tabManager.updateTab(current, url = normalizedUrl, title = titleFromUrl(normalizedUrl))
        refreshSelectedTabTitle()
    }

    private fun openNewTab(initialUrl: String) {
        val normalizedUrl = normalizeUrl(initialUrl)
        val title = titleFromUrl(normalizedUrl)
        val state = tabManager.openTab(normalizedUrl, title)
        if (state == null) {
            Messages.showInfoMessage(project, "ShadowSurf keeps up to 5 tabs in MVP mode.", "ShadowSurf")
            return
        }

        val browser = JBCefBrowser(normalizedUrl)
        browsers[state.id] = browser
        selectionBridges[state.id] = SelectionCaptureBridge(browser)
        attachHandlers(state.id, browser)
        tabbedPane.addTab(state.title, browser.component)
        tabbedPane.selectedIndex = tabbedPane.tabCount - 1
        addressField.text = normalizedUrl
        applyDarkMode(browser)
    }

    private fun attachHandlers(tabId: String, browser: JBCefBrowser) {
        browser.jbCefClient.addLoadHandler(object : CefLoadHandlerAdapter() {
            override fun onLoadEnd(cefBrowser: CefBrowser?, frame: CefFrame?, httpStatusCode: Int) {
                val currentUrl = cefBrowser?.url ?: return
                ApplicationManager.getApplication().invokeLater {
                    tabManager.updateTab(tabId, url = currentUrl, title = titleFromUrl(currentUrl))
                    refreshAllTabTitles()
                    if (currentTabId() == tabId) {
                        addressField.text = currentUrl
                    }
                    applyDarkMode(browser)
                }
            }
        }, browser.cefBrowser)

        browser.jbCefClient.addDisplayHandler(object : CefDisplayHandlerAdapter() {
            override fun onTitleChange(cefBrowser: CefBrowser?, title: String?) {
                val resolvedTitle = title?.takeIf { it.isNotBlank() } ?: return
                ApplicationManager.getApplication().invokeLater {
                    tabManager.updateTab(tabId, title = resolvedTitle)
                    refreshAllTabTitles()
                }
            }
        }, browser.cefBrowser)
    }

    private fun closeCurrentTab() {
        val selectedIndex = tabbedPane.selectedIndex
        val tabId = currentTabId() ?: return
        val browser = browsers.remove(tabId)
        selectionBridges.remove(tabId)?.dispose()
        browser?.dispose()
        tabManager.closeTab(tabId)
        tabbedPane.removeTabAt(selectedIndex)

        if (tabbedPane.tabCount == 0) {
            openNewTab(DEFAULT_URL)
            return
        }

        syncAddressBarFromSelection()
        refreshAllTabTitles()
    }

    private fun refreshAllTabTitles() {
        tabManager.tabs().forEachIndexed { index, tab ->
            if (index < tabbedPane.tabCount) {
                tabbedPane.setTitleAt(index, tab.title)
            }
        }
    }

    private fun refreshSelectedTabTitle() {
        val selectedIndex = tabbedPane.selectedIndex
        val tab = tabManager.selectedTab() ?: return
        if (selectedIndex >= 0 && selectedIndex < tabbedPane.tabCount) {
            tabbedPane.setTitleAt(selectedIndex, tab.title)
        }
    }

    private fun syncAddressBarFromSelection() {
        val selectedIndex = tabbedPane.selectedIndex
        if (selectedIndex < 0) {
            return
        }
        val tab = tabManager.tabs().getOrNull(selectedIndex) ?: return
        tabManager.selectTab(tab.id)
        addressField.text = tab.url
    }

    private fun currentBrowser(): JBCefBrowser? = currentTabId()?.let(browsers::get)
    private fun currentSelectionBridge(): SelectionCaptureBridge? = currentTabId()?.let(selectionBridges::get)

    private fun currentTabId(): String? {
        val selectedIndex = tabbedPane.selectedIndex
        return tabManager.tabs().getOrNull(selectedIndex)?.id
    }

    private fun applyTheme() {
        background = JBColor.PanelBackground
        addressField.background = JBColor.background()
        addressField.foreground = JBColor.foreground()
        tabbedPane.background = JBColor.PanelBackground
        tabbedPane.foreground = JBColor.foreground()
        SwingUtilities.updateComponentTreeUI(this)
    }

    private fun applyDarkModeToAllTabs() {
        browsers.values.forEach(::applyDarkMode)
    }

    private fun applyDarkMode(browser: JBCefBrowser) {
        darkModeInjector.apply(browser, darkToggle.isSelected)
    }

    private fun normalizeUrl(value: String): String {
        val trimmed = value.trim()
        return when {
            trimmed.isBlank() -> DEFAULT_URL
            trimmed.startsWith("http://") || trimmed.startsWith("https://") -> trimmed
            else -> "https://$trimmed"
        }
    }

    private fun titleFromUrl(url: String): String {
        return runCatching {
            val host = URI(url).host?.removePrefix("www.")
            if (host.isNullOrBlank()) url else host
        }.getOrDefault(url)
    }

    override fun dispose() {
        KeyboardFocusManager.getCurrentKeyboardFocusManager().removeKeyEventDispatcher(noteDispatcher)
        selectionBridges.values.forEach { it.dispose() }
        selectionBridges.clear()
        browsers.values.forEach { it.dispose() }
        browsers.clear()
    }

    private fun triggerReadingNoteCapture() {
        val bridge = currentSelectionBridge() ?: return
        val currentTab = tabManager.selectedTab() ?: return
        bridge.captureSelection { selectedText ->
            if (selectedText.isNullOrBlank()) {
                JBPopupFactory.getInstance()
                    .createHtmlTextBalloonBuilder("Select text first.", null, JBColor.border(), null)
                    .setFadeoutTime(1500)
                    .createBalloon()
                    .show(RelativePoint.getSouthOf(addressField), Balloon.Position.below)
                return@captureSelection
            }
            pendingNoteContext = ReadingNoteContext(
                selectedText = selectedText,
                pageTitle = currentTab.title,
                pageUrl = currentTab.url,
            )
            noteBar.open(
                selectedText = selectedText,
                pageTitle = currentTab.title,
                pageUrl = currentTab.url,
            )
            revalidate()
            repaint()
        }
    }

    private fun saveReadingNote(noteText: String, tagsText: String, forceChooseTarget: Boolean) {
        val note = readingNoteComposer.compose(pendingNoteContext, noteText, tagsText) ?: return
        val saveTarget = resolveSaveTarget(forceChooseTarget) ?: return

        runCatching {
            ReadingNoteWriter(saveTarget.path, readingNoteFormatter).write(note, saveTarget.mode)
        }.onSuccess {
            pendingNoteContext = null
            noteBar.reset()
        }.onFailure { error ->
            Messages.showErrorDialog(project, error.message ?: "Failed to save reading note.", "ShadowSurf")
        }
    }

    private fun resolveSaveTarget(forceChooseTarget: Boolean): SaveTarget? {
        if (!forceChooseTarget && readingNotesSettings.hasSelectedNoteFile()) {
            return SaveTarget(readingNotesSettings.resolvedPath(), ReadingNoteWriteMode.APPEND)
        }

        val chosenFile = chooseSaveTarget() ?: return null
        readingNotesSettings.updateSelectedFile(chosenFile)
        val mode = when {
            Files.exists(chosenFile) -> confirmWriteMode() ?: return null
            else -> ReadingNoteWriteMode.APPEND
        }
        return SaveTarget(chosenFile, mode)
    }

    private fun chooseSaveTarget(): Path? {
        val chooser = JFileChooser().apply {
            dialogTitle = "Choose Reading Notes File"
            fileSelectionMode = JFileChooser.FILES_ONLY
            selectedFile = readingNotesSettings.resolvedPath().toFile()
        }
        val result = chooser.showSaveDialog(this)
        if (result != JFileChooser.APPROVE_OPTION) {
            return null
        }
        return chooser.selectedFile?.toPath()?.normalize()
    }

    private fun confirmWriteMode(): ReadingNoteWriteMode? {
        val choice = Messages.showDialog(
            project,
            "The selected note file already exists. How do you want to save this note?",
            "ShadowSurf",
            arrayOf("Append", "Overwrite", "Cancel"),
            0,
            Messages.getQuestionIcon(),
        )
        return when (choice) {
            0 -> ReadingNoteWriteMode.APPEND
            1 -> ReadingNoteWriteMode.OVERWRITE
            else -> null
        }
    }

    companion object {
        private const val DEFAULT_URL = "https://www.baidu.com"
        private val TIMESTAMP_FORMATTER: DateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")
    }

    private data class SaveTarget(
        val path: Path,
        val mode: ReadingNoteWriteMode,
    )
}
