package io.github.shijianjs.kmphutool.core.util

import io.github.shijianjs.kmphutool.core.text.StrPool

/**
 * Hexadecimal encoding and decoding.
 *
 * Port of `cn.hutool.core.util.HexUtil`. The colour helpers (`encodeColor`/`decodeColor`) are not
 * ported: they take `java.awt.Color`, which commonMain does not have. Everything else works on bytes
 * and strings only.
 *
 * @hutool cn.hutool.core.util.HexUtil
 */
object HexUtil {
    private val HEX_DIGITS = "0123456789ABCDEF".toCharArray()
    private val HEX_DIGITS_LOWER = "0123456789abcdef".toCharArray()

    /** `true` when [value] is hexadecimal with an optional `0x` / `#` prefix. */
    fun isHexNumber(value: CharSequence?): Boolean {
        if (value == null || value.isEmpty()) return false
        val text = when {
            value.startsWith("0x") || value.startsWith("0X") -> value.substring(2)
            value.startsWith("#") -> value.substring(1)
            else -> value
        }
        return text.isNotEmpty() && text.length % 2 == 0 && text.all { CharUtil.isHexChar(it) }
    }

    /** Encodes [data] as upper-case hexadecimal bytes. */
    fun encodeHex(data: ByteArray): ByteArray = encodeHex(data, toLowerCase = false)

    /** Encodes [data] as hexadecimal bytes, lower-cased when [toLowerCase] is `true`. */
    fun encodeHex(data: ByteArray, toLowerCase: Boolean): ByteArray {
        val digits = if (toLowerCase) HEX_DIGITS_LOWER else HEX_DIGITS
        val result = ByteArray(data.size * 2)
        for (index in data.indices) {
            val value = data[index].toInt() and 0xFF
            result[index * 2] = digits[value ushr 4].code.toByte()
            result[index * 2 + 1] = digits[value and 0x0F].code.toByte()
        }
        return result
    }

    /** Encodes [data] as an upper-case hexadecimal string. */
    fun encodeHexStr(data: ByteArray): String = encodeHexStr(data, toLowerCase = false)

    /** Encodes [data] as a hexadecimal string, lower-cased when [toLowerCase] is `true`. */
    fun encodeHexStr(data: ByteArray, toLowerCase: Boolean): String {
        val digits = if (toLowerCase) HEX_DIGITS_LOWER else HEX_DIGITS
        val result = StringBuilder(data.size * 2)
        for (byte in data) {
            val value = byte.toInt() and 0xFF
            result.append(digits[value ushr 4]).append(digits[value and 0x0F])
        }
        return result.toString()
    }

    /** Encodes the UTF-8 bytes of [data] as a hexadecimal string. */
    fun encodeHexStr(data: String): String = encodeHexStr(data.encodeToByteArray())

    /** Decodes [hexStr] into bytes; `0x` prefixes are accepted. */
    fun decodeHex(hexStr: CharSequence): ByteArray {
        val text = if (hexStr.startsWith("0x") || hexStr.startsWith("0X")) hexStr.substring(2) else hexStr
        require(text.length % 2 == 0) { "The hexadecimal string must have an even length" }
        val result = ByteArray(text.length / 2)
        for (index in result.indices) {
            val high = CharUtil.digit16(text[index * 2])
            val low = CharUtil.digit16(text[index * 2 + 1])
            require(high >= 0 && low >= 0) { "Illegal hexadecimal character at index ${index * 2}" }
            result[index] = ((high shl 4) + low).toByte()
        }
        return result
    }

    /** Decodes [hexStr] into bytes. */
    fun decodeHex(hexStr: String): ByteArray = decodeHex(hexStr as CharSequence)

    /** Decodes [hexData] into a UTF-8 string. */
    fun decodeHexStr(hexStr: String): String = decodeHex(hexStr).decodeToString()

    /** The `\uXXXX` escape for [value]. */
    fun toUnicodeHex(value: Int): String = "\\u" + value.toString(16).padStart(4, '0')

    /** The `\uXXXX` escape for [ch]. */
    fun toUnicodeHex(ch: Char): String = toUnicodeHex(ch.code)

    /** The hexadecimal form of [value] in upper case. */
    fun toHex(value: Int): String = value.toString(16).uppercase()

    /** Parses a hexadecimal string as an [Int]. */
    fun hexToInt(value: String): Int = value.toInt(16)

    /** The hexadecimal form of [value] in upper case. */
    fun toHex(value: Long): String = value.toString(16).uppercase()

    /** Parses a hexadecimal string as a [Long]. */
    fun hexToLong(value: String): Long = value.toLong(16)

    /** The IEEE-754 hexadecimal form of [value]. */
    fun toHex(value: Float): String = value.toRawBits().toString(16).uppercase()

    /** Parses the hexadecimal raw bits of a [Float]. */
    fun hexToFloat(value: String): Float = Float.fromBits(value.toLong(16).toInt())

    /** The IEEE-754 hexadecimal form of [value]. */
    fun toHex(value: Double): String = value.toRawBits().toString(16).uppercase()

    /** Parses the hexadecimal raw bits of a [Double]. */
    fun hexToDouble(value: String): Double = Double.fromBits(value.toLong(16))

    /** Parses a hexadecimal string as a decimal string. */
    fun toBigInteger(hexStr: String): String = hexStr.toLong(16).toString()

    /**
     * Groups [hexStr] into byte-sized chunks separated by a space: `"1a2b"` → `"1a 2b"`.
     *
     * @param prefix a prefix put in front of each chunk
     */
    fun format(hexStr: String?, prefix: String = StrPool.EMPTY): String? {
        if (hexStr == null) return null
        if (hexStr.isEmpty()) return StrPool.EMPTY
        val builder = StringBuilder(hexStr.length + hexStr.length / 2)
        var index = 0
        while (index < hexStr.length) {
            if (index > 0 && index % 2 == 0) builder.append(StrPool.SPACE)
            if (index % 2 == 0) builder.append(prefix)
            builder.append(hexStr[index])
            index++
        }
        return builder.toString()
    }
}
