package io.github.shijianjs.kmphutool.core

import io.github.shijianjs.kmphutool.core.math.Arrangement
import io.github.shijianjs.kmphutool.core.math.BitStatusUtil
import io.github.shijianjs.kmphutool.core.math.Combination
import kotlin.test.Test
import kotlin.test.assertContentEquals
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class MathCombinatoricsTest {
    @Test
    fun bitStatusAddHasRemove() {
        var states = BitStatusUtil.clear()
        states = BitStatusUtil.add(states, 2)
        states = BitStatusUtil.add(states, 4)
        assertTrue(BitStatusUtil.has(states, 2))
        assertTrue(BitStatusUtil.has(states, 4))
        assertFalse(BitStatusUtil.has(states, 8))
        assertEquals(2, BitStatusUtil.remove(states, 4))
        assertEquals(6, BitStatusUtil.remove(states, 8))
        assertFailsWith<IllegalArgumentException> { BitStatusUtil.add(1, 2) }
    }

    @Test
    fun combinationCountsAndSelects() {
        assertEquals(10L, Combination.count(5, 2))
        assertEquals(20L, Combination.count(6, 3))
        assertEquals(0L, Combination.count(3, 4))
        assertEquals(1L, Combination.count(3, 0))
        assertEquals(31L, Combination.countAll(5))

        val combination = Combination(arrayOf("1", "2", "3"))
        val pairs = combination.select(2)
        assertEquals(3, pairs.size)
        assertContentEquals(arrayOf("1", "2"), pairs[0])
        assertEquals(7, combination.selectAll().size)
    }

    @Test
    fun arrangementCountsAndSelects() {
        assertEquals(6L, Arrangement.count(3, 2))
        assertEquals(120L, Arrangement.count(5))
        assertEquals(15L, Arrangement.countAll(3))

        val arrangement = Arrangement(arrayOf("1", "2", "3"))
        val pairs = arrangement.select(2)
        assertEquals(6, pairs.size)
        assertContentEquals(arrayOf("1", "2"), pairs[0])

        val lazy = arrangement.iterate(2).toList()
        assertEquals(pairs.map { it.toList() }, lazy.map { it.toList() })
        assertEquals(listOf(emptyList<String>()), arrangement.iterate(0).map { it.toList() })
    }
}

