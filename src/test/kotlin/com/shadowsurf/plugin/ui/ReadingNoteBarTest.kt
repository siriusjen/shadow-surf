package com.shadowsurf.plugin.ui

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class ReadingNoteBarTest {

    @Test
    fun shouldBeHiddenByDefault() {
        val bar = ReadingNoteBar()

        assertFalse(bar.isVisible)
    }

    @Test
    fun shouldPopulateAndResetFields() {
        val bar = ReadingNoteBar()

        bar.open(
            selectedText = "Excerpt",
            pageTitle = "Example",
            pageUrl = "https://example.com",
        )
        bar.setNoteText("Short note")
        bar.setTagsText("tag1, tag2")
        bar.reset()

        assertFalse(bar.isVisible)
        assertEquals("", bar.noteText())
        assertEquals("", bar.tagsText())
        assertEquals("", bar.previewText())
    }

    @Test
    fun shouldInvokeSaveAsListener() {
        val bar = ReadingNoteBar()
        var called = false

        bar.onSaveAs { _, _ ->
            called = true
        }
        bar.triggerSaveAs()

        assertTrue(called)
    }
}
