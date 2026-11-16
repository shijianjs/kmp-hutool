package io.github.shijianjs.kmphutool.core

import io.github.shijianjs.kmphutool.core.map.FixedLinkedHashMap
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlin.test.assertTrue

class FixedLinkedHashMapTest {
    @Test
    fun evictsLeastRecentlyUsed() {
        val map = FixedLinkedHashMap<String, Int>(2)
        map["a"] = 1
        map["b"] = 2
        // Touch "a" so "b" becomes the eldest.
        assertEquals(1, map["a"])
        map["c"] = 3
        assertEquals(2, map.size)
        assertNull(map["b"])
        assertEquals(3, map["c"])
    }

    @Test
    fun reportsEvictedEntryToListener() {
        val map = FixedLinkedHashMap<String, Int>(1)
        val evicted = mutableListOf<String>()
        map.removeListener = { evicted += it.key }
        map["a"] = 1
        map["b"] = 2
        assertEquals(listOf("a"), evicted)
        assertTrue(map.containsKey("b"))
    }
}
