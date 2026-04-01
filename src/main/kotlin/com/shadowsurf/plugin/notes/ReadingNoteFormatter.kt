package com.shadowsurf.plugin.notes

class ReadingNoteFormatter {

    fun format(note: ReadingNote): String {
        val renderedTags = note.tags
            .map(String::trim)
            .filter(String::isNotBlank)
            .joinToString(", ")
            .ifBlank { "-" }

        return buildString {
            appendLine("## ${note.timestamp}")
            appendLine()
            appendLine("- Title: ${note.pageTitle}")
            appendLine("- URL: ${note.pageUrl}")
            appendLine("- Tags: $renderedTags")
            appendLine()
            appendLine("> ${note.selectedText}")
            appendLine()
            appendLine("Note:")
            append(note.userNote)
        }
    }
}
