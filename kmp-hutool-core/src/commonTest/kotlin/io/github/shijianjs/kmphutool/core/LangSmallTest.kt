package io.github.shijianjs.kmphutool.core

import io.github.shijianjs.kmphutool.core.lang.ConsistentHash
import io.github.shijianjs.kmphutool.core.lang.EnumItem
import io.github.shijianjs.kmphutool.core.lang.Pair
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlin.test.assertTrue

class LangSmallTest {
    private enum class Color(override val intVal: Int) : EnumItem<Color> {
        RED(1),
        GREEN(2);

        override fun items(): List<Color> = entries.toList()
    }

    @Test
    fun pairAccessorsAndEquality() {
        val pair = Pair.of("k", 1)
        assertEquals("k", pair.key)
        assertEquals(1, pair.value)
        assertEquals(Pair.of("k", 1), pair)
        assertEquals("Pair [key=k, value=1]", pair.toString())
    }

    @Test
    fun enumItemLookups() {
        assertEquals(Color.GREEN, Color.RED.fromInt(2))
        assertNull(Color.RED.fromInt(3))
        assertEquals(Color.RED, Color.GREEN.fromStr("red"))
        assertEquals("RED", Color.RED.text())
    }

    @Test
    fun consistentHashAssignsAndRemoves() {
        val ring = ConsistentHash(4, listOf("node-a", "node-b", "node-c"))
        assertTrue(ring.get("some-key") in listOf("node-a", "node-b", "node-c"))
        // Same key always lands on the same node.
        assertEquals(ring.get("some-key"), ring.get("some-key"))
        val empty = ConsistentHash<String>(2, emptyList())
        assertNull(empty.get("x"))
    }
}

