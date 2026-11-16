package io.github.shijianjs.kmphutool.core.cron.pattern.matcher

import io.github.shijianjs.kmphutool.core.date.Month

/**
 * Day-of-month matcher that understands month lengths, leap years and `L` (last day, encoded as 32).
 *
 * Port of `cn.hutool.cron.pattern.matcher.DayOfMonthMatcher`.
 *
 * @hutool cn.hutool.cron.pattern.matcher.DayOfMonthMatcher
 */
class DayOfMonthMatcher(intValueList: List<Int>) : BoolArrayMatcher(intValueList) {
    fun match(dayValue: Int, month: Int, isLeapYear: Boolean): Boolean =
        super.match(dayValue) || matchLastDay(dayValue, month, isLeapYear)

    fun nextAfter(dayValue: Int, month: Int, isLeapYear: Boolean): Int {
        val maxValue = getMaxValue(month, isLeapYear)
        val minValue = getMinValue(month, isLeapYear)
        if (dayValue > minValue) {
            var current = dayValue
            while (current <= maxValue) {
                if (bValues[current] || (current == maxValue && match(LAST_DAY))) return current
                current++
            }
        }
        return minValue
    }

    fun isLast(): Boolean = match(LAST_DAY)

    fun isLastDay(value: Int, month: Int, isLeapYear: Boolean): Boolean =
        matchLastDay(value, month, isLeapYear)

    fun getMinValue(month: Int, isLeapYear: Boolean): Int {
        val min = super.getMinValue()
        return if (min == LAST_DAY) getLastDay(month, isLeapYear) else min
    }

    fun getMaxValue(month: Int, isLeapYear: Boolean): Int =
        minOf(super.getMaxValue(), getLastDay(month, isLeapYear))

    private fun matchLastDay(dayValue: Int, month: Int, isLeapYear: Boolean): Boolean =
        dayValue > 27 && match(LAST_DAY) && dayValue == getLastDay(month, isLeapYear)

    private fun getLastDay(month: Int, isLeapYear: Boolean): Int =
        Month.getLastDay(month - 1, isLeapYear)

    companion object {
        private const val LAST_DAY = 32
    }
}
