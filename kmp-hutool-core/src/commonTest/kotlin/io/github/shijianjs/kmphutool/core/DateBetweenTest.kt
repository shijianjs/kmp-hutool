package io.github.shijianjs.kmphutool.core

import io.github.shijianjs.kmphutool.core.date.BetweenFormatter
import io.github.shijianjs.kmphutool.core.date.DateBetween
import io.github.shijianjs.kmphutool.core.date.DateUnit
import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toInstant
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class DateBetweenTest {
    private fun instant(y: Int, m: Int, d: Int) =
        LocalDateTime(y, m, d, 0, 0, 0).toInstant(TimeZone.UTC)

    @Test
    fun countsUnitsAndMonths() {
        val between = DateBetween(instant(2024, 1, 1), instant(2024, 3, 1))
        assertEquals(60, between.between(DateUnit.DAY))
        assertEquals(2, between.betweenMonth(true))
        assertEquals(0, between.betweenYear(true))
        assertEquals("60天", between.toString(BetweenFormatter.Level.MILLISECOND))
    }

    @Test
    fun swapsWhenAbsolute() {
        val between = DateBetween(instant(2024, 3, 1), instant(2024, 1, 1))
        assertEquals(60, between.between(DateUnit.DAY))
        val signed = DateBetween(instant(2024, 3, 1), instant(2024, 1, 1), isAbs = false)
        assertEquals(-60, signed.between(DateUnit.DAY))
        assertTrue(signed.betweenMonth(true) < 0)
    }
}
