package com.shadowsurf.plugin.notes

class ReadingNoteComposer(
    private val timestampProvider: () -> String,
) {

    fun compose(
        context: ReadingNoteContext?,
        noteText: String,
        tagsText: String,
    ): ReadingNote? {
        val resolvedContext = context ?: return null
        if (resolvedContext.selectedText.isBlank()) {
            return null
        }

        return ReadingNote(
            timestamp = timestampProvider(),
            pageTitle = resolvedContext.pageTitle,
            pageUrl = resolvedContext.pageUrl,
            selectedText = resolvedContext.selectedText,
            userNote = noteText,
            tags = tagsText.split(",").map(String::trim).filter(String::isNotBlank),
        )
    }
}
