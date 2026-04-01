package com.shadowsurf.plugin.ui

import com.intellij.util.ui.JBUI
import java.awt.BorderLayout
import java.awt.FlowLayout
import java.awt.Toolkit
import java.awt.event.KeyEvent
import javax.swing.JButton
import javax.swing.JLabel
import javax.swing.JPanel
import javax.swing.JScrollPane
import javax.swing.JTextArea
import javax.swing.JTextField
import javax.swing.KeyStroke

class ReadingNoteBar : JPanel(BorderLayout(JBUI.scale(8), JBUI.scale(8))) {

    private val previewArea = JTextArea().apply {
        lineWrap = true
        wrapStyleWord = true
        isEditable = false
        rows = 3
    }
    private val noteField = JTextArea().apply {
        lineWrap = true
        wrapStyleWord = true
        rows = 4
    }
    private val tagsField = JTextField()
    private val saveButton = JButton("Save")
    private val saveAsButton = JButton("Save As...")
    private val cancelButton = JButton("Cancel")

    private var onSave: ((String, String) -> Unit)? = null
    private var onSaveAs: ((String, String) -> Unit)? = null
    private var onCancel: (() -> Unit)? = null

    init {
        isVisible = false
        border = JBUI.Borders.empty(8)

        val formPanel = JPanel(BorderLayout(JBUI.scale(0), JBUI.scale(6)))
        formPanel.add(JLabel("Selected Text"), BorderLayout.NORTH)
        formPanel.add(previewArea, BorderLayout.CENTER)

        val fieldsPanel = JPanel(BorderLayout(JBUI.scale(8), JBUI.scale(6)))
        fieldsPanel.add(JScrollPane(noteField), BorderLayout.CENTER)
        fieldsPanel.add(tagsField, BorderLayout.SOUTH)

        val actionPanel = JPanel(FlowLayout(FlowLayout.RIGHT, JBUI.scale(6), 0))
        actionPanel.add(cancelButton)
        actionPanel.add(saveAsButton)
        actionPanel.add(saveButton)

        add(formPanel, BorderLayout.NORTH)
        add(fieldsPanel, BorderLayout.CENTER)
        add(actionPanel, BorderLayout.SOUTH)

        saveButton.addActionListener { onSave?.invoke(noteField.text.trim(), tagsField.text.trim()) }
        saveAsButton.addActionListener { onSaveAs?.invoke(noteField.text.trim(), tagsField.text.trim()) }
        cancelButton.addActionListener {
            onCancel?.invoke()
            reset()
        }

        val saveKeyStroke = KeyStroke.getKeyStroke(
            KeyEvent.VK_ENTER,
            Toolkit.getDefaultToolkit().menuShortcutKeyMaskEx,
        )
        noteField.inputMap.put(saveKeyStroke, "shadowSurf.saveNote")
        noteField.actionMap.put(
            "shadowSurf.saveNote",
            object : javax.swing.AbstractAction() {
                override fun actionPerformed(event: java.awt.event.ActionEvent?) {
                    saveButton.doClick()
                }
            },
        )
    }

    fun open(selectedText: String, pageTitle: String, pageUrl: String) {
        previewArea.text = buildString {
            append(selectedText)
            appendLine()
            appendLine()
            append("[$pageTitle] $pageUrl")
        }
        isVisible = true
        noteField.requestFocusInWindow()
    }

    fun reset() {
        previewArea.text = ""
        noteField.text = ""
        tagsField.text = ""
        isVisible = false
        revalidate()
        repaint()
        parent?.revalidate()
        parent?.repaint()
    }

    fun setNoteText(value: String) {
        noteField.text = value
    }

    fun setTagsText(value: String) {
        tagsField.text = value
    }

    fun noteText(): String = noteField.text

    fun tagsText(): String = tagsField.text

    fun previewText(): String = previewArea.text

    fun onSave(listener: (String, String) -> Unit) {
        onSave = listener
    }

    fun onSaveAs(listener: (String, String) -> Unit) {
        onSaveAs = listener
    }

    fun onCancel(listener: () -> Unit) {
        onCancel = listener
    }

    fun triggerSaveAs() {
        saveAsButton.doClick()
    }

    fun triggerKeyboardSave() {
        noteField.actionMap.get("shadowSurf.saveNote")?.actionPerformed(null)
    }

    fun noteInputWraps(): Boolean = noteField.lineWrap && noteField.wrapStyleWord
}
