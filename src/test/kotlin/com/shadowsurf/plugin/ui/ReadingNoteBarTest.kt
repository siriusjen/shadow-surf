package com.shadowsurf.plugin.ui

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue
import javax.swing.JPanel

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

    @Test
    fun shouldUseMultilineWrappingNoteInput() {
        val bar = ReadingNoteBar()

        bar.setNoteText("Line 1\nLine 2")

        assertTrue(bar.noteInputWraps())
        assertEquals("Line 1\nLine 2", bar.noteText())
    }

    @Test
    fun shouldSaveOnKeyboardShortcut() {
        val bar = ReadingNoteBar()
        var receivedNote = ""
        var receivedTags = ""

        bar.setNoteText("A long note")
        bar.setTagsText("tag1")
        bar.onSave { noteText, tagsText ->
            receivedNote = noteText
            receivedTags = tagsText
        }

        bar.triggerKeyboardSave()

        assertEquals("A long note", receivedNote)
        assertEquals("tag1", receivedTags)
    }

    @Test
    fun shouldRequestParentRelayoutWhenReset() {
        var revalidated = false
        val parent = object : JPanel() {
            override fun revalidate() {
                revalidated = true
                super.revalidate()
            }
        }
        val bar = ReadingNoteBar()
        parent.add(bar)

        bar.open(
            selectedText = "Excerpt",
            pageTitle = "Example",
            pageUrl = "https://example.com",
        )
        revalidated = false

        bar.reset()

        assertTrue(revalidated)
    }
}
