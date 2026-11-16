package io.github.shijianjs.kmphutool.core

import io.github.shijianjs.kmphutool.core.date.DateField
import io.github.shijianjs.kmphutool.core.date.DateUnit
import io.github.shijianjs.kmphutool.core.date.Month
import io.github.shijianjs.kmphutool.core.date.Quarter
import io.github.shijianjs.kmphutool.core.date.Week
import io.github.shijianjs.kmphutool.core.date.Zodiac
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class DateEnumTest {
    @Test
    fun unitsCarryMillis() {
        assertEquals(1000L, DateUnit.SECOND.getMillis())
        assertEquals(86400000L, DateUnit.DAY.getMillis())
    }

    @Test
    fun monthsAndWeeksRoundTrip() {
        assertEquals(Month.JANUARY, Month.of(0))
        assertEquals(Month.MARCH, Month.of("mar"))
        assertEquals(Month.DECEMBER, Month.of("December"))
        assertEquals(29, Month.FEBRUARY.getLastDay(true))
        assertEquals(Week.MONDAY, Week.of("星期一"))
        assertEquals(Week.SUNDAY, Week.of("sun"))
        assertEquals(7, Week.SUNDAY.getIso8601Value())
        assertEquals("星期一", Week.MONDAY.toChinese())
    }

    @Test
    fun quartersCoverMonths() {
        assertEquals(Quarter.Q1, Quarter.fromMonth(2))
        assertEquals(Quarter.Q4, Quarter.fromMonth(Month.DECEMBER))
        assertEquals(Month.JANUARY, Quarter.Q1.firstMonth())
        assertEquals(Month.MARCH, Quarter.Q1.lastMonth())
        assertEquals(Quarter.Q1, Quarter.Q4.plus(1))
    }

    @Test
    fun dateFieldsMatchCalendarNumbers() {
        assertEquals(5, DateField.DAY_OF_MONTH.getValue())
        assertEquals(DateField.MILLISECOND, DateField.of(14))
    }

    @Test
    fun zodiacSignsAndAnimals() {
        assertEquals("摩羯座", Zodiac.getZodiac(0, 1))
        assertEquals("水瓶座", Zodiac.getZodiac(0, 25))
        assertEquals("鼠", Zodiac.getChineseZodiac(1900))
        assertEquals(null, Zodiac.getChineseZodiac(1899))
        assertTrue(Zodiac.getZodiac(kotlinx.datetime.LocalDate(2020, 8, 1)).endsWith("座"))
    }
}
