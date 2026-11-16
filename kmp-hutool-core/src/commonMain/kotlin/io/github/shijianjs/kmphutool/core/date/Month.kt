package io.github.shijianjs.kmphutool.core.date

/**
 * Months with `java.util.Calendar` numbers (January = 0) plus `UNDECIMBER`.
 *
 * Port of `cn.hutool.core.date.Month`. `getDisplayName` is not ported because it needs
 * `java.time.format.TextStyle` and a JVM locale database.
 *
 * @hutool cn.hutool.core.date.Month
 */
enum class Month(private val value: Int) {
    JANUARY(0),
    FEBRUARY(1),
    MARCH(2),
    APRIL(3),
    MAY(4),
    JUNE(5),
    JULY(6),
    AUGUST(7),
    SEPTEMBER(8),
    OCTOBER(9),
    NOVEMBER(10),
    DECEMBER(11),

    /** A thirteenth month, used only by lunar calendars. */
    UNDECIMBER(12);

    /** The `Calendar` number, 0-based. */
    fun getValue(): Int = value

    /** The one-based month number, `1..12`. */
    fun getValueBaseOne(): Int {
        require(this != UNDECIMBER) { "Unsupported UNDECIMBER Field" }
        return value + 1
    }

    /** Last day of this month; `-1` for [UNDECIMBER]. */
    fun getLastDay(isLeapYear: Boolean): Int = when (this) {
        FEBRUARY -> if (isLeapYear) 29 else 28
        APRIL, JUNE, SEPTEMBER, NOVEMBER -> 30
        UNDECIMBER -> -1
        else -> 31
    }

    companion object {
        private val ALIASES = arrayOf("jan", "feb", "mar", "apr", "may", "jun", "jul", "aug", "sep", "oct", "nov", "dec")

        /** Maps a 0-based `Calendar` month number, or `null` when out of range. */
        fun of(calendarMonthIntValue: Int): Month? = entries.getOrNull(calendarMonthIntValue)

        /** Parses `jan`, `FEBRUARY` or `March`, case-insensitively. */
        fun of(name: String): Month {
            require(name.isNotBlank()) { "name must not be blank" }
            val aliasIndex = ALIASES.indexOfFirst { it.equals(name, ignoreCase = true) }
            if (aliasIndex >= 0) return entries[aliasIndex]
            return entries.first { it.name.equals(name, ignoreCase = true) }
        }

        /** Last day of a 0-based [month]. */
        fun getLastDay(month: Int, isLeapYear: Boolean): Int {
            val of = of(month) ?: throw IllegalArgumentException("Invalid Month base 0: $month")
            return of.getLastDay(isLeapYear)
        }
    }
}
