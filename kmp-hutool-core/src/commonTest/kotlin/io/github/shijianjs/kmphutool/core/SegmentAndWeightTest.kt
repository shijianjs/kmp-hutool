package io.github.shijianjs.kmphutool.core

import io.github.shijianjs.kmphutool.core.lang.DefaultSegment
import io.github.shijianjs.kmphutool.core.lang.WeightListRandom
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class SegmentAndWeightTest {
    @Test
    fun segmentLengthUsesAbsoluteDifference() {
        assertEquals(5.0, DefaultSegment(2, 7).length())
        assertEquals(5.0, DefaultSegment(7, 2).length())
    }

    @Test
    fun weightedPoolReturnsOnlyRegisteredElements() {
        val pool = WeightListRandom<String>()
        pool.add("a", 1.0)
        pool.add("b", 3.0)
        assertTrue(pool.next() in setOf("a", "b"))
        assertTrue(pool.remove("a"))
        assertEquals("b", pool.next())
        pool.remove("b")
        assertTrue(pool.isEmpty())
    }
}
