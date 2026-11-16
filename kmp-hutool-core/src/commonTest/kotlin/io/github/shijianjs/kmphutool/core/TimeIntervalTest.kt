package io.github.shijianjs.kmphutool.core

import io.github.shijianjs.kmphutool.core.date.BetweenFormatter
import io.github.shijianjs.kmphutool.core.date.GroupTimeInterval
import io.github.shijianjs.kmphutool.core.date.TimeInterval
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class TimeIntervalTest {
    @Test
    fun formatsBetweenDurations() {
        assertEquals("1秒", BetweenFormatter(1_000L, BetweenFormatter.Level.SECOND).format())
        assertEquals("1分30秒", BetweenFormatter(90_000L, BetweenFormatter.Level.SECOND).format())
        assertEquals("1天2小时", BetweenFormatter(93_600_000L, BetweenFormatter.Level.HOUR).format())
        assertEquals("0毫秒", BetweenFormatter(0L, BetweenFormatter.Level.MILLISECOND).format())
    }

    @Test
    fun customSeparatorAndFormatter() {
        val formatter = BetweenFormatter(3_661_000L, BetweenFormatter.Level.SECOND)
            .setSeparator(" ")
            .setLevelFormatter { it.name.lowercase() }
        assertEquals("1hour 1minute 1second", formatter.format())
    }

    @Test
    fun groupTimersTrackSeparateIds() {
        val timer = GroupTimeInterval(false)
        timer.start("a")
        timer.start("b")
        assertTrue(timer.intervalMs("a") >= 0)
        assertTrue(timer.intervalMs("missing") == 0L)
        assertTrue(timer.intervalSecond("a") >= 0)
        assertTrue(timer.clear().intervalMs("a") == 0L)
    }

    @Test
    fun singleTimerStartsOnConstruction() {
        val timer = TimeInterval()
        assertTrue(timer.intervalMs() >= 0)
        assertTrue(timer.intervalPretty().isNotEmpty())
    }
}
