package com.shadowsurf.plugin.ui

import com.intellij.ide.ui.LafManagerListener
import com.intellij.openapi.Disposable
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.Messages
import com.intellij.ui.JBColor
import com.intellij.ui.components.JBLabel
import com.intellij.ui.jcef.JBCefApp
import com.intellij.ui.jcef.JBCefBrowser
import com.intellij.util.ui.JBUI
import com.shadowsurf.plugin.browser.BrowserTabManager
import com.shadowsurf.plugin.browser.DarkModeInjector
import org.cef.browser.CefBrowser
import org.cef.browser.CefFrame
import org.cef.handler.CefDisplayHandlerAdapter
import org.cef.handler.CefLoadHandlerAdapter
import java.awt.BorderLayout
import java.awt.FlowLayout
import java.net.URI
import javax.swing.JButton
import javax.swing.JPanel
import javax.swing.JTabbedPane
import javax.swing.JTextField
import javax.swing.JToggleButton
import javax.swing.SwingUtilities

class ShadowSurfPanel(private val project: Project) : JPanel(BorderLayout()), Disposable {

    private val tabManager = BrowserTabManager(maxTabs = 5)
    private val darkModeInjector = DarkModeInjector()
    private val browsers = linkedMapOf<String, JBCefBrowser>()

    private val addressField = JTextField()
    private val tabbedPane = JTabbedPane()
    private val darkToggle = JToggleButton("Dark Page")

    init {
        border = JBUI.Borders.empty()
        if (!JBCefApp.isSupported()) {
            add(createUnsupportedPanel(), BorderLayout.CENTER)
        } else {
            layout = BorderLayout(JBUI.scale(0), JBUI.scale(8))
            add(createToolbar(), BorderLayout.NORTH)
            add(tabbedPane, BorderLayout.CENTER)

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
        browsers.values.forEach { it.dispose() }
        browsers.clear()
    }

    companion object {
        private const val DEFAULT_URL = "https://www.baidu.com"
    }
}
