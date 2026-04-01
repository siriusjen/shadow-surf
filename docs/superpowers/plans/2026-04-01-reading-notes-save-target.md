# Reading Notes Save Target Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Add a visible `Save As...` entry point in the reading note bar and make the first `Save` choose a target file before later saves reuse it.

**Architecture:** Extend the existing reading-note flow rather than adding a new manager. Keep target-file memory inside `ReadingNotesSettings`, add write-mode support to the writer, and let `ShadowSurfPanel` coordinate chooser + confirmation dialogs.

**Tech Stack:** Kotlin, Swing UI, IntelliJ persistent state APIs, local file I/O, Kotlin tests.

---

### Task 1: Persist selected note target

**Files:**
- Modify: `src/main/kotlin/com/shadowsurf/plugin/settings/ReadingNotesSettings.kt`
- Modify: `src/test/kotlin/com/shadowsurf/plugin/settings/ReadingNotesSettingsTest.kt`

- [ ] Add state for whether the user has explicitly selected a note file.
- [ ] Add helper to update settings from a chosen file path.
- [ ] Test default false state and update behavior.

### Task 2: Support append and overwrite writes

**Files:**
- Modify: `src/main/kotlin/com/shadowsurf/plugin/notes/ReadingNoteWriter.kt`
- Modify: `src/test/kotlin/com/shadowsurf/plugin/notes/ReadingNoteWriterTest.kt`

- [ ] Add write mode support for append vs overwrite.
- [ ] Keep append as the default behavior.
- [ ] Test overwrite replacing old contents.

### Task 3: Add `Save As...` to the note bar

**Files:**
- Modify: `src/main/kotlin/com/shadowsurf/plugin/ui/ReadingNoteBar.kt`
- Modify: `src/test/kotlin/com/shadowsurf/plugin/ui/ReadingNoteBarTest.kt`

- [ ] Add a `Save As...` button beside the existing actions.
- [ ] Add a callback for `Save As...`.
- [ ] Test that the bar still resets cleanly and exposes the new action.

### Task 4: Wire first-save chooser and overwrite prompt

**Files:**
- Modify: `src/main/kotlin/com/shadowsurf/plugin/ui/ShadowSurfPanel.kt`

- [ ] On first `Save`, show a save-file chooser seeded from current settings.
- [ ] On `Save As...`, always show the chooser.
- [ ] If the chosen file exists, ask whether to append or overwrite, defaulting to append.
- [ ] After selection, save the chosen path into settings and write the note.

### Task 5: Verify and document

**Files:**
- Modify: `README.md`
- Modify: `CHANGELOG.md`

- [ ] Run focused note-related tests.
- [ ] Run `./gradlew test --stacktrace`.
- [ ] Run `./gradlew buildPlugin --stacktrace`.
- [ ] Update docs to describe first-save chooser and `Save As...`.
