package io.github.shijianjs.kmphutool.core

import io.github.shijianjs.kmphutool.core.map.TableMap
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class TableMapTest {
    @Test
    fun allowsDuplicateKeysAndReverseLookup() {
        val map = TableMap<String, String>()
        map["a"] = "1"
        map.put("a", "2")
        assertEquals("1", map["a"])
        assertEquals(listOf("1", "2"), map.getValues("a"))
        assertEquals("a", map.getKey("2"))
        assertEquals(listOf("a"), map.getKeys("2"))
        assertTrue(map.containsKey("a"))
        assertEquals(2, map.size)
    }
}
