package io.github.shijianjs.kmphutool.core.date

/**
 * Fields of a date, with the numeric values `java.util.Calendar` uses.
 *
 * Port of `cn.hutool.core.date.DateField`. Calendar itself is JVM-only, so the values are spelled
 * out here; [of] still maps the same integers back to the enum.
 *
 * @hutool cn.hutool.core.date.DateField
 */
enum class DateField(private val value: Int) {
    /** Era. */
    ERA(0),

    /** Year. */
    YEAR(1),

    /** Month, 0-based. */
    MONTH(2),

    /** Week of year. */
    WEEK_OF_YEAR(3),

    /** Week of month. */
    WEEK_OF_MONTH(4),

    /** Day of month. */
    DAY_OF_MONTH(5),

    /** Day of year. */
    DAY_OF_YEAR(6),

    /** Day of week, 1 = Sunday. */
    DAY_OF_WEEK(7),

    /** Occurrence of the day within the month. */
    DAY_OF_WEEK_IN_MONTH(8),

    /** AM/PM. */
    AM_PM(9),

    /** Hour in the 12-hour clock. */
    HOUR(10),

    /** Hour in the 24-hour clock. */
    HOUR_OF_DAY(11),

    /** Minute. */
    MINUTE(12),

    /** Second. */
    SECOND(13),

    /** Millisecond. */
    MILLISECOND(14);

    /** The `Calendar`-compatible numeric value. */
    fun getValue(): Int = value

    companion object {
        /** Maps a `Calendar` field constant to its enum, or `null`. */
        fun of(calendarPartIntValue: Int): DateField? = entries.firstOrNull { it.value == calendarPartIntValue }
    }
}
