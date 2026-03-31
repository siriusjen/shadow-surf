package com.shadowsurf.plugin.browser

import java.util.UUID

data class BrowserTabState(
    val id: String,
    val title: String,
    val url: String,
)

class BrowserTabManager(private val maxTabs: Int = 5) {

    private val tabs = mutableListOf<BrowserTabState>()
    private var selectedIndex = -1

    fun tabs(): List<BrowserTabState> = tabs.toList()

    fun selectedTab(): BrowserTabState? = tabs.getOrNull(selectedIndex)

    fun openTab(url: String, title: String = url): BrowserTabState? {
        if (tabs.size >= maxTabs) {
            return null
        }
        val tab = BrowserTabState(
            id = UUID.randomUUID().toString(),
            title = title,
            url = url,
        )
        tabs += tab
        selectedIndex = tabs.lastIndex
        return tab
    }

    fun selectTab(tabId: String): BrowserTabState? {
        val index = tabs.indexOfFirst { it.id == tabId }
        if (index < 0) {
            return null
        }
        selectedIndex = index
        return tabs[index]
    }

    fun updateTab(tabId: String, title: String? = null, url: String? = null): BrowserTabState? {
        val index = tabs.indexOfFirst { it.id == tabId }
        if (index < 0) {
            return null
        }
        val current = tabs[index]
        val updated = current.copy(
            title = title ?: current.title,
            url = url ?: current.url,
        )
        tabs[index] = updated
        return updated
    }

    fun closeTab(tabId: String): BrowserTabState? {
        val index = tabs.indexOfFirst { it.id == tabId }
        if (index < 0) {
            return selectedTab()
        }
        tabs.removeAt(index)
        if (tabs.isEmpty()) {
            selectedIndex = -1
            return null
        }
        selectedIndex = when {
            index <= 0 -> 0
            index > tabs.lastIndex -> tabs.lastIndex
            else -> index - 1
        }
        return tabs[selectedIndex]
    }
}
