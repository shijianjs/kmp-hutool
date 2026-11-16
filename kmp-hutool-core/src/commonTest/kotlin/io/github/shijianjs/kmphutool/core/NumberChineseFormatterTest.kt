package io.github.shijianjs.kmphutool.core

import io.github.shijianjs.kmphutool.core.convert.NumberChineseFormatter
import kotlin.test.Test
import kotlin.test.assertEquals

class NumberChineseFormatterTest {
    @Test
    fun formatsIntegers() {
        assertEquals("一百二十三", NumberChineseFormatter.format(123L, false))
        assertEquals("壹佰贰拾叁", NumberChineseFormatter.format(123L, true))
        assertEquals("负一百二十三", NumberChineseFormatter.format(-123L, false))
        assertEquals("零", NumberChineseFormatter.format(0L, false))
    }

    @Test
    fun formatsThousandAndSimple() {
        assertEquals("十", NumberChineseFormatter.formatThousand(10, false))
        assertEquals("十五", NumberChineseFormatter.formatThousand(15, false))
        assertEquals("5.55万", NumberChineseFormatter.formatSimple(55_500))
        assertEquals("-5.56万", NumberChineseFormatter.formatSimple(-55_555))
    }

    @Test
    fun formatsMoney() {
        assertEquals("壹佰贰拾叁元肆角伍分", NumberChineseFormatter.format(123.45, true, true))
        assertEquals("壹佰贰拾叁元整", NumberChineseFormatter.format(123.0, true, true))
    }

    @Test
    fun parsesChineseNumerals() {
        assertEquals(220, NumberChineseFormatter.chineseToNumber("二百二十"))
        assertEquals(1012, NumberChineseFormatter.chineseToNumber("一千零一十二"))
        assertEquals(12, NumberChineseFormatter.chineseToNumber("十二"))
        assertEquals("五", NumberChineseFormatter.numberCharToChinese('5', false))
    }
}
