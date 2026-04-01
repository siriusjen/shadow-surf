package com.shadowsurf.plugin.settings

import java.nio.file.Paths
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class ReadingNotesSettingsTest {

    @Test
    fun shouldUseDefaultDirectoryAndFileName() {
        val settings = ReadingNotesSettings.State()

        assertEquals("~/Documents/ShadowSurf", settings.notesDirectory)
        assertEquals("reading-notes.md", settings.notesFileName)
        assertFalse(settings.hasSelectedNoteFile)
    }

    @Test
    fun shouldResolveCustomPath() {
        val state = ReadingNotesSettings.State(
            notesDirectory = "~/Documents/notes",
            notesFileName = "custom.md",
        )

        val resolved = ReadingNotesSettings.resolvePath(state)

        assertEquals(
            System.getProperty("user.home") + "/Documents/notes/custom.md",
            resolved.toString(),
        )
    }

    @Test
    fun shouldUpdateStateFromChosenFile() {
        val settings = ReadingNotesSettings()

        settings.updateSelectedFile(Paths.get("/tmp/custom/location/my-notes.md"))

        val state = requireNotNull(settings.getState())
        assertEquals("/tmp/custom/location", state.notesDirectory)
        assertEquals("my-notes.md", state.notesFileName)
        assertTrue(state.hasSelectedNoteFile)
    }
}
