package io.github.shijianjs.kmphutool.core

import io.github.shijianjs.kmphutool.core.lang.ansi.Ansi8BitColor
import io.github.shijianjs.kmphutool.core.lang.ansi.AnsiBackground
import io.github.shijianjs.kmphutool.core.lang.ansi.AnsiColor
import io.github.shijianjs.kmphutool.core.lang.ansi.AnsiColorWrapper
import io.github.shijianjs.kmphutool.core.lang.ansi.AnsiEncoder
import io.github.shijianjs.kmphutool.core.lang.ansi.AnsiStyle
import io.github.shijianjs.kmphutool.core.lang.ansi.BitDepth
import io.github.shijianjs.kmphutool.core.lang.ansi.ForeOrBack
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class AnsiTest {
    @Test
    fun encodesStylesAndColours() {
        assertEquals(
            "\u001B[31;1mred bold\u001B[0;39m",
            AnsiEncoder.encode(AnsiColor.RED, AnsiStyle.BOLD, "red bold"),
        )
        assertEquals("\u001B[38;5;42m8bit\u001B[0;39m", AnsiEncoder.encode(Ansi8BitColor.foreground(42), "8bit"))
        assertEquals("plain", AnsiEncoder.encode("plain"))
    }

    @Test
    fun wrapperMatchesElements() {
        assertTrue(AnsiColorWrapper(31, BitDepth.FOUR).toAnsiElement(ForeOrBack.FORE) == AnsiColor.RED)
        assertTrue(AnsiColorWrapper(31, BitDepth.FOUR).toAnsiElement(ForeOrBack.BACK) == AnsiBackground.RED)
        assertTrue(AnsiColorWrapper(200, BitDepth.EIGHT).toAnsiElement(ForeOrBack.BACK) is Ansi8BitColor)
        assertFalse(AnsiColorWrapper(31, BitDepth.FOUR) == AnsiColorWrapper(31, BitDepth.EIGHT))
    }
}
