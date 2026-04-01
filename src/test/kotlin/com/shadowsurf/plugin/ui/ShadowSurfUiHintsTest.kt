package com.shadowsurf.plugin.ui

import kotlin.test.Test
import kotlin.test.assertEquals

class ShadowSurfUiHintsTest {

    @Test
    fun shouldExposeReadingNoteToolbarHint() {
        assertEquals("Note", ShadowSurfUiHints.READING_NOTE_BUTTON_LABEL)
        assertEquals(
            "Add Reading Note (Shortcut: Cmd/Ctrl + Shift + M)",
            ShadowSurfUiHints.readingNoteButtonTooltip(),
        )
    }
}
