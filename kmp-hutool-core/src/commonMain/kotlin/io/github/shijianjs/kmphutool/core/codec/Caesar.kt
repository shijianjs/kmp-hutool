package io.github.shijianjs.kmphutool.core.codec

import io.github.shijianjs.kmphutool.core.lang.Assert

/**
 * Caesar shift over the 52 Latin letters (upper and lower case keep their case); other characters
 * stay untouched.
 *
 * Port of `cn.hutool.core.codec.Caesar`.
 *
 * @hutool cn.hutool.core.codec.Caesar
 */
object Caesar {
    /** The 52-character wheel: `AaBb...Zz`. */
    const val TABLE: String = "AaBbCcDdEeFfGgHhIiJjKkLlMmNnOoPpQqRrSsTtUuVvWwXxYyZz"

    /** Encrypts [message] by shifting each letter [offset] positions forward. */
    fun encode(message: String, offset: Int): String {
        Assert.notNull(message, "message must be not null!")
        return message.map { c -> if (TABLE.indexOf(c) >= 0) shift(c, offset) else c }.joinToString("")
    }

    /** Decrypts [cipherText] by shifting each letter [offset] positions back. */
    fun decode(cipherText: String, offset: Int): String {
        Assert.notNull(cipherText, "cipherText must be not null!")
        return cipherText.map { c -> if (TABLE.indexOf(c) >= 0) shift(c, -offset) else c }.joinToString("")
    }

    private fun shift(c: Char, offset: Int): Char {
        val position = ((TABLE.indexOf(c) + offset) % 52 + 52) % 52
        return TABLE[position]
    }
}
