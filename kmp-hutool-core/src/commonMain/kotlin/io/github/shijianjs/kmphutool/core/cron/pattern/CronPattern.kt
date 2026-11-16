package io.github.shijianjs.kmphutool.core.cron.pattern

import io.github.shijianjs.kmphutool.core.cron.pattern.matcher.PatternMatcher
import io.github.shijianjs.kmphutool.core.cron.pattern.parser.PatternParser
import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

/**
 * A parsed cron expression.
 *
 * Port of `cn.hutool.cron.pattern.CronPattern`. Matching works on [LocalDateTime] /
 * [Instant], so no `java.util.Calendar` or `java.util.TimeZone` is involved. Upstream's
 * `Calendar`-based `nextMatchAfter` is intentionally not provided; a scheduler can advance the
 * clock itself and call [match].
 *
 * @hutool cn.hutool.cron.pattern.CronPattern
 */
class CronPattern(val pattern: String) {
    private val matchers: List<PatternMatcher> = PatternParser.parse(pattern)

    fun match(dateTime: LocalDateTime, isMatchSecond: Boolean): Boolean {
        val fields = intArrayOf(
            if (isMatchSecond) dateTime.second else 0,
            dateTime.minute,
            dateTime.hour,
            dateTime.dayOfMonth,
            dateTime.monthNumber,
            dayOfWeekBase0(dateTime),
            dateTime.year,
        )
        return matchers.any { it.match(fields) }
    }

    fun match(instant: Instant, timeZone: TimeZone, isMatchSecond: Boolean): Boolean =
        match(instant.toLocalDateTime(timeZone), isMatchSecond)

    fun match(millis: Long, isMatchSecond: Boolean): Boolean =
        match(Instant.fromEpochMilliseconds(millis), TimeZone.currentSystemDefault(), isMatchSecond)

    override fun toString(): String = pattern

    private fun dayOfWeekBase0(dateTime: LocalDateTime): Int {
        // 1970-01-01 was a Thursday; Sunday is 0 in cron expressions.
        val epochDays = dateTime.date.toEpochDays()
        return (((epochDays % 7) + 4) % 7).toInt()
    }

    companion object {
        fun of(pattern: String): CronPattern = CronPattern(pattern)
    }
}
