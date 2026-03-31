package com.shadowsurf.plugin

import com.intellij.openapi.project.Project
import com.intellij.openapi.util.Disposer
import com.intellij.ui.content.ContentFactory
import com.intellij.openapi.wm.ToolWindow
import com.intellij.openapi.wm.ToolWindowFactory
import com.shadowsurf.plugin.ui.ShadowSurfPanel

class ShadowSurfToolWindowFactory : ToolWindowFactory {

    override fun createToolWindowContent(project: Project, toolWindow: ToolWindow) {
        val panel = ShadowSurfPanel(project)
        val content = ContentFactory.getInstance().createContent(panel, "", false)
        toolWindow.contentManager.addContent(content)
        Disposer.register(toolWindow.disposable, panel)
    }

    override fun shouldBeAvailable(project: Project): Boolean = true
}
