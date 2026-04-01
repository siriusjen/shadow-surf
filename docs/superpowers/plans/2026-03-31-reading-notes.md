# Reading Notes Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Add a low-profile reading note flow to ShadowSurf so users can select text in the embedded browser, trigger a compact note bar with a shortcut, and append the excerpt plus metadata into a configurable local Markdown file.

**Architecture:** Keep the feature inside the existing Tool Window flow. Add a small persistent settings state for note path configuration, a note-writing service that owns Markdown formatting and file writes, and a browser-to-panel selection bridge that fetches selected text from JCEF only when the user explicitly triggers note capture. Keep the UI minimal: a compact note bar layered into `ShadowSurfPanel`, not a full note manager.

**Tech Stack:** Kotlin, IntelliJ Platform persistent state APIs, Swing UI, JBCef/JCEF JavaScript execution, local file I/O, Kotlin tests.

---

### Task 1: Add note settings and storage path resolution

**Files:**
- Create: `src/main/kotlin/com/shadowsurf/plugin/settings/ReadingNotesSettings.kt`
- Create: `src/main/kotlin/com/shadowsurf/plugin/settings/ReadingNotesConfigurable.kt`
- Modify: `src/main/resources/META-INF/plugin.xml`

- [ ] **Step 1: Add persistent settings state**

Create `ReadingNotesSettings.kt` as a small `PersistentStateComponent` that stores:
- `notesDirectory`
- `notesFileName`

Use defaults matching the approved spec:
- `~/Documents/ShadowSurf`
- `reading-notes.md`

Also add a helper that resolves the final target path from both fields.

- [ ] **Step 2: Add a minimal settings UI**

Create `ReadingNotesConfigurable.kt` with only three rows:
- editable `Notes Directory`
- editable `Notes File Name`
- read-only `Resolved Path`

Keep the form simple and native to IntelliJ settings pages.

- [ ] **Step 3: Register the settings page**

Update `plugin.xml` to register the configurable under the plugin settings so the user can change only directory and filename, without introducing broader feature settings.

- [ ] **Step 4: Add path-resolution tests**

Add a focused test that verifies default values and resolved path generation for custom directory + filename inputs.

### Task 2: Add a Markdown note writer service

**Files:**
- Create: `src/main/kotlin/com/shadowsurf/plugin/notes/ReadingNote.kt`
- Create: `src/main/kotlin/com/shadowsurf/plugin/notes/ReadingNoteFormatter.kt`
- Create: `src/main/kotlin/com/shadowsurf/plugin/notes/ReadingNoteWriter.kt`
- Create: `src/test/kotlin/com/shadowsurf/plugin/notes/ReadingNoteFormatterTest.kt`
- Create: `src/test/kotlin/com/shadowsurf/plugin/notes/ReadingNoteWriterTest.kt`

- [ ] **Step 1: Define the note data model**

Create `ReadingNote.kt` containing the exact fields the spec requires:
- timestamp
- page title
- page URL
- selected text
- user note
- optional tags

- [ ] **Step 2: Format a note into Markdown**

Create `ReadingNoteFormatter.kt` that renders one note block in the agreed structure:
- `## <timestamp>`
- `Title`
- `URL`
- `Tags`
- blockquote excerpt
- `Note:` section

Keep formatting deterministic so tests can assert exact output.

- [ ] **Step 3: Add a file writer that creates directories automatically**

Create `ReadingNoteWriter.kt` that:
- resolves the configured path from `ReadingNotesSettings`
- creates parent directories if missing
- appends new note blocks to the target Markdown file

Keep writing logic isolated from the UI.

- [ ] **Step 4: Add formatter and writer tests**

Add tests for:
- correct Markdown output
- tag rendering when empty vs non-empty
- file creation when directory does not exist
- append behavior when file already exists

### Task 3: Add selected-text capture from the embedded browser

**Files:**
- Create: `src/main/kotlin/com/shadowsurf/plugin/browser/SelectionCaptureBridge.kt`
- Modify: `src/main/kotlin/com/shadowsurf/plugin/ui/ShadowSurfPanel.kt`

- [ ] **Step 1: Add a JCEF selection bridge**

Create `SelectionCaptureBridge.kt` that executes JavaScript against the current page only when invoked. It should fetch the current text selection from the DOM and return it back to the panel callback.

- [ ] **Step 2: Keep capture explicit, not automatic**

Do not listen continuously for selection changes. Wire the bridge so selection is queried only after the user triggers the note action, preserving the low-profile reading flow.

- [ ] **Step 3: Handle empty selection gracefully**

If no text is selected:
- do not open the note bar
- show a minimal, non-disruptive message to the user

- [ ] **Step 4: Cover bridge fallback behavior**

Add a small test or isolated verification helper for the “empty selection / unsupported capture result” handling path, even if raw JCEF execution itself is not unit-tested.

### Task 4: Add the compact note bar UI and shortcut-driven flow

**Files:**
- Create: `src/main/kotlin/com/shadowsurf/plugin/ui/ReadingNoteBar.kt`
- Modify: `src/main/kotlin/com/shadowsurf/plugin/ui/ShadowSurfPanel.kt`

- [ ] **Step 1: Build the compact note bar component**

Create `ReadingNoteBar.kt` as a small Swing component with only:
- excerpt preview label or text area (read-only)
- note input field
- tag input field
- save button
- cancel button

Keep the visual footprint tight and suitable for the current bottom-right usage pattern.

- [ ] **Step 2: Mount the note bar inside the existing panel**

Modify `ShadowSurfPanel.kt` so the note bar is hidden by default and can be shown inline without replacing the browser area or opening a new window.

- [ ] **Step 3: Add the trigger action and shortcut binding**

Add a shortcut-driven action scoped to ShadowSurf that:
- queries current selected text through `SelectionCaptureBridge`
- captures current page title + URL from the selected tab/browser
- opens the compact note bar with prefilled excerpt metadata

Keep the shortcut implementation minimal and local to this feature.

- [ ] **Step 4: Save and dismiss in one flow**

When the user saves:
- build a `ReadingNote`
- call `ReadingNoteWriter`
- clear the form
- hide the note bar

When cancelled:
- clear the form
- hide the note bar

- [ ] **Step 5: Add focused UI-state tests where practical**

Add tests for panel-level state transitions that do not require full JCEF runtime:
- note bar hidden by default
- note bar resets after save/cancel
- save path is not executed when excerpt is blank

### Task 5: Verify behavior and document usage

**Files:**
- Modify: `README.md`
- Modify: `CHANGELOG.md`

- [ ] **Step 1: Update user-facing docs**

Add a short section describing:
- note capture shortcut
- what gets saved
- where to configure directory and filename

- [ ] **Step 2: Run focused tests first**

Run note-related tests before broader verification.
Suggested commands:
- `./gradlew test --tests '*ReadingNote*' --stacktrace`
- `./gradlew test --tests '*BrowserTabManagerTest' --stacktrace`

- [ ] **Step 3: Run full project verification**

Run:
- `./gradlew test --stacktrace`
- `./gradlew buildPlugin --stacktrace`

- [ ] **Step 4: Manually verify the reading-note flow in the sandbox**

Run:
- `./gradlew runIde --stacktrace`

Then verify inside the sandbox:
- open `ShadowSurf`
- load a readable article page
- select text
- trigger the note shortcut
- confirm the compact note bar appears
- save a note with and without tags
- confirm the Markdown file is created at the configured path
- confirm cancel hides the bar without writing
- confirm empty selection does not open the note bar
