package io.github.shijianjs.kmphutool.core.date

import kotlinx.datetime.LocalDate

/**
 * A year plus a [Quarter], e.g. `2024 Q3`.
 *
 * Port of `cn.hutool.core.date.YearQuarter` on kotlinx-datetime. `YearMonth`, `java.util.Date`
 * and `Calendar` factories/accessors are not ported; [firstDate] and [lastDate] replace them.
 *
 * @hutool cn.hutool.core.date.YearQuarter
 */
data class YearQuarter(val year: Int, val quarter: Quarter) : Comparable<YearQuarter> {
    init {
        require(year in -999_999_999..999_999_999) { "Invalid value for Year: $year" }
    }

    /** First day of the quarter. */
    val firstDate: LocalDate
        get() = LocalDate(year, quarter.firstMonthValue(), 1)

    /** Last day of the quarter. */
    val lastDate: LocalDate
        get() = LocalDate(year, quarter.lastMonthValue(), quarter.lastMonth().getLastDay(isLeapYear(year)))

    /** Quarter value, `1..4`. */
    fun getQuarterValue(): Int = quarter.getValue()

    /** First month of the quarter. */
    fun firstMonth(): Month = quarter.firstMonth()

    /** One-based number of the quarter's first month. */
    fun firstMonthValue(): Int = quarter.firstMonthValue()

    /** Last month of the quarter. */
    fun lastMonth(): Month = quarter.lastMonth()

    /** One-based number of the quarter's last month. */
    fun lastMonthValue(): Int = quarter.lastMonthValue()

    /** This quarter moved by [quartersToAdd]. */
    fun plusQuarters(quartersToAdd: Long): YearQuarter {
        if (quartersToAdd == 0L) return this
        val quarterCount = year * 4L + (quarter.getValue() - 1)
        val calcQuarters = quarterCount + quartersToAdd
        val newYear = floorDiv(calcQuarters, 4L).toInt()
        val newQuarter = floorMod(calcQuarters, 4L).toInt() + 1
        return YearQuarter(newYear, Quarter.of(newQuarter)!!)
    }

    /** This quarter moved back by [quartersToMinus]. */
    fun minusQuarters(quartersToMinus: Long): YearQuarter = plusQuarters(-quartersToMinus)

    fun nextQuarter(): YearQuarter = plusQuarters(1L)

    fun lastQuarter(): YearQuarter = minusQuarters(1L)

    /** The same quarter [yearsToAdd] years later. */
    fun plusYears(yearsToAdd: Long): YearQuarter {
        if (yearsToAdd == 0L) return this
        return YearQuarter((year + yearsToAdd).toInt(), quarter)
    }

    fun minusYears(yearsToMinus: Long): YearQuarter = plusYears(-yearsToMinus)

    fun nextYear(): YearQuarter = plusYears(1L)

    fun lastYear(): YearQuarter = minusYears(1L)

    fun isBefore(other: YearQuarter): Boolean = compareTo(other) < 0

    fun isAfter(other: YearQuarter): Boolean = compareTo(other) > 0

    override fun compareTo(other: YearQuarter): Int {
        val cmp = year - other.year
        return if (cmp == 0) quarter.compareTo(other.quarter) else cmp
    }

    override fun toString(): String = "$year ${quarter.name}"

    companion object {
        /** Builds a year-quarter from a one-based quarter [quarter]. */
        fun of(year: Int, quarter: Int): YearQuarter {
            require(quarter in 1..4) { "Invalid value for Quarter: $quarter" }
            return YearQuarter(year, Quarter.of(quarter)!!)
        }

        /** Builds a year-quarter from a year and a [Quarter]. */
        fun of(year: Int, quarter: Quarter): YearQuarter = YearQuarter(year, quarter)

        /** The quarter containing [date]. */
        fun of(date: LocalDate): YearQuarter = YearQuarter(date.year, Quarter.fromMonth(date.monthNumber))

        private fun floorDiv(x: Long, y: Long): Long {
            val q = x / y
            return if (x % y != 0L && (x < 0) != (y < 0)) q - 1 else q
        }

        private fun floorMod(x: Long, y: Long): Long = x - floorDiv(x, y) * y

        private fun isLeapYear(year: Int): Boolean =
            (year % 4 == 0 && year % 100 != 0) || year % 400 == 0
    }
}
