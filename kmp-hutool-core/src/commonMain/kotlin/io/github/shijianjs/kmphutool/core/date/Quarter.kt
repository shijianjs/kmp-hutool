package io.github.shijianjs.kmphutool.core.date

/**
 * Calendar quarters.
 *
 * Port of `cn.hutool.core.date.Quarter`. `MonthDay`/`YearQuarter` helpers are not ported, because
 * `YearQuarter` needs `java.time`; the month numbers and quarter arithmetic are kept.
 *
 * @hutool cn.hutool.core.date.Quarter
 */
enum class Quarter(private val value: Int) {
    Q1(1),
    Q2(2),
    Q3(3),
    Q4(4);

    /** Quarter number, `1..4`. */
    fun getValue(): Int = value

    /** First month's one-based number. */
    fun firstMonthValue(): Int = value * 3 - 2

    /** Last month's one-based number. */
    fun lastMonthValue(): Int = value * 3

    /** First [Month] of the quarter. */
    fun firstMonth(): Month = Month.of(firstMonthValue() - 1)!!

    /** Last [Month] of the quarter. */
    fun lastMonth(): Month = Month.of(lastMonthValue() - 1)!!

    /** This quarter moved by [quarters]; positive moves forward. */
    fun plus(quarters: Int): Quarter {
        val amount = (quarters % 4) + 4
        return entries[(ordinal + amount) % 4]
    }

    /** Pairs this quarter with [year]. */
    fun atYear(year: Int): YearQuarter = YearQuarter.of(year, this)

    companion object {
        /** Maps `1..4`, or `null`. */
        fun of(intValue: Int): Quarter? = when (intValue) {
            1 -> Q1
            2 -> Q2
            3 -> Q3
            4 -> Q4
            else -> null
        }

        /** The quarter containing a one-based month. */
        fun fromMonth(monthValue: Int): Quarter {
            require(monthValue in 1..12) { "Invalid value for MonthOfYear: $monthValue" }
            return of((monthValue - 1) / 3 + 1)!!
        }

        /** The quarter containing [month]. */
        fun fromMonth(month: Month): Quarter = fromMonth(month.getValueBaseOne())

        /** Rejects values outside `1..4`. */
        fun checkValidIntValue(value: Int): Int {
            require(value in 1..4) { "Invalid value for Quarter: $value" }
            return value
        }
    }
}
