package io.github.shijianjs.kmphutool.core.date

import io.github.shijianjs.kmphutool.core.convert.NumberChineseFormatter
import io.github.shijianjs.kmphutool.core.date.chinese.ChineseMonth
import io.github.shijianjs.kmphutool.core.date.chinese.GanZhi
import io.github.shijianjs.kmphutool.core.date.chinese.LunarFestival
import io.github.shijianjs.kmphutool.core.date.chinese.LunarInfo
import io.github.shijianjs.kmphutool.core.date.chinese.SolarTerms
import kotlinx.datetime.LocalDate

/**
 * A lunar (Chinese) date that also knows its Gregorian counterpart.
 *
 * Port of `cn.hutool.core.date.ChineseDate` on kotlinx-datetime; `java.util.Date`/`Calendar`
 * accessors become [LocalDate].
 *
 * @hutool cn.hutool.core.date.ChineseDate
 */
class ChineseDate {
    /** Lunar year. */
    private val year: Int

    /** Lunar month; a leap N month is stored as N+1, like upstream. */
    private val month: Int

    /** Whether this month is the leap one. */
    private val isLeapMonth: Boolean

    /** Lunar day, 1-based. */
    private val day: Int

    private val gyear: Int
    private val gmonthBase1: Int
    private val gday: Int

    /** Builds from a Gregorian date. */
    constructor(localDate: LocalDate) {
        gyear = localDate.year
        gmonthBase1 = localDate.monthNumber
        gday = localDate.dayOfMonth

        var offset = (localDate.toEpochDays() - LunarInfo.BASE_DAY).toInt()
        var iYear = LunarInfo.BASE_YEAR
        while (iYear <= LunarInfo.MAX_YEAR) {
            val daysOfYear = LunarInfo.yearDays(iYear)
            if (offset < daysOfYear) break
            offset -= daysOfYear
            iYear++
        }
        year = iYear

        val leapMonth = LunarInfo.leapMonth(iYear)
        var m = 1
        var hasLeapMonth = false
        while (m < 13) {
            val daysOfMonth = if (leapMonth > 0 && m == leapMonth + 1) {
                hasLeapMonth = true
                LunarInfo.leapDays(year)
            } else {
                LunarInfo.monthDays(year, if (hasLeapMonth) m - 1 else m)
            }
            if (offset < daysOfMonth) break
            offset -= daysOfMonth
            m++
        }
        isLeapMonth = leapMonth > 0 && m == leapMonth + 1
        if (hasLeapMonth && !isLeapMonth) m--
        month = m
        day = offset + 1
    }

    /** Builds from a lunar date; [chineseMonth] is the leap month automatically when it matches. */
    constructor(chineseYear: Int, chineseMonth: Int, chineseDay: Int) : this(
        chineseYear,
        chineseMonth,
        chineseDay,
        chineseMonth == LunarInfo.leapMonth(chineseYear),
    )

    /** Builds from a lunar date, explicitly choosing the leap month. */
    constructor(chineseYear: Int, chineseMonth: Int, chineseDay: Int, isLeapMonthIn: Boolean) {
        val leap = isLeapMonthIn && chineseMonth == LunarInfo.leapMonth(chineseYear)
        day = chineseDay
        isLeapMonth = leap
        month = if (leap) chineseMonth + 1 else chineseMonth
        year = chineseYear
        val gregorian = lunarToSolar(chineseYear, chineseMonth, chineseDay, leap)
        gyear = gregorian?.year ?: -1
        gmonthBase1 = gregorian?.monthNumber ?: -1
        gday = gregorian?.dayOfMonth ?: -1
    }

    fun getChineseYear(): Int = year

    fun getGregorianYear(): Int = gyear

    /** Lunar month index; a leap N month is N+1. */
    fun getMonth(): Int = month

    /** Gregorian month, 1-based. */
    fun getGregorianMonthBase1(): Int = gmonthBase1

    /** Gregorian month, 0-based. */
    fun getGregorianMonth(): Int = gmonthBase1 - 1

    fun isLeapMonth(): Boolean = isLeapMonth

    /** Lunar month name without the traditional `正`/`腊` naming. */
    fun getChineseMonth(): String = getChineseMonth(false)

    /** Lunar month name with the traditional `正`/`腊` naming. */
    fun getChineseMonthName(): String = getChineseMonth(true)

    /** Lunar month name. */
    fun getChineseMonth(isTraditional: Boolean): String = ChineseMonth.getChineseMonthName(
        isLeapMonth,
        if (isLeapMonth) month - 1 else month,
        isTraditional,
    )

    fun getDay(): Int = day

    fun getGregorianDay(): Int = gday

    /** Lunar day name, e.g. `初五` or `廿三`. */
    fun getChineseDay(): String {
        val chineseTen = arrayOf("初", "十", "廿", "卅")
        val n = if (day % 10 == 0) 9 else day % 10 - 1
        if (day > 30) return ""
        return when (day) {
            10 -> "初十"
            20 -> "二十"
            30 -> "三十"
            else -> chineseTen[day / 10] + NumberChineseFormatter.format((n + 1).toLong(), false)
        }
    }

    /** The Gregorian [LocalDate]; dates outside the supported table give `null`. */
    fun getGregorianDate(): LocalDate? =
        if (gyear == -1) null else LocalDate(gyear, gmonthBase1, gday)

    /** Festivals on this lunar day, comma-separated. */
    fun getFestivals(): String =
        LunarFestival.getFestivals(year, if (isLeapMonth) month - 1 else month, day).joinToString(",")

    /** Chinese zodiac animal of the lunar year. */
    fun getChineseZodiac(): String? = Zodiac.getChineseZodiac(year)

    /** Stem/branch of the lunar year. */
    fun getCyclical(): String = GanZhi.getGanzhiOfYear(year)

    /** Stem/branch year, month and day of the Gregorian date, or `null`. */
    fun getCyclicalYMD(): String? {
        if (gyear < LunarInfo.BASE_YEAR || gmonthBase1 <= 0 || gday <= 0) return null
        return "${GanZhi.getGanzhiOfYear(year)}年${GanZhi.getGanzhiOfMonth(gyear, gmonthBase1, gday)}月" +
            "${GanZhi.getGanzhiOfDay(gyear, gmonthBase1, gday)}日"
    }

    /** Solar term on the Gregorian date, or an empty string. */
    fun getTerm(): String =
        if (gyear == -1) "" else SolarTerms.getTerm(gyear, gmonthBase1, gday)

    /** `YYYY-MM-DD` in lunar terms, without the leap marker. */
    fun toStringNormal(): String {
        val m = if (isLeapMonth) month - 1 else month
        return "${year.toString().padStart(4, '0')}-${m.toString().padStart(2, '0')}-${day.toString().padStart(2, '0')}"
    }

    override fun toString(): String = "${getCyclical()}${getChineseZodiac()}年${getChineseMonthName()}${getChineseDay()}"

    private fun lunarToSolar(chineseYear: Int, chineseMonth: Int, chineseDay: Int, leap: Boolean): LocalDate? {
        if (chineseYear < LunarInfo.BASE_YEAR || chineseYear > LunarInfo.MAX_YEAR) return null
        val monthDays = if (leap) LunarInfo.leapDays(chineseYear) else LunarInfo.monthDays(chineseYear, chineseMonth)
        if (chineseDay < 1 || chineseDay > monthDays) return null

        var offset = 0
        for (i in LunarInfo.BASE_YEAR until chineseYear) offset += LunarInfo.yearDays(i)
        var leapAdded = false
        val leapMonth = LunarInfo.leapMonth(chineseYear)
        for (i in 1 until chineseMonth) {
            if (!leapAdded && leapMonth in 1..i) {
                offset += LunarInfo.leapDays(chineseYear)
                leapAdded = true
            }
            offset += LunarInfo.monthDays(chineseYear, i)
        }
        if (leap) offset += monthDays
        return LocalDate.fromEpochDays(LunarInfo.BASE_DAY + offset + chineseDay - 1)
    }
}
