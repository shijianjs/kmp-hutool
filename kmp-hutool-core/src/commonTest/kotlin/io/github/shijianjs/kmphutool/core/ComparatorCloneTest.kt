package io.github.shijianjs.kmphutool.core

import io.github.shijianjs.kmphutool.core.clone.CloneRuntimeException
import io.github.shijianjs.kmphutool.core.clone.Cloneable
import io.github.shijianjs.kmphutool.core.comparator.ComparableComparator
import io.github.shijianjs.kmphutool.core.comparator.LengthComparator
import io.github.shijianjs.kmphutool.core.comparator.NullComparator
import io.github.shijianjs.kmphutool.core.comparator.ReverseComparator
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class ComparatorCloneTest {
    @Test
    fun nullOrdering() {
        val nullLast = NullComparator<String>(true, null)
        assertTrue(nullLast.compare(null, "a") > 0)
        assertTrue(nullLast.compare("a", null) < 0)
        assertEquals(0, nullLast.compare(null, null))
        assertTrue(nullLast.compare("a", "b") < 0)

        val nullFirst = NullComparator<String>(false, null)
        assertTrue(nullFirst.compare(null, "a") < 0)
    }

    @Test
    fun reverseAndNaturalOrder() {
        val reversed = ReverseComparator<Int>()
        assertTrue(reversed.compare(1, 2) > 0)
        assertTrue(reversed.compare(2, 1) < 0)
        assertEquals(0, reversed.compare(3, 3))

        val byLengthThenText = ReverseComparator<String>(LengthComparator)
        assertTrue(byLengthThenText.compare("a", "bb") > 0)
        assertTrue(ComparableComparator.compare("a", "b") < 0)
        assertEquals(1, LengthComparator.compare("bb", "a"))
        assertTrue(LengthComparator.compare("ab", "ba") < 0)
    }

    @Test
    fun cloneableContract() {
        class Point(val x: Int, val y: Int) : Cloneable<Point> {
            override fun clone(): Point = Point(x, y)
        }
        val original = Point(1, 2)
        val copy = original.clone()
        assertEquals(original.x, copy.x)
        assertTrue(original !== copy)

        val failure = CloneRuntimeException("nope")
        assertEquals("nope", failure.message)
    }
}
