package com.shadowsurf.plugin.browser

import kotlin.test.Test
import kotlin.test.assertTrue

class SelectionCaptureBridgeTest {

    @Test
    fun shouldBuildSelectionScriptUsingInjectedQuery() {
        val script = SelectionCaptureBridge.buildCaptureScript("window.__capture")

        assertTrue(script.contains("window.getSelection"))
        assertTrue(script.contains("window.__capture"))
        assertTrue(script.contains("toString()"))
    }
}
