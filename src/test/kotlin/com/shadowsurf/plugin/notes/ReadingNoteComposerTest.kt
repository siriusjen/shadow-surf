package com.shadowsurf.plugin.notes

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class ReadingNoteComposerTest {

    @Test
    fun shouldComposeReadingNoteFromContext() {
        val composer = ReadingNoteComposer { "2026-04-01 09:30" }

        val note = composer.compose(
            context = ReadingNoteContext(
                selectedText = "Selected excerpt",
                pageTitle = "Example Title",
                pageUrl = "https://example.com/article",
            ),
            noteText = "Short note",
            tagsText = "tag1, tag2 ,  ,tag3",
        )

        requireNotNull(note)
        assertEquals("2026-04-01 09:30", note.timestamp)
        assertEquals("Example Title", note.pageTitle)
        assertEquals("https://example.com/article", note.pageUrl)
        assertEquals("Selected excerpt", note.selectedText)
        assertEquals("Short note", note.userNote)
        assertEquals(listOf("tag1", "tag2", "tag3"), note.tags)
    }

    @Test
    fun shouldReturnNullWhenSelectionIsBlank() {
        val composer = ReadingNoteComposer { "2026-04-01 09:30" }

        val note = composer.compose(
            context = ReadingNoteContext(
                selectedText = "   ",
                pageTitle = "Example Title",
                pageUrl = "https://example.com/article",
            ),
            noteText = "Short note",
            tagsText = "tag1",
        )

        assertNull(note)
    }
}
