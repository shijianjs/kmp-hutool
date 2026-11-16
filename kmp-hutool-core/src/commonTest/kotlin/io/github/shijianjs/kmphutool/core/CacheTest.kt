package io.github.shijianjs.kmphutool.core

import io.github.shijianjs.kmphutool.core.cache.CacheUtil
import io.github.shijianjs.kmphutool.core.cache.FIFOCache
import io.github.shijianjs.kmphutool.core.cache.LFUCache
import io.github.shijianjs.kmphutool.core.cache.LRUCache
import io.github.shijianjs.kmphutool.core.cache.NoCache
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertTrue

class CacheTest {
    @Test
    fun fifoEvictsTheOldestEntry() {
        val removed = mutableListOf<String>()
        val cache = FIFOCache<String, String>(2).apply { setListener { key, _ -> removed.add(key) } }
        cache.put("a", "1")
        cache.put("b", "2")
        assertEquals("1", cache.get("a"))
        cache.put("c", "3")

        assertNull(cache.get("a"))
        assertEquals("2", cache.get("b"))
        assertEquals("3", cache.get("c"))
        assertEquals(listOf("a"), removed)
    }

    @Test
    fun lfuEvictsTheLeastFrequentlyUsedEntry() {
        val cache = LFUCache<String, String>(2)
        cache.put("a", "1")
        cache.put("b", "2")
        cache.get("a")
        cache.get("a")
        cache.put("c", "3")

        assertNull(cache.get("b"))
        assertEquals("1", cache.get("a"))
    }

    @Test
    fun lruKeepsTheMostRecentlyUsedEntry() {
        val cache = LRUCache<String, String>(2)
        cache.put("a", "1")
        cache.put("b", "2")
        cache.get("a")
        cache.put("c", "3")

        assertNull(cache.get("b"))
        assertEquals("1", cache.get("a"))
    }

    @Test
    fun timeoutAndNoCache() {
        val cache = FIFOCache<String, String>(2, timeout = 1)
        cache.put("a", "1")
        assertEquals(1, cache.timeout())

        val noCache = NoCache<String, String>()
        noCache.put("a", "1")
        assertTrue(noCache.isEmpty())
        assertFalse(noCache.containsKey("a"))
        assertEquals("fallback", noCache.get("a") { "fallback" })
        assertEquals(0, CacheUtil.newNoCache<String, String>().size())
    }
}
