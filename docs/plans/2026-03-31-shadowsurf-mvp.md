# ShadowSurf Implementation Plan

> **For Claude:** REQUIRED SUB-SKILL: Use superpowers:executing-plans to implement this plan task-by-task.

**Goal:** Build a standalone IntelliJ IDEA plugin named `ShadowSurf` that provides a lightweight in-IDE browser tool window with basic navigation, a small tab set, IDEA-theme-aware UI, and dark-page enhancement.

**Architecture:** Use IntelliJ Platform plugin APIs with a `ToolWindowFactory` as the host shell and `JBCefBrowser` for page rendering. Keep state minimal: a small in-memory tab model, Swing-based controls styled with IDEA theme APIs, and a simple JS/CSS injection layer to darken pages when enabled.

**Tech Stack:** Kotlin, Gradle, IntelliJ Platform plugin SDK, JCEF (`JBCefBrowser`), Swing UI.

---

## Approved Design

- Project name: `ShadowSurf`
- Runtime form: IntelliJ IDEA plugin shown in a tool window
- Browser engine: `JBCefBrowser`
- MVP features only: URL input, open, back, forward, refresh, 3-5 practical tabs, follow IDEA theme, webpage darkening toggle
- Out of scope: downloads, bookmark system, login syncing, devtools, full browser behavior, perfect dark conversion for all sites

## Acceptance Criteria

1. Plugin builds successfully with Gradle in the new standalone project.
2. Running the plugin sandbox shows a `ShadowSurf` tool window.
3. The tool window can open a URL and execute back, forward, and refresh.
4. Multiple tabs can be created, switched, and closed within the intended lightweight limit.
5. Plugin chrome follows IDEA theme changes without obvious hardcoded light colors.
6. Dark enhancement can be toggled and attempts page darkening through injected CSS/JS.
7. When JCEF is unavailable, the UI shows a clear unsupported message instead of failing.

### Task 1: Scaffold plugin project

**Files:**
- Create: `settings.gradle.kts`
- Create: `build.gradle.kts`
- Create: `gradle.properties`
- Create: `src/main/resources/META-INF/plugin.xml`
- Create: `src/main/kotlin/...` base package files

**Steps:**
1. Create the Gradle Kotlin project skeleton for an IntelliJ plugin.
2. Configure plugin metadata, IntelliJ target version, Kotlin, and sandbox tasks.
3. Add the minimum package structure for the tool window feature.
4. Run Gradle help or tasks to verify the scaffold is valid.

### Task 2: Build the browser tool window shell

**Files:**
- Create: `src/main/kotlin/com/shadowsurf/ShadowSurfToolWindowFactory.kt`
- Create: `src/main/kotlin/com/shadowsurf/ui/ShadowSurfPanel.kt`

**Steps:**
1. Register the tool window in `plugin.xml`.
2. Create the main panel with toolbar and browser host area.
3. Wire URL loading and navigation actions.
4. Show a fallback panel when `JBCefApp.isSupported()` is false.
5. Build the project to verify compilation.

### Task 3: Add lightweight tabs

**Files:**
- Create: `src/main/kotlin/com/shadowsurf/browser/BrowserTab.kt`
- Create: `src/main/kotlin/com/shadowsurf/browser/BrowserTabManager.kt`
- Modify: `src/main/kotlin/com/shadowsurf/ui/ShadowSurfPanel.kt`

**Steps:**
1. Add a small in-memory tab model.
2. Support tab create, select, rename from page title, and close.
3. Keep the UI simple and cap behavior to a lightweight use case.
4. Rebuild to verify no integration errors.

### Task 4: Add theme sync and page darkening

**Files:**
- Create: `src/main/kotlin/com/shadowsurf/theme/ThemeSyncService.kt`
- Create: `src/main/kotlin/com/shadowsurf/browser/DarkModeInjector.kt`
- Modify: `src/main/kotlin/com/shadowsurf/ui/ShadowSurfPanel.kt`

**Steps:**
1. Make toolbar, tabs, and fields use theme-aware colors.
2. Listen for IDEA LookAndFeel changes and refresh plugin chrome.
3. Add a dark enhancement toggle.
4. Inject minimal CSS/JS after page load to darken common backgrounds and text.
5. Rebuild and verify the injection path executes without compilation issues.

### Task 5: Verify manually and package

**Files:**
- Modify: project build files as needed for packaging

**Steps:**
1. Run Gradle build tasks.
2. Run the plugin sandbox.
3. Open a few pages and verify tabs, nav actions, theme sync, and dark enhancement behavior.
4. Package the plugin artifact.
5. Record any known limitations discovered during manual QA.
