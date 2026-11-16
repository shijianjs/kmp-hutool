package io.github.shijianjs.kmphutool.core.cron.pattern.matcher

import io.github.shijianjs.kmphutool.core.cron.pattern.Part

/**
 * Seven field matchers for one cron expression.
 *
 * Port of `cn.hutool.cron.pattern.matcher.PatternMatcher` (matching half). Upstream's
 * `nextMatchAfter` builds a `java.util.Calendar`; the KMP port leaves date advancement to
 * `CronPattern`/the caller.
 *
 * @hutool cn.hutool.cron.pattern.matcher.PatternMatcher
 */
class PatternMatcher(
    secondMatcher: PartMatcher,
    minuteMatcher: PartMatcher,
    hourMatcher: PartMatcher,
    dayOfMonthMatcher: PartMatcher,
    monthMatcher: PartMatcher,
    dayOfWeekMatcher: PartMatcher,
    yearMatcher: PartMatcher,
) {
    private val matchers = arrayOf(
        secondMatcher, minuteMatcher, hourMatcher, dayOfMonthMatcher,
        monthMatcher, dayOfWeekMatcher, yearMatcher,
    )

    fun get(part: Part): PartMatcher = matchers[part.ordinal]

    fun match(fields: IntArray): Boolean =
        match(fields[0], fields[1], fields[2], fields[3], fields[4], fields[5], fields[6])

    fun matchWeek(dayOfWeekValue: Int): Boolean = matchers[Part.DAY_OF_WEEK.ordinal].match(dayOfWeekValue)

    private fun match(
        second: Int,
        minute: Int,
        hour: Int,
        dayOfMonth: Int,
        month: Int,
        dayOfWeek: Int,
        year: Int,
    ): Boolean {
        val leap = isLeapYear(year)
        return (second < 0 || matchers[0].match(second)) &&
            matchers[1].match(minute) &&
            matchers[2].match(hour) &&
            matchDayOfMonth(matchers[3], dayOfMonth, month, leap) &&
            matchers[4].match(month) &&
            matchers[5].match(dayOfWeek) &&
            matchers[6].match(year)
    }

    private fun matchDayOfMonth(
        matcher: PartMatcher,
        dayOfMonth: Int,
        month: Int,
        isLeapYear: Boolean,
    ): Boolean = if (matcher is DayOfMonthMatcher) {
        matcher.match(dayOfMonth, month, isLeapYear)
    } else {
        matcher.match(dayOfMonth)
    }

    private fun isLeapYear(year: Int): Boolean =
        (year % 4 == 0 && year % 100 != 0) || year % 400 == 0
}
