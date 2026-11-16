package io.github.shijianjs.kmphutool.core

import io.github.shijianjs.kmphutool.core.codec.Base32
import io.github.shijianjs.kmphutool.core.codec.BCD
import io.github.shijianjs.kmphutool.core.codec.Caesar
import kotlin.test.Test
import kotlin.test.assertEquals

@Suppress("DEPRECATION")
class CodecExtrasTest {
    @Test
    fun base32RoundTripsAndMatchesRfc() {
        assertEquals("MZXW6YTBOI======", Base32.encode("foobar"))
        assertEquals("foobar", Base32.decodeStr("MZXW6YTBOI======"))
        val hex = Base32.encodeHex("foobar")
        assertEquals("foobar", Base32.decodeStrHex(hex))
        // Lowercase input is accepted on decode.
        assertEquals("foobar", Base32.decodeStr("mzxw6ytboi======"))
    }

    @Test
    fun caesarShiftsOnlyLetters() {
        val plain = "Hello, World! 123 你好"
        val cipher = Caesar.encode(plain, 3)
        assertEquals(plain, Caesar.decode(cipher, 3))
        // The interleaved AaBb wheel means odd offsets also change letter case, as upstream does.\n        assertEquals("iGNNQ, xQTNF! 123 你好", cipher)
        assertEquals(plain, Caesar.encode(plain, 0))
    }

    @Test
    fun bcdPacksAndUnpacks() {
        val packed = BCD.strToBcd("1234")
        assertEquals("1234", BCD.bcdToStr(packed))
        assertEquals(0x12.toByte(), packed[0])
        assertEquals(0x34.toByte(), packed[1])
        val ascii = "1234".encodeToByteArray()
        assertEquals("1234", BCD.bcdToStr(BCD.ascToBcd(ascii)))
    }
}
