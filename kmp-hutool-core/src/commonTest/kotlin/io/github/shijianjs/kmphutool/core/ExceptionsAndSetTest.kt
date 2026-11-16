package io.github.shijianjs.kmphutool.core

import io.github.shijianjs.kmphutool.core.collection.UniqueKeySet
import io.github.shijianjs.kmphutool.core.exceptions.DependencyException
import io.github.shijianjs.kmphutool.core.exceptions.NotInitedException
import io.github.shijianjs.kmphutool.core.exceptions.StatefulException
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class ExceptionsAndSetTest {
    @Test
    fun exceptionsFormatTemplates() {
        assertEquals("missing {}", DependencyException("missing {}").message)
        assertEquals("not ready 7", NotInitedException("not ready {}", 7).message)
        val stateful = StatefulException(503, "down")
        assertEquals(503, stateful.status)
        assertEquals("down", stateful.message)
    }

    @Test
    fun uniqueKeySetDeduplicatesByKey() {
        data class Item(val id: Int, val name: String)

        val set = UniqueKeySet<Int, Item>(uniqueGenerator = { it.id })
        assertTrue(set.add(Item(1, "a")))
        assertFalse(set.add(Item(1, "b")))
        assertEquals(1, set.size)

        assertFalse(set.addIfAbsent(Item(1, "c")))
        assertTrue(set.addIfAbsent(Item(2, "d")))
        assertEquals(2, set.size)

        assertTrue(set.remove(Item(1, "x")))
        assertEquals(1, set.size)
    }
}

