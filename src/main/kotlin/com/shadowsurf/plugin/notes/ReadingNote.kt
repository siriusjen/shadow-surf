package com.shadowsurf.plugin.notes

data class ReadingNote(
    val timestamp: String,
    val pageTitle: String,
    val pageUrl: String,
    val selectedText: String,
    val userNote: String,
    val tags: List<String>,
)
