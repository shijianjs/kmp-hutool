package io.github.shijianjs.kmphutool.core.codec

import io.github.shijianjs.kmphutool.core.lang.Assert

/**
 * RotN — rotate letters (and optionally digits) by N places.
 *
 * Port of `cn.hutool.core.codec.Rot` (originally from orclight/jencrypt). Rot-13 is its own inverse
 * for letters; numbers shift within `0-9`.
 *
 * @hutool cn.hutool.core.codec.Rot
 */
object Rot {
    private const val A = 'A'
    private const val Z = 'Z'
    private const val LOWER_A = 'a'
    private const val LOWER_Z = 'z'
    private const val ZERO = '0'
    private const val NINE = '9'

    /** Rot-13 encode, digits included. */
    fun encode13(message: String): String = encode13(message, true)

    /** Rot-13 encode. */
    fun encode13(message: String, isEncodeNumber: Boolean): String = encode(message, 13, isEncodeNumber)

    /** Rot-N encode. */
    fun encode(message: String?, offset: Int, isEncodeNumber: Boolean): String {
        Assert.notNull(message, "message must not be null")
        return buildString(message!!.length) {
            message.forEach { append(encodeChar(it, offset, isEncodeNumber)) }
        }
    }

    /** Rot-13 decode, digits included. */
    fun decode13(rot: String): String = decode13(rot, true)

    /** Rot-13 decode. */
    fun decode13(rot: String, isDecodeNumber: Boolean): String = decode(rot, 13, isDecodeNumber)

    /** Rot-N decode. */
    fun decode(rot: String?, offset: Int, isDecodeNumber: Boolean): String {
        Assert.notNull(rot, "rot must not be null")
        return buildString(rot!!.length) {
            rot.forEach { append(decodeChar(it, offset, isDecodeNumber)) }
        }
    }

    private fun encodeChar(c: Char, offset: Int, isEncodeNumber: Boolean): Char {
        var ch = c
        if (isEncodeNumber && ch in ZERO..NINE) {
            ch = (((ch - ZERO + offset) % 10) + ZERO.code).toChar()
        }
        ch = when (ch) {
            in A..Z -> (((ch - A + offset) % 26) + A.code).toChar()
            in LOWER_A..LOWER_Z -> (((ch - LOWER_A + offset) % 26) + LOWER_A.code).toChar()
            else -> ch
        }
        return ch
    }

    private fun decodeChar(c: Char, offset: Int, isDecodeNumber: Boolean): Char {
        var code = c.code
        if (isDecodeNumber && code in ZERO.code..NINE.code) {
            var temp = code - ZERO.code - offset
            while (temp < 0) temp += 10
            code = temp + ZERO.code
        }
        code = when {
            code in A.code..Z.code -> {
                var temp = code - A.code - offset
                while (temp < 0) temp += 26
                temp + A.code
            }
            code in LOWER_A.code..LOWER_Z.code -> {
                var temp = code - LOWER_A.code - offset
                if (temp < 0) temp += 26
                temp + LOWER_A.code
            }
            else -> code
        }
        return code.toChar()
    }
}
