package com.shadowsurf.plugin

import com.intellij.openapi.wm.ToolWindow

object ShadowSurfToolWindowLayout {

    fun apply(toolWindow: ToolWindow) {
        toolWindow.setSplitMode(true, null)
    }
}
