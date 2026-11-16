package io.github.shijianjs.kmphutool.core.text

import io.github.shijianjs.kmphutool.core.util.HexUtil

/**
 * `\uXXXX` encoding and decoding for text.
 *
 * Port of `cn.hutool.core.text.UnicodeUtil`. It simply forwards to the hexadecimal helpers, exactly
 * as upstream does.
 *
 * @hutool cn.hutool.core.text.UnicodeUtil
 */
object UnicodeUtil {
    /** The `\uXXXX` form of [ch]. */
    fun toUnicode(ch: Char): String = HexUtil.toUnicodeHex(ch)

    /** The `\uXXXX` form of the code point [code]. */
    fun toUnicode(code: Int): String = HexUtil.toUnicodeHex(code)

    /** Every character of [text] encoded as `\uXXXX`. */
    fun toUnicode(text: CharSequence?): String? {
        if (text == null) return null
        val result = StringBuilder(text.length * 6)
        for (ch in text) result.append(HexUtil.toUnicodeHex(ch))
        return result.toString()
    }

    /**
     * Decodes `\uXXXX` sequences; text without escapes is returned unchanged and an incomplete
     * escape is kept as written.
     */
    fun decodeUnicode(text: CharSequence?): String? {
        if (text == null) return null
        val source = text.toString()
        if (!source.contains("\\u")) return source
        val result = StringBuilder(source.length)
        var index = 0
        while (index < source.length) {
            if (source[index] == '\\' && index + 5 < source.length + 1 && index + 6 <= source.length &&
                source[index + 1] == 'u'
            ) {
                val value = source.substring(index + 2, index + 6).toIntOrNull(16)
                if (value != null) {
                    result.append(value.toChar())
                    index += 6
                    continue
                }
            }
            result.append(source[index])
            index++
        }
        return result.toString()
    }
}
