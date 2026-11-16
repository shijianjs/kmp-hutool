package io.github.shijianjs.kmphutool.core.date

import kotlinx.datetime.LocalDate

/**
 * Western zodiac sign and Chinese zodiac animal.
 *
 * Port of `cn.hutool.core.date.Zodiac`. `java.util.Date`/`Calendar` overloads become
 * kotlinx-datetime [LocalDate].
 *
 * @hutool cn.hutool.core.date.Zodiac
 */
object Zodiac {
    private val DAY_ARR = intArrayOf(20, 19, 21, 20, 21, 22, 23, 23, 23, 24, 23, 22)
    private val ZODIACS = arrayOf(
        "摩羯座", "水瓶座", "双鱼座", "白羊座", "金牛座", "双子座", "巨蟹座",
        "狮子座", "处女座", "天秤座", "天蝎座", "射手座", "摩羯座",
    )
    private val CHINESE_ZODIACS = arrayOf("鼠", "牛", "虎", "兔", "龙", "蛇", "马", "羊", "猴", "鸡", "狗", "猪")

    /** The sign for [date]. */
    fun getZodiac(date: LocalDate): String = getZodiac(date.monthNumber, date.dayOfMonth)

    /** The sign for a 0-based [month] and [day]. */
    fun getZodiac(month: Month, day: Int): String = getZodiac(month.getValue(), day)

    /** The sign for a 0-based [month] and [day]. */
    fun getZodiac(month: Int, day: Int): String {
        require(month in 0..11) { "Unsupported month value, must be [0,11]" }
        return if (day < DAY_ARR[month]) ZODIACS[month] else ZODIACS[month + 1]
    }

    /** The Chinese zodiac animal for [date], using its Gregorian year as upstream does. */
    fun getChineseZodiac(date: LocalDate): String? = getChineseZodiac(date.year)

    /** The Chinese zodiac animal for [year], or `null` before 1900. */
    fun getChineseZodiac(year: Int): String? {
        if (year < 1900) return null
        return CHINESE_ZODIACS[(year - 1900) % CHINESE_ZODIACS.size]
    }
}
