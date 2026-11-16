package io.github.shijianjs.kmphutool.core

import io.github.shijianjs.kmphutool.core.cron.pattern.CronPattern
import io.github.shijianjs.kmphutool.core.cron.pattern.CronPatternBuilder
import io.github.shijianjs.kmphutool.core.cron.pattern.Part
import io.github.shijianjs.kmphutool.core.cron.pattern.matcher.BoolArrayMatcher
import io.github.shijianjs.kmphutool.core.cron.pattern.matcher.DayOfMonthMatcher
import io.github.shijianjs.kmphutool.core.cron.pattern.parser.PartParser
import io.github.shijianjs.kmphutool.core.cron.task.CronTask
import io.github.shijianjs.kmphutool.core.cron.task.Task
import kotlinx.datetime.LocalDateTime
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class CronTest {
    @Test
    fun partParserSupportsListsRangesAndSteps() {
        val minute = PartParser.of(Part.MINUTE).parse("3-18/5") as BoolArrayMatcher
        assertTrue(minute.match(3))
        assertTrue(minute.match(18))
        assertFalse(minute.match(19))

        val week = PartParser.of(Part.DAY_OF_WEEK).parse("mon-fri") as BoolArrayMatcher
        assertTrue(week.match(1))
        assertTrue(week.match(5))
        assertFalse(week.match(0))
    }

    @Test
    fun dayOfMonthUnderstandsLastDay() {
        val matcher = PartParser.of(Part.DAY_OF_MONTH).parse("L") as DayOfMonthMatcher
        assertTrue(matcher.match(31, 1, false))
        assertTrue(matcher.match(30, 4, false))
        assertFalse(matcher.match(30, 2, false))
        assertTrue(matcher.match(29, 2, true))
    }

    @Test
    fun cronPatternMatchesDateTime() {
        val everyFiveMinutes = CronPattern("*/5 * * * *")
        assertTrue(everyFiveMinutes.match(LocalDateTime(2026, 11, 17, 10, 5), false))
        assertFalse(everyFiveMinutes.match(LocalDateTime(2026, 11, 17, 10, 6), false))

        val tuesdayNoon = CronPattern("0 12 * * 2")
        assertTrue(tuesdayNoon.match(LocalDateTime(2026, 11, 17, 12, 0), false))
        assertFalse(tuesdayNoon.match(LocalDateTime(2026, 11, 17, 13, 0), false))
    }

    @Test
    fun cronPatternWithSecondsAndYear() {
        val pattern = CronPattern("30 0 12 17 11 * 2026")
        assertTrue(pattern.match(LocalDateTime(2026, 11, 17, 12, 0, 30), true))
        assertFalse(pattern.match(LocalDateTime(2027, 11, 17, 12, 0, 30), true))
    }

    @Test
    fun builderFillsDefaultFields() {
        assertEquals(
            "0-30 8,12 * * *",
            CronPatternBuilder.of()
                .setRange(Part.MINUTE, 0, 30)
                .setValues(Part.HOUR, 8, 12)
                .build(),
        )
        assertEquals(
            "5 * * * * *",
            CronPatternBuilder.of().setValues(Part.SECOND, 5).build(),
        )
    }

    @Test
    fun cronTaskKeepsIdAndPattern() {
        var ran = false
        val task = CronTask("t1", CronPattern("* * * * *"), Task { ran = true })
        task.execute()
        assertTrue(ran)
        assertEquals("t1", task.getId())
        assertEquals("* * * * *", task.pattern.toString())
    }
}
