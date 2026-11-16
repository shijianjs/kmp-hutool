package io.github.shijianjs.kmphutool.core

import io.github.shijianjs.kmphutool.core.comparator.ComparatorChain
import io.github.shijianjs.kmphutool.core.comparator.IndexedComparator
import io.github.shijianjs.kmphutool.core.comparator.InstanceComparator
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class ComparatorChainTest {
    @Test
    fun indexOrderWins() {
        val comparator = IndexedComparator(false, arrayOf("b", "a"))
        assertTrue(comparator.compare("b", "a") < 0)
        assertTrue(comparator.compare("c", "a") < 0)
    }

    @Test
    fun instanceOrderUsesClassPosition() {
        val comparator = InstanceComparator<Any>(false, { it is String }, { it is Int })
        assertTrue(comparator.compare("x", 1) < 0)
        assertTrue(comparator.compare(1, "x") > 0)
    }

    @Test
    fun chainFallsThroughAndReverses() {
        val chain = ComparatorChain<String>(Comparator { a, b -> a.length.compareTo(b.length) }, false)
        chain.addComparator(Comparator { a, b -> a.compareTo(b) }, true)
        assertTrue(chain.compare("a", "bb") < 0)
        assertTrue(chain.compare("aa", "bb") > 0)
        assertEquals(2, chain.size())
        assertTrue(chain.isLocked())

        val locked = chain
        runCatching { locked.addComparator(Comparator { a, b -> 0 }) }
            .onSuccess { throw AssertionError("chain should be locked") }
            .onFailure { assertTrue(it is UnsupportedOperationException) }
    }

    @Test
    fun emptyChainFailsOnCompare() {
        val chain = ComparatorChain<String>()
        runCatching { chain.compare("a", "b") }
            .onSuccess { throw AssertionError("empty chain must fail") }
            .onFailure { assertTrue(it is UnsupportedOperationException) }
        assertFalse(chain.isLocked())
    }
}

