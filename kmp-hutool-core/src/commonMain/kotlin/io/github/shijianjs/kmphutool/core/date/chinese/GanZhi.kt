package io.github.shijianjs.kmphutool.core.date.chinese

import kotlinx.datetime.LocalDate

/**
 * The ten heavenly stems and twelve earthly branches (干支).
 *
 * Port of `cn.hutool.core.date.chinese.GanZhi`.
 *
 * @hutool cn.hutool.core.date.chinese.GanZhi
 */
object GanZhi {
    private val GAN = arrayOf("甲", "乙", "丙", "丁", "戊", "己", "庚", "辛", "壬", "癸")
    private val ZHI = arrayOf("子", "丑", "寅", "卯", "辰", "巳", "午", "未", "申", "酉", "戌", "亥")

    /** The stem/branch pair for an offset where 0 is 甲子. */
    fun cyclicalm(num: Int): String = GAN[num % 10] + ZHI[num % 12]

    /** The stem/branch pair of lunar year [year]. */
    fun getGanzhiOfYear(year: Int): String = cyclicalm(year - LunarInfo.BASE_YEAR + 36)

    /** The stem/branch pair of the solar month containing the given Gregorian date. */
    fun getGanzhiOfMonth(year: Int, month: Int, day: Int): String {
        val firstNode = SolarTerms.getTerm(year, month * 2 - 1)
        var monthOffset = (year - LunarInfo.BASE_YEAR) * 12 + month + 11
        if (day >= firstNode) monthOffset++
        return cyclicalm(monthOffset)
    }

    /** The stem/branch pair of the given Gregorian day. */
    fun getGanzhiOfDay(year: Int, month: Int, day: Int): String {
        val days = LocalDate(year, month, day).toEpochDays() - 1
        return cyclicalm((days - LunarInfo.BASE_DAY + 41).toInt())
    }
}
