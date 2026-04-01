package com.shadowsurf.plugin.settings

import com.intellij.openapi.components.PersistentStateComponent
import com.intellij.openapi.components.Service
import com.intellij.openapi.components.State
import com.intellij.openapi.components.Storage
import java.nio.file.Path
import java.nio.file.Paths

@Service(Service.Level.APP)
@State(name = "ShadowSurfReadingNotesSettings", storages = [Storage("shadowSurfReadingNotes.xml")])
class ReadingNotesSettings : PersistentStateComponent<ReadingNotesSettings.State> {

    data class State(
        var notesDirectory: String = DEFAULT_NOTES_DIRECTORY,
        var notesFileName: String = DEFAULT_NOTES_FILE_NAME,
        var hasSelectedNoteFile: Boolean = false,
    )

    private var state = State()

    override fun getState(): State = state

    override fun loadState(state: State) {
        this.state = state
    }

    fun resolvedPath(): Path = resolvePath(state)

    fun hasSelectedNoteFile(): Boolean = state.hasSelectedNoteFile

    fun updateSelectedFile(path: Path) {
        val normalized = path.normalize()
        val parent = normalized.parent?.toString().orEmpty()
        state = state.copy(
            notesDirectory = parent.ifBlank { DEFAULT_NOTES_DIRECTORY },
            notesFileName = normalized.fileName.toString(),
            hasSelectedNoteFile = true,
        )
    }

    companion object {
        const val DEFAULT_NOTES_DIRECTORY = "~/Documents/ShadowSurf"
        const val DEFAULT_NOTES_FILE_NAME = "reading-notes.md"

        fun resolvePath(state: State): Path {
            val rawDirectory = state.notesDirectory.trim().ifBlank { DEFAULT_NOTES_DIRECTORY }
            val expandedDirectory = if (rawDirectory.startsWith("~/")) {
                Paths.get(System.getProperty("user.home")).resolve(rawDirectory.removePrefix("~/"))
            } else {
                Paths.get(rawDirectory)
            }
            val rawFileName = state.notesFileName.trim().ifBlank { DEFAULT_NOTES_FILE_NAME }
            return expandedDirectory.resolve(rawFileName).normalize()
        }
    }
}
