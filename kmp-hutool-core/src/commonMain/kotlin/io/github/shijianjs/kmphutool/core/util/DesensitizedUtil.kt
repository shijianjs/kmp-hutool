package io.github.shijianjs.kmphutool.core.util

import io.github.shijianjs.kmphutool.core.text.StrPool

/**
 * Masks sensitive text for logs and screens, the Hutool `DesensitizedUtil`.
 *
 * Port of `cn.hutool.core.util.DesensitizedUtil`. The Java class builds a `DesensitizedUtil` object
 * whose `desensitized` hook lets callers swap the strategy; the port exposes the strategies as
 * plain functions, which is how Kotlin call sites use them. The `CarLicense` and address variants
 * keep upstream's fixed separators.
 *
 * @hutool cn.hutool.core.util.DesensitizedUtil
 */
object DesensitizedUtil {
    private const val STAR = '*'

    /** Masks a user id, keeping [front] characters at the start and [end] at the end. */
    fun userId(id: Long, front: Int = 1, end: Int = 1): String =
        desensitize(id.toString(), front, end)

    /** Masks an id card number, keeping [front] characters at the start and [end] at the end. */
    fun idCardNum(idCard: CharSequence?, front: Int = 1, end: Int = 2): String {
        val text = idCard?.toString() ?: return StrPool.EMPTY
        return desensitize(text, front, end)
    }

    /** Masks an 11-digit mobile number: `13812345678` → `138****5678`. */
    fun mobilePhone(number: CharSequence?): String {
        val text = number?.toString() ?: return StrPool.EMPTY
        return if (text.length == 11) desensitize(text, 3, 4) else desensitize(text, 1, 1)
    }

    /** Replaces a password with six stars, whatever its length. */
    fun password(password: CharSequence?): String = StrPool.EMPTY.padEnd(password?.length ?: 6, STAR)

    /** Masks a Chinese name, keeping the surname: `张三丰` → `张**`. */
    fun chineseName(name: CharSequence?): String {
        val text = name?.toString() ?: return StrPool.EMPTY
        return if (text.length <= 1) text else text.first() + StrPool.EMPTY.padEnd(text.length - 1, STAR)
    }

    /**
     * Masks a bank card number the way upstream does: blank characters are removed first, the first
     * four digits stay visible, the rest becomes groups of four stars, and the last `length % 4`
     * digits (four when the length is a multiple of four) close the string.
     *
     * `"6222021234567890"` → `"6222 **** **** 7890"`.
     */
    fun bankCard(cardNo: CharSequence?): String {
        val text = cardNo?.toString()?.filter { !it.isWhitespace() } ?: return StrPool.EMPTY
        if (text.length < 9) return text
        val length = text.length
        val endLength = if (length % 4 == 0) 4 else length % 4
        val midLength = length - 4 - endLength
        val builder = StringBuilder(length + length / 4)
        builder.append(text, 0, 4)
        for (index in 0 until midLength) {
            if (index % 4 == 0) builder.append(' ')
            builder.append(STAR)
        }
        builder.append(' ').append(text, length - endLength, length)
        return builder.toString()
    }

    /** Masks an e-mail address, keeping the first character of the local part and the domain. */
    fun email(email: CharSequence?): String {
        val text = email?.toString() ?: return StrPool.EMPTY
        val at = text.indexOf('@')
        if (at <= 0) return text
        val local = text.substring(0, at)
        val masked = if (local.length <= 1) STAR.toString() else local.first() + StrPool.EMPTY.padEnd(3, STAR)
        return masked + text.substring(at)
    }

    /**
     * Masks a Chinese licence plate the way upstream does: for a 7-character plate the characters at
     * indexes 3..5 become stars, for an 8-character new-energy plate indexes 3..6.
     */
    fun carLicense(license: CharSequence?): String {
        val text = license?.toString() ?: return StrPool.EMPTY
        return when (text.length) {
            7 -> hide(text, 3, 6)
            8 -> hide(text, 3, 7)
            else -> text
        }
    }

    /** Masks an address, dropping [sensitiveLength] characters from the middle. */
    fun address(address: CharSequence?, sensitiveLength: Int): String {
        val text = address?.toString() ?: return StrPool.EMPTY
        if (sensitiveLength <= 0 || text.length <= sensitiveLength) return text
        val keep = text.length - sensitiveLength
        val front = keep / 2
        val end = keep - front
        return text.substring(0, front) + StrPool.EMPTY.padEnd(sensitiveLength, STAR) + text.takeLast(end)
    }

    /** Masks the characters in `[startInclude, endExclude)` with [fixedChar]. */
    fun hide(str: CharSequence?, startInclude: Int, endExclude: Int, fixedChar: Char = STAR): String {
        val text = str?.toString() ?: return StrPool.EMPTY
        val start = startInclude.coerceIn(0, text.length)
        val end = endExclude.coerceIn(start, text.length)
        return text.substring(0, start) + StrPool.EMPTY.padEnd(end - start, fixedChar) + text.substring(end)
    }

    private fun desensitize(text: String, front: Int, end: Int): String {
        if (text.isEmpty()) return text
        val keepFront = front.coerceIn(0, text.length)
        val keepEnd = end.coerceIn(0, text.length - keepFront)
        val stars = (text.length - keepFront - keepEnd).coerceAtLeast(0)
        return text.substring(0, keepFront) + StrPool.EMPTY.padEnd(stars, STAR) + text.takeLast(keepEnd)
    }
}
