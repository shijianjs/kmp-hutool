package io.github.shijianjs.kmphutool.core

import io.github.shijianjs.kmphutool.core.date.ChineseDate
import io.github.shijianjs.kmphutool.core.date.chinese.ChineseMonth
import io.github.shijianjs.kmphutool.core.date.chinese.GanZhi
import io.github.shijianjs.kmphutool.core.date.chinese.LunarFestival
import io.github.shijianjs.kmphutool.core.date.chinese.LunarInfo
import io.github.shijianjs.kmphutool.core.date.chinese.SolarTerms
import kotlinx.datetime.LocalDate
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class LunarInfoTest {
    @Test
    fun leapMonthsAndYearLengths() {
        assertEquals(2, LunarInfo.leapMonth(2023))
        assertEquals(0, LunarInfo.leapMonth(2024))
        assertEquals(384, LunarInfo.yearDays(2023))
        assertEquals(354, LunarInfo.yearDays(2024))
        assertEquals(29, LunarInfo.monthDays(2023, 1))
        assertTrue(ChineseMonth.isLeapMonth(2023, 2))
    }

    @Test
    fun solarTermsAndStems() {
        assertEquals("立春", SolarTerms.getTerm(2024, 2, 4))
        assertEquals("立春", SolarTerms.getTerm(LocalDate(2024, 2, 4)))
        assertEquals("", SolarTerms.getTerm(2024, 2, 3))
        assertEquals("甲辰", GanZhi.getGanzhiOfYear(2024))
        assertEquals("甲子", GanZhi.cyclicalm(0))
        assertTrue(LunarFestival.getFestivals(1, 1).contains("春节"))
        assertEquals(2, LunarFestival.getFestivals(6, 6).size)
    }

    @Test
    fun convertsBetweenCalendars() {
        val newYear = ChineseDate(LocalDate(2024, 2, 10))
        assertEquals(2024, newYear.getChineseYear())
        assertEquals(1, newYear.getMonth())
        assertEquals(1, newYear.getDay())
        assertEquals("正月初一", newYear.getChineseMonthName() + newYear.getChineseDay())
        assertEquals(LocalDate(2024, 2, 10), ChineseDate(2024, 1, 1).getGregorianDate())
    }

    @Test
    fun monthNames() {
        assertEquals("正月", ChineseMonth.getChineseMonthName(false, 1, true))
        assertEquals("闰二月", ChineseMonth.getChineseMonthName(true, 2, false))
        assertEquals("腊月", ChineseMonth.getChineseMonthName(false, 12, true))
    }
}
