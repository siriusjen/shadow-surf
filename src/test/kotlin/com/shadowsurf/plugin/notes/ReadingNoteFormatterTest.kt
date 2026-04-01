package com.shadowsurf.plugin.notes

import kotlin.test.Test
import kotlin.test.assertEquals

class ReadingNoteFormatterTest {

    @Test
    fun shouldFormatMarkdownWithTags() {
        val note = ReadingNote(
            timestamp = "2026-04-01 10:00",
            pageTitle = "Example Article",
            pageUrl = "https://example.com/article",
            selectedText = "Important excerpt",
            userNote = "Short note",
            tags = listOf("history", "essay"),
        )

        val markdown = ReadingNoteFormatter().format(note)

        assertEquals(
            """
            ## 2026-04-01 10:00
            
            - Title: Example Article
            - URL: https://example.com/article
            - Tags: history, essay
            
            > Important excerpt
            
            Note:
            Short note
            """.trimIndent(),
            markdown,
        )
    }

    @Test
    fun shouldFormatMarkdownWithoutTagsAsDash() {
        val note = ReadingNote(
            timestamp = "2026-04-01 10:00",
            pageTitle = "Example Article",
            pageUrl = "https://example.com/article",
            selectedText = "Important excerpt",
            userNote = "Short note",
            tags = emptyList(),
        )

        val markdown = ReadingNoteFormatter().format(note)

        assertEquals(
            "- Tags: -",
            markdown.lines()[4],
        )
    }
}
