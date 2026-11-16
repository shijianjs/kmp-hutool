package io.github.shijianjs.kmphutool.core

import io.github.shijianjs.kmphutool.core.convert.NumberWordFormatter
import kotlin.test.Test
import kotlin.test.assertEquals

class NumberWordFormatterTest {
    @Test
    fun spellsAmounts() {
        assertEquals(
            "ONE HUNDRED AND TWENTY THREE AND CENTS FORTY FIVE ONLY",
            NumberWordFormatter.format(123.45),
        )
        assertEquals("", NumberWordFormatter.format(null))
    }

    @Test
    fun formatsSimpleUnits() {
        assertEquals("2.1k", NumberWordFormatter.formatSimple(2100))
        assertEquals("438.43w", NumberWordFormatter.formatSimple(4_384_300))
        assertEquals("4.38m", NumberWordFormatter.formatSimple(4_384_300, false))
    }
}
