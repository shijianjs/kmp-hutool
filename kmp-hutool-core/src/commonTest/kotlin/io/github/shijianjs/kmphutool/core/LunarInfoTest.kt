package io.github.shijianjs.kmphutool.core

import io.github.shijianjs.kmphutool.core.date.chinese.ChineseMonth
import io.github.shijianjs.kmphutool.core.date.chinese.LunarInfo
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
    fun monthNames() {
        assertEquals("正月", ChineseMonth.getChineseMonthName(false, 1, true))
        assertEquals("闰二月", ChineseMonth.getChineseMonthName(true, 2, false))
        assertEquals("腊月", ChineseMonth.getChineseMonthName(false, 12, true))
    }
}
