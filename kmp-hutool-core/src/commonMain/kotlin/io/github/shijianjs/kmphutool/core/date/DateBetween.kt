package io.github.shijianjs.kmphutool.core.date

import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

/**
 * Difference between two instants.
 *
 * Port of `cn.hutool.core.date.DateBetween`. Hutool stores `java.util.Date`; here the endpoints are
 * `kotlinx.datetime.Instant`, and the month/year calculations interpret them in UTC because common
 * code has no ambient default time zone.
 *
 * @hutool cn.hutool.core.date.DateBetween
 */
class DateBetween(begin: Instant, end: Instant, isAbs: Boolean = true) {
    private val begin: Instant
    private val end: Instant

    init {
        if (isAbs && begin > end) {
            this.begin = end
            this.end = begin
        } else {
            this.begin = begin
            this.end = end
        }
    }

    /** Difference in [unit]s, truncated toward zero. */
    fun between(unit: DateUnit): Long =
        (end.toEpochMilliseconds() - begin.toEpochMilliseconds()) / unit.getMillis()

    /**
     * Difference in months. When [isReset] is false a partial month counts as one less.
     */
    fun betweenMonth(isReset: Boolean): Long {
        val b = begin.toLocalDateTime(TimeZone.UTC)
        val e = end.toLocalDateTime(TimeZone.UTC)
        var result = ((e.year - b.year) * 12 + (e.monthNumber - b.monthNumber)).toLong()
        if (!isReset) {
            val shifted = e.withYearMonth(b.year, b.monthNumber)
            if (shifted < b) result -= 1
        }
        return result
    }

    /** Difference in years. When [isReset] is false a partial year counts as one less. */
    fun betweenYear(isReset: Boolean): Long {
        val b = begin.toLocalDateTime(TimeZone.UTC)
        val e = end.toLocalDateTime(TimeZone.UTC)
        var result = (e.year - b.year).toLong()
        if (!isReset) {
            if (b.monthNumber < e.monthNumber) return result
            if (b.monthNumber > e.monthNumber) return result - 1
            if (b.monthNumber == 2 && isLastDayOfMonth(b) && isLastDayOfMonth(e)) {
                // Both are the last day of February: treat the months as equal.
                val shifted = e.withYearMonth(b.year, b.monthNumber, day = 1)
                if (shifted < b.withDay(1)) result -= 1
                return result
            }
            val shifted = e.withYearMonth(b.year, b.monthNumber)
            if (shifted < b) result -= 1
        }
        return result
    }

    fun toString(unit: DateUnit, level: BetweenFormatter.Level): String =
        BetweenFormatter(between(unit), level).format()

    fun toString(level: BetweenFormatter.Level): String =
        BetweenFormatter(end.toEpochMilliseconds() - begin.toEpochMilliseconds(), level).format()

    override fun toString(): String = toString(BetweenFormatter.Level.MILLISECOND)

    private fun LocalDateTime.withYearMonth(year: Int, month: Int, day: Int = dayOfMonth): LocalDateTime {
        val safeDay = minOf(day, LocalDate(year, month, 1).let { daysIn(it.year, it.monthNumber) })
        return LocalDateTime(year, month, safeDay, hour, minute, second, nanosecond)
    }

    private fun LocalDateTime.withDay(day: Int): LocalDateTime {
        val safeDay = minOf(day, daysIn(year, monthNumber))
        return LocalDateTime(year, monthNumber, safeDay, hour, minute, second, nanosecond)
    }

    private fun isLastDayOfMonth(dateTime: LocalDateTime): Boolean =
        dateTime.dayOfMonth == daysIn(dateTime.year, dateTime.monthNumber)

    private fun daysIn(year: Int, month: Int): Int = when (month) {
        1, 3, 5, 7, 8, 10, 12 -> 31
        4, 6, 9, 11 -> 30
        2 -> if (year % 4 == 0 && (year % 100 != 0 || year % 400 == 0)) 29 else 28
        else -> throw IllegalArgumentException("month out of range: $month")
    }
}
