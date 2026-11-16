package io.github.shijianjs.kmphutool.core.codec

import io.github.shijianjs.kmphutool.core.lang.Assert

/**
 * Binary-coded decimal helpers.
 *
 * Port of `cn.hutool.core.codec.BCD`, which upstream marks deprecated (the ASCII handling is loose
 * and the format is non-standard). Kept for parity and marked deprecated here as well.
 *
 * @hutool cn.hutool.core.codec.BCD
 */
@Deprecated("Upstream removes this in 6.0.0; the ASCII/BCD mapping is non-standard.")
object BCD {
    /** Packs a hex-like ASCII string into BCD bytes, left-padding an odd length with `0`. */
    fun strToBcd(asc: String): ByteArray {
        Assert.notNull(asc, "ASCII must not be null!")
        var text = asc
        if (text.length % 2 != 0) text = "0$text"

        val result = ByteArray(text.length / 2)
        for (p in result.indices) {
            val high = digitValue(text[2 * p])
            val low = digitValue(text[2 * p + 1])
            result[p] = ((high shl 4) + low).toByte()
        }
        return result
    }

    /** Packs an ASCII byte array into BCD bytes. */
    fun ascToBcd(ascii: ByteArray): ByteArray = ascToBcd(ascii, ascii.size)

    /** Packs [ascLength] bytes of [ascii] into BCD; an odd length leaves the low nibble at 0. */
    fun ascToBcd(ascii: ByteArray, ascLength: Int): ByteArray {
        Assert.notNull(ascii, "Ascii must be not null!")
        val bcd = ByteArray(ascLength / 2)
        var j = 0
        for (i in bcd.indices) {
            val high = singleDigit(ascii[j++])
            val low = if (j >= ascLength) 0 else singleDigit(ascii[j++])
            bcd[i] = ((high shl 4) + low).toByte()
        }
        return bcd
    }

    /** Unpacks BCD bytes into uppercase hex characters, two characters per byte. */
    fun bcdToStr(bytes: ByteArray): String {
        Assert.notNull(bytes, "Bcd bytes must be not null!")
        val out = StringBuilder(bytes.size * 2)
        for (b in bytes) {
            out.append(hexChar((b.toInt() shr 4) and 0x0f))
            out.append(hexChar(b.toInt() and 0x0f))
        }
        return out.toString()
    }

    private fun digitValue(c: Char): Int = when (c) {
        in '0'..'9' -> c - '0'
        in 'a'..'z' -> c - 'a' + 0x0a
        else -> c - 'A' + 0x0a
    }

    private fun singleDigit(b: Byte): Int {
        val value = b.toInt()
        return when {
            value >= '0'.code && value <= '9'.code -> value - '0'.code
            value >= 'A'.code && value <= 'F'.code -> value - 'A'.code + 10
            value >= 'a'.code && value <= 'f'.code -> value - 'a'.code + 10
            else -> value - 48
        }
    }

    private fun hexChar(nibble: Int): Char = if (nibble > 9) 'A' + (nibble - 10) else '0' + nibble
}
