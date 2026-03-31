package com.shadowsurf.plugin.browser

import com.intellij.ui.jcef.JBCefBrowser

class DarkModeInjector {

    fun apply(browser: JBCefBrowser, enabled: Boolean) {
        val currentUrl = browser.cefBrowser.url ?: "about:blank"
        val script = if (enabled) ENABLE_SCRIPT else DISABLE_SCRIPT
        browser.cefBrowser.executeJavaScript(script, currentUrl, 0)
    }

    companion object {
        private const val STYLE_ID = "shadowsurf-dark-style"

        private val ENABLE_SCRIPT = """
            (function() {
              var existing = document.getElementById('$STYLE_ID');
              if (!existing) {
                existing = document.createElement('style');
                existing.id = '$STYLE_ID';
                existing.textContent = `
                  html, body {
                    background: #1e1f22 !important;
                    color: #d7dae0 !important;
                  }
                  body, div, section, article, main, aside, header, footer, nav {
                    background-color: transparent !important;
                  }
                  input, textarea, select, button {
                    background: #2b2d30 !important;
                    color: #d7dae0 !important;
                    border-color: #55585e !important;
                  }
                  a {
                    color: #7aa2f7 !important;
                  }
                `;
                document.documentElement.appendChild(existing);
              }
              document.documentElement.style.backgroundColor = '#1e1f22';
              document.body && (document.body.style.backgroundColor = '#1e1f22');
            })();
        """.trimIndent()

        private val DISABLE_SCRIPT = """
            (function() {
              var existing = document.getElementById('$STYLE_ID');
              if (existing && existing.parentNode) {
                existing.parentNode.removeChild(existing);
              }
              document.documentElement.style.removeProperty('background-color');
              document.body && document.body.style.removeProperty('background-color');
            })();
        """.trimIndent()
    }
}
