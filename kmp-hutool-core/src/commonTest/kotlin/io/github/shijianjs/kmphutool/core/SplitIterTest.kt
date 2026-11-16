package io.github.shijianjs.kmphutool.core

import io.github.shijianjs.kmphutool.core.date.DateException
import io.github.shijianjs.kmphutool.core.text.finder.StrFinder
import io.github.shijianjs.kmphutool.core.text.split.SplitIter
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class SplitIterTest {
    @Test
    fun splitsLazilyWithLimitAndEmptyHandling() {
        val iter = SplitIter("a,b,,c", StrFinder(",", false), 0, false)
        assertEquals(listOf("a", "b", "", "c"), iter.toList(false))

        val limited = SplitIter("a,b,c", StrFinder(",", false), 2, false)
        assertEquals(listOf("a", "b,c"), limited.toList(false))

        val ignoring = SplitIter("a,b,,c", StrFinder(",", false), 0, true)
        assertEquals(listOf("a", "b", "c"), ignoring.toList(false))

        val trimmed = SplitIter(" a , b ", StrFinder(",", false), 0, false).toArray(true)
        assertEquals(listOf("a", "b"), trimmed.toList())
    }

    @Test
    fun dateExceptionFormatsMessages() {
        assertEquals("bad day 3", DateException("bad day {}", 3).message)
    }
}

