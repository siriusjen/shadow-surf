package com.shadowsurf.plugin.ui

import com.intellij.util.ui.JBUI
import java.awt.BorderLayout
import java.awt.FlowLayout
import javax.swing.JButton
import javax.swing.JLabel
import javax.swing.JPanel
import javax.swing.JTextArea
import javax.swing.JTextField

class ReadingNoteBar : JPanel(BorderLayout(JBUI.scale(8), JBUI.scale(8))) {

    private val previewArea = JTextArea().apply {
        lineWrap = true
        wrapStyleWord = true
        isEditable = false
        rows = 3
    }
    private val noteField = JTextField()
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
        fieldsPanel.add(noteField, BorderLayout.CENTER)
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
}
