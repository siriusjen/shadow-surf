package com.shadowsurf.plugin.browser

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull

class BrowserTabManagerTest {

    @Test
    fun shouldOpenAndSelectNewestTab() {
        val manager = BrowserTabManager(maxTabs = 5)

        val first = manager.openTab("https://example.com", "example")
        val second = manager.openTab("https://jetbrains.com", "jetbrains")

        assertNotNull(first)
        assertNotNull(second)
        assertEquals(2, manager.tabs().size)
        assertEquals(second.id, manager.selectedTab()?.id)
    }

    @Test
    fun shouldRejectTabsBeyondLimit() {
        val manager = BrowserTabManager(maxTabs = 2)

        manager.openTab("https://a.com", "a")
        manager.openTab("https://b.com", "b")
        val overflow = manager.openTab("https://c.com", "c")

        assertNull(overflow)
        assertEquals(2, manager.tabs().size)
    }

    @Test
    fun shouldSelectAdjacentLeftTabWhenClosingCurrentOne() {
        val manager = BrowserTabManager(maxTabs = 5)

        manager.openTab("https://a.com", "a")!!
        val second = manager.openTab("https://b.com", "b")!!
        val third = manager.openTab("https://c.com", "c")!!

        val selected = manager.closeTab(third.id)

        assertNotNull(selected)
        assertEquals(second.id, selected.id)
        assertEquals(second.id, manager.selectedTab()?.id)
    }

    @Test
    fun shouldUpdateTabMetadata() {
        val manager = BrowserTabManager(maxTabs = 5)
        val tab = manager.openTab("https://a.com", "a")!!

        val updated = manager.updateTab(tab.id, title = "updated", url = "https://updated.com")

        assertNotNull(updated)
        assertEquals("updated", updated.title)
        assertEquals("https://updated.com", updated.url)
    }
}
