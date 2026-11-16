package io.github.shijianjs.kmphutool.core

import io.github.shijianjs.kmphutool.core.map.CamelCaseLinkedMap
import io.github.shijianjs.kmphutool.core.map.CamelCaseMap
import io.github.shijianjs.kmphutool.core.map.CaseInsensitiveLinkedMap
import io.github.shijianjs.kmphutool.core.map.FuncKeyMap
import io.github.shijianjs.kmphutool.core.map.FuncMap
import io.github.shijianjs.kmphutool.core.map.MapBuilder
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class MapFamilyTest {
    @Test
    fun camelCaseMapNormalisesKeys() {
        val map = CamelCaseMap<String, Int>()
        map["int_value"] = 1
        assertEquals(1, map["intValue"])
        assertEquals(1, map["int_value"])
        assertTrue(map.containsKey("INTVALUE") || map.containsKey("intValue"))

        val linked = CamelCaseLinkedMap<String, Int>(mapOf("int_value" to 2))
        assertEquals(2, linked["intValue"])
    }

    @Test
    fun funcMapTransformsKeysAndValues() {
        val keys = FuncKeyMap<String, Int>(keyFunc = { it.lowercase() })
        keys["A"] = 1
        assertEquals(1, keys["a"])
        assertEquals(setOf("a"), keys.keys)

        val values = FuncMap<Int, Int>(valueFunc = { it * 2 })
        values[1] = 5
        assertEquals(10, values[1])

        val removed = keys.remove("A")
        assertEquals(1, removed)
        assertFalse(keys.containsKey("a"))
    }

    @Test
    fun caseInsensitiveLinkedMapKeepsOrder() {
        val map = CaseInsensitiveLinkedMap<Int>()
        map["Foo"] = 1
        map["bar"] = 2
        assertEquals(1, map["foo"])
        assertEquals(2, map["BAR"])
        assertEquals(listOf("foo", "bar"), map.keys.toList())
    }

    @Test
    fun mapBuilderBuildsAndJoins() {
        val builder = MapBuilder<String, Int>(linkedMapOf())
            .put("a", 1)
            .put(false, "skip", 2)
            .put("b", 3)
        assertEquals(mapOf("a" to 1, "b" to 3), builder.build())
        assertEquals("a=1,b=3", builder.join(",", "="))
    }
}
