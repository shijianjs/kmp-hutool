package io.github.shijianjs.kmphutool.core

import io.github.shijianjs.kmphutool.core.text.TextSimilarity
import io.github.shijianjs.kmphutool.core.util.BooleanUtil
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertTrue

class BooleanTextTest {
    @Test
    fun parseBooleanSpellings() {
        assertTrue(BooleanUtil.toBoolean(" TRUE "))
        assertTrue(BooleanUtil.toBoolean("开启"))
        assertFalse(BooleanUtil.toBoolean(""))
        assertFalse(BooleanUtil.toBoolean("maybe"))
        assertEquals(true, BooleanUtil.toBooleanObject("on"))
        assertEquals(false, BooleanUtil.toBooleanObject("错"))
        assertNull(BooleanUtil.toBooleanObject("maybe"))
    }

    @Test
    fun foldBooleans() {
        assertNull(BooleanUtil.negate(null))
        assertEquals(false, BooleanUtil.negate(true))
        assertTrue(BooleanUtil.and(true, true))
        assertFalse(BooleanUtil.and(true, false))
        assertTrue(BooleanUtil.or(false, false, true))
        assertFalse(BooleanUtil.orOfWrap(false, null))
        assertFalse(BooleanUtil.xor(true, false, true))
        assertTrue(BooleanUtil.exactlyOneTrue(false, true, false))
        assertFalse(BooleanUtil.exactlyOneTrue(true, true))
    }

    @Test
    fun renderBooleans() {
        assertEquals("on", BooleanUtil.toStringOnOff(true))
        assertEquals("no", BooleanUtil.toStringYesNo(false))
        assertEquals("yes", BooleanUtil.toString(true, "yes", "no"))
        assertEquals("n/a", BooleanUtil.toString(null, "yes", "no", "n/a"))
        assertEquals(1, BooleanUtil.toInt(true))
        assertEquals('\u0000', BooleanUtil.toChar(false))
    }

    @Test
    fun textSimilarity() {
        assertEquals(1.0, TextSimilarity.similar("kmp-hutool", "kmp hutool"))
        assertEquals(1.0, TextSimilarity.similar("", ""))
        assertEquals(0.0, TextSimilarity.similar("123", ""))
        assertEquals(0.6666666667, TextSimilarity.similar("abc", "abd"))
        assertEquals("66.67%", TextSimilarity.similar("abc", "abd", 2))
        assertEquals("ace", TextSimilarity.longestCommonSubstring("abcde", "ace"))
    }
}
