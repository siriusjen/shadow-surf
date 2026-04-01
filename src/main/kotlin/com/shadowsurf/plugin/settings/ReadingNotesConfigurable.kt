package com.shadowsurf.plugin.settings

import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.options.SearchableConfigurable
import com.intellij.ui.components.JBTextField
import com.intellij.util.ui.FormBuilder
import java.awt.BorderLayout
import javax.swing.JComponent
import javax.swing.JPanel
import javax.swing.event.DocumentEvent
import javax.swing.event.DocumentListener

class ReadingNotesConfigurable : SearchableConfigurable {

    private val directoryField = JBTextField()
    private val fileNameField = JBTextField()
    private val resolvedPathField = JBTextField().apply {
        isEditable = false
    }
    private val settings: ReadingNotesSettings
        get() = ApplicationManager.getApplication().getService(ReadingNotesSettings::class.java)

    private var component: JPanel? = null

    override fun getId(): String = "com.shadowsurf.plugin.readingNotes"

    override fun getDisplayName(): String = "ShadowSurf Reading Notes"

    override fun createComponent(): JComponent {
        if (component == null) {
            val listener = object : DocumentListener {
                override fun insertUpdate(e: DocumentEvent?) = updateResolvedPath()
                override fun removeUpdate(e: DocumentEvent?) = updateResolvedPath()
                override fun changedUpdate(e: DocumentEvent?) = updateResolvedPath()
            }
            directoryField.document.addDocumentListener(listener)
            fileNameField.document.addDocumentListener(listener)

            component = JPanel(BorderLayout()).apply {
                add(
                    FormBuilder.createFormBuilder()
                        .addLabeledComponent("Notes Directory", directoryField)
                        .addLabeledComponent("Notes File Name", fileNameField)
                        .addLabeledComponent("Resolved Path", resolvedPathField)
                        .panel,
                    BorderLayout.NORTH,
                )
            }
        }
        reset()
        return component!!
    }

    override fun isModified(): Boolean {
        val state = settings.state
        return directoryField.text != state.notesDirectory || fileNameField.text != state.notesFileName
    }

    override fun apply() {
        settings.loadState(
            ReadingNotesSettings.State(
                notesDirectory = directoryField.text.trim(),
                notesFileName = fileNameField.text.trim(),
                hasSelectedNoteFile = true,
            ),
        )
        updateResolvedPath()
    }

    override fun reset() {
        val state = settings.state
        directoryField.text = state.notesDirectory
        fileNameField.text = state.notesFileName
        updateResolvedPath()
    }

    private fun updateResolvedPath() {
        resolvedPathField.text = ReadingNotesSettings.resolvePath(
            ReadingNotesSettings.State(
                notesDirectory = directoryField.text,
                notesFileName = fileNameField.text,
            ),
        ).toString()
    }
}
