package io.github.shijianjs.kmphutool.core.codec

import io.github.shijianjs.kmphutool.core.exceptions.UtilException
import io.github.shijianjs.kmphutool.core.util.StrUtil

/**
 * Hexadecimal codec with selectable letter case.
 *
 * Port of `cn.hutool.core.codec.Base16Codec`. Kotlin-ified: `encode` returns a [String] instead of
 * `char[]`, and the shared `Encoder`/`Decoder` interfaces are skipped because they are string-bound.
 *
 * @hutool cn.hutool.core.codec.Base16Codec
 */
class Base16Codec(lowerCase: Boolean = true) {
    private val alphabets: String = if (lowerCase) "0123456789abcdef" else "0123456789ABCDEF"

    /** Encodes bytes as hex text. */
    fun encode(data: ByteArray): String = buildString(data.size * 2) {
        for (b in data) {
            append(alphabets[(0xF0 and b.toInt()) ushr 4])
            append(alphabets[0x0F and b.toInt()])
        }
    }

    /** Decodes hex text, ignoring blanks and zero-padding an odd-length input. */
    fun decode(encoded: CharSequence?): ByteArray? {
        if (StrUtil.isEmpty(encoded)) return null
        var text = StrUtil.cleanBlank(encoded).toString()
        if (text.length and 0x01 != 0) text = "0$text"
        val out = ByteArray(text.length shr 1)
        var i = 0
        var j = 0
        while (j < text.length) {
            val high = toDigit(text[j], j).shl(4)
            j++
            val low = toDigit(text[j], j)
            j++
            out[i++] = (high or low).toByte()
        }
        return out
    }

    /** Renders a char as a `\uXXXX` escape. */
    fun toUnicodeHex(ch: Char): String =
        "\\u" + alphabets[(ch.code shr 12) and 15] +
            alphabets[(ch.code shr 8) and 15] +
            alphabets[(ch.code shr 4) and 15] +
            alphabets[ch.code and 15]

    /** Appends one byte's two hex digits to [builder]. */
    fun appendHex(builder: StringBuilder, b: Byte) {
        builder.append(alphabets[(b.toInt() and 0xF0) ushr 4])
        builder.append(alphabets[b.toInt() and 0x0F])
    }

    companion object {
        /** Lower-case instance. */
        val CODEC_LOWER = Base16Codec(true)

        /** Upper-case instance. */
        val CODEC_UPPER = Base16Codec(false)

        private fun toDigit(ch: Char, index: Int): Int {
            val digit = ch.digitToIntOrNull(16) ?: -1
            if (digit < 0) throw UtilException("Illegal hexadecimal character $ch at index $index")
            return digit
        }
    }
}
