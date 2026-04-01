package com.shadowsurf.plugin.browser

import com.intellij.openapi.application.ApplicationManager
import com.intellij.ui.jcef.JBCefBrowser
import com.intellij.ui.jcef.JBCefJSQuery

class SelectionCaptureBridge(private val browser: JBCefBrowser) {

    private val query = JBCefJSQuery.create(browser)
    private var pendingCallback: ((String?) -> Unit)? = null

    init {
        query.addHandler { value ->
            val callback = pendingCallback
            pendingCallback = null
            ApplicationManager.getApplication().invokeLater {
                callback?.invoke(value.trim().ifBlank { null })
            }
            JBCefJSQuery.Response("")
        }
    }

    fun captureSelection(callback: (String?) -> Unit) {
        pendingCallback = callback
        val script = buildCaptureScript(query.inject("selectionText"))
        val currentUrl = browser.cefBrowser.url ?: "about:blank"
        browser.cefBrowser.executeJavaScript(script, currentUrl, 0)
    }

    fun dispose() {
        query.dispose()
    }

    companion object {
        fun buildCaptureScript(queryCall: String): String {
            return """
                (function() {
                  var selectionText = '';
                  if (window.getSelection) {
                    selectionText = window.getSelection().toString();
                  }
                  $queryCall;
                })();
            """.trimIndent()
        }
    }
}
