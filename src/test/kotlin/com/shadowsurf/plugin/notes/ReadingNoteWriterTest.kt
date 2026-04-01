package com.shadowsurf.plugin.notes

import java.nio.file.Files
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class ReadingNoteWriterTest {

    @Test
    fun shouldCreateMissingDirectoriesAndAppendNotes() {
        val tempDir = Files.createTempDirectory("shadowsurf-notes")
        val targetFile = tempDir.resolve("nested").resolve("reading-notes.md")
        val writer = ReadingNoteWriter(targetFile, ReadingNoteFormatter())

        val first = ReadingNote(
            timestamp = "2026-04-01 10:00",
            pageTitle = "First",
            pageUrl = "https://example.com/1",
            selectedText = "Excerpt 1",
            userNote = "Note 1",
            tags = listOf("tag1"),
        )
        val second = first.copy(
            timestamp = "2026-04-01 10:05",
            pageTitle = "Second",
            pageUrl = "https://example.com/2",
            selectedText = "Excerpt 2",
            userNote = "Note 2",
            tags = emptyList(),
        )

        writer.append(first)
        writer.append(second)

        assertTrue(Files.exists(targetFile))
        assertEquals(
            ReadingNoteFormatter().format(first) + ReadingNoteFormatter().format(second),
            Files.readString(targetFile),
        )
    }

    @Test
    fun shouldOverwriteExistingContentsWhenRequested() {
        val tempDir = Files.createTempDirectory("shadowsurf-notes-overwrite")
        val targetFile = tempDir.resolve("reading-notes.md")
        Files.writeString(targetFile, "old content")
        val writer = ReadingNoteWriter(targetFile, ReadingNoteFormatter())
        val note = ReadingNote(
            timestamp = "2026-04-01 10:10",
            pageTitle = "Overwrite",
            pageUrl = "https://example.com/overwrite",
            selectedText = "Excerpt",
            userNote = "Note",
            tags = listOf("tag"),
        )

        writer.write(note, ReadingNoteWriteMode.OVERWRITE)

        assertEquals(ReadingNoteFormatter().format(note), Files.readString(targetFile))
    }
}
