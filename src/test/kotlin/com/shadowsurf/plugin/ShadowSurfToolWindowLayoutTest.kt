package com.shadowsurf.plugin

import com.intellij.openapi.wm.ToolWindow
import java.lang.reflect.Proxy
import kotlin.test.Test
import kotlin.test.assertTrue

class ShadowSurfToolWindowLayoutTest {

    @Test
    fun shouldEnableSplitMode() {
        var splitModeEnabled = false
        val toolWindow = Proxy.newProxyInstance(
            ToolWindow::class.java.classLoader,
            arrayOf(ToolWindow::class.java),
        ) { _, method, args ->
            when (method.name) {
                "setSplitMode" -> {
                    splitModeEnabled = args?.get(0) == true
                    null
                }

                "isSplitMode" -> splitModeEnabled
                "hashCode" -> 0
                "equals" -> false
                "toString" -> "ToolWindowProxy"
                else -> when (method.returnType) {
                    Boolean::class.javaPrimitiveType -> false
                    Int::class.javaPrimitiveType -> 0
                    java.lang.Void.TYPE -> null
                    else -> null
                }
            }
        } as ToolWindow

        ShadowSurfToolWindowLayout.apply(toolWindow)

        assertTrue(splitModeEnabled)
    }
}
