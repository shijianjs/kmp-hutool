package io.github.shijianjs.kmphutool.core.date.chinese

/**
 * Names and leap-month helpers for lunar months.
 *
 * Port of `cn.hutool.core.date.chinese.ChineseMonth`.
 *
 * @hutool cn.hutool.core.date.chinese.ChineseMonth
 */
object ChineseMonth {
    private val MONTH_NAME = arrayOf("一", "二", "三", "四", "五", "六", "七", "八", "九", "十", "十一", "十二")
    private val MONTH_NAME_TRADITIONAL = arrayOf("正", "二", "三", "四", "五", "六", "七", "八", "九", "寒", "冬", "腊")

    /** Whether [month] is the leap month of lunar year [year]. */
    fun isLeapMonth(year: Int, month: Int): Boolean = month == LunarInfo.leapMonth(year)

    /** Chinese month name, e.g. `闰正月` or `十二月`. */
    fun getChineseMonthName(isLeapMonth: Boolean, month: Int, isTraditional: Boolean): String {
        require(month in 1..12) { "Invalid lunar month: $month" }
        val names = if (isTraditional) MONTH_NAME_TRADITIONAL else MONTH_NAME
        return (if (isLeapMonth) "闰" else "") + names[month - 1] + "月"
    }
}
