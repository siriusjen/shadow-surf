package com.shadowsurf.plugin.notes

import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.StandardOpenOption

enum class ReadingNoteWriteMode {
    APPEND,
    OVERWRITE,
}

class ReadingNoteWriter(
    private val targetFile: Path,
    private val formatter: ReadingNoteFormatter = ReadingNoteFormatter(),
) {

    constructor(
        targetFileProvider: () -> Path,
        formatter: ReadingNoteFormatter = ReadingNoteFormatter(),
    ) : this(targetFileProvider(), formatter)

    fun append(note: ReadingNote) {
        write(note, ReadingNoteWriteMode.APPEND)
    }

    fun write(note: ReadingNote, mode: ReadingNoteWriteMode = ReadingNoteWriteMode.APPEND) {
        val parent = targetFile.parent
        if (parent != null) {
            Files.createDirectories(parent)
        }
        val options = when (mode) {
            ReadingNoteWriteMode.APPEND -> arrayOf(StandardOpenOption.CREATE, StandardOpenOption.APPEND)
            ReadingNoteWriteMode.OVERWRITE -> arrayOf(
                StandardOpenOption.CREATE,
                StandardOpenOption.TRUNCATE_EXISTING,
                StandardOpenOption.WRITE,
            )
        }
        Files.writeString(
            targetFile,
            formatter.format(note),
            *options,
        )
    }
}
