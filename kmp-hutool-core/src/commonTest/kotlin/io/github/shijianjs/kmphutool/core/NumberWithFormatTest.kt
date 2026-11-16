package io.github.shijianjs.kmphutool.core

import io.github.shijianjs.kmphutool.core.convert.NumberWithFormat
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class NumberWithFormatTest {
    @Test
    fun exposesNumberAndFormat() {
        val value = NumberWithFormat(42, "yyyy-MM")
        assertEquals(42, value.toInt())
        assertEquals("42", value.toString())
        assertTrue(NumberWithFormat(1, null) < NumberWithFormat(2, null))
    }
}
