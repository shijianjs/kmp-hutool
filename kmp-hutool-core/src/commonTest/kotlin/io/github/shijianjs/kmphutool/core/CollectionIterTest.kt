package io.github.shijianjs.kmphutool.core

import io.github.shijianjs.kmphutool.core.collection.ArrayIter
import io.github.shijianjs.kmphutool.core.collection.ComputeIter
import io.github.shijianjs.kmphutool.core.collection.CopiedIter
import io.github.shijianjs.kmphutool.core.collection.FilterIter
import io.github.shijianjs.kmphutool.core.collection.IterChain
import io.github.shijianjs.kmphutool.core.collection.PartitionIter
import io.github.shijianjs.kmphutool.core.collection.TransCollection
import io.github.shijianjs.kmphutool.core.lang.Filter
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class CollectionIterTest {
    @Test
    fun arrayIterWindowsAndResets() {
        val iter = ArrayIter(arrayOf("a", "b", "c", "d"), 1, 3)
        assertEquals(listOf("b", "c"), iter.asSequence().toList())
        iter.reset()
        assertEquals("b", iter.next())
    }

    @Test
    fun computeIterCachesAndFinishes() {
        var counter = 0
        val iter = object : ComputeIter<Int>() {
            override fun computeNext(): Int? = if (counter < 3) ++counter else null
        }
        assertEquals(listOf(1, 2, 3), iter.asSequence().toList())
        assertFalse(iter.hasNext())
    }

    @Test
    fun filterIterSkipsRejectedElements() {
        val even = object : Filter<Int> {
            override fun accept(t: Int): Boolean = t % 2 == 0
        }
        val iter = FilterIter(listOf(1, 2, 3, 4).iterator(), even)
        assertEquals(listOf(2, 4), iter.asSequence().toList())
    }

    @Test
    fun iterChainWalksEverySource() {
        val chain = IterChain(listOf(1, 2).iterator(), listOf(3).iterator(), emptyList<Int>().iterator())
        assertEquals(listOf(1, 2, 3), (chain as Iterator<Int>).asSequence().toList())
    }

    @Test
    fun copiedIterIsIndependent() {
        val source = mutableListOf(1, 2, 3)
        val copied = CopiedIter(source.iterator())
        source.clear()
        assertEquals(listOf(1, 2, 3), copied.asSequence().toList())
    }

    @Test
    fun partitionIterBatches() {
        val batches = PartitionIter((1..5).iterator(), 2).asSequence().toList()
        assertEquals(listOf(listOf(1, 2), listOf(3, 4), listOf(5)), batches)
    }

    @Test
    fun transCollectionMapsLive() {
        val source = mutableListOf(1, 2, 3)
        val mapped = TransCollection(source) { it.toString() }
        assertEquals(listOf("1", "2", "3"), mapped.toList())
        source.add(4)
        assertEquals(4, mapped.size)
        assertTrue(mapped.removeIf { it == "4" })
        assertEquals(3, source.size)
    }
}

