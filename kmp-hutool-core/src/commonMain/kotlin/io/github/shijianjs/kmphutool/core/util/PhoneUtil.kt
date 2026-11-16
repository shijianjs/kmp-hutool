package io.github.shijianjs.kmphutool.core.util

import io.github.shijianjs.kmphutool.core.text.StrPool

/**
 * Telephone predicates and partial masking.
 *
 * Port of `cn.hutool.core.util.PhoneUtil`. The mainland, Hong Kong, Taiwan and Macau patterns are
 * copied from upstream; the mask helpers keep the same shape (`hideBefore` keeps the tail,
 * `hideAfter` keeps the head, `hideBetween` keeps both).
 *
 * @hutool cn.hutool.core.util.PhoneUtil
 */
object PhoneUtil {
    private val MAINLAND_MOBILE = Regex("""^1(?:3\d|4[5-9]|5[0-35-9]|6[2567]|7[0-8]|8\d|9\d)\d{8}$""")
    private val MAINLAND_TEL = Regex("""^(?:0\d{2,3}-?)?[2-9]\d{6,7}$""")
    private val HK_MOBILE = Regex("""^(?:5|6|9)\d{7}$""")
    private val TW_MOBILE = Regex("""^09\d{8}$""")
    private val MO_MOBILE = Regex("""^6\d{7}$""")

    /** `true` for a mainland China mobile number. */
    fun isMobile(value: CharSequence?): Boolean = value != null && MAINLAND_MOBILE.matches(value)

    /** `true` for a Hong Kong mobile number. */
    fun isMobileHk(value: CharSequence?): Boolean = value != null && HK_MOBILE.matches(value)

    /** `true` for a Taiwan mobile number. */
    fun isMobileTw(value: CharSequence?): Boolean = value != null && TW_MOBILE.matches(value)

    /** `true` for a Macau mobile number. */
    fun isMobileMo(value: CharSequence?): Boolean = value != null && MO_MOBILE.matches(value)

    /** `true` for a mainland landline number, with or without the area code. */
    fun isTel(value: CharSequence?): Boolean = value != null && MAINLAND_TEL.matches(value)

    /** `true` for the 400/800 service numbers. */
    fun isTel400800(value: CharSequence?): Boolean {
        val digits = value?.toString()?.replace("-", StrPool.EMPTY) ?: return false
        return digits.length == 10 && (digits.startsWith("400") || digits.startsWith("800"))
    }

    /** `true` for any number [isMobile], [isTel] or [isTel400800] accepts. */
    fun isPhone(value: CharSequence?): Boolean =
        isMobile(value) || isTel(value) || isTel400800(value)

    /** Masks everything but the last four characters: `*******1234`. */
    fun hideBefore(phone: CharSequence?): String = mask(phone, keepHead = 0, keepTail = 4)

    /** Keeps the first three and the last four characters: `138****1234`. */
    fun hideBetween(phone: CharSequence?): String = mask(phone, keepHead = 3, keepTail = 4)

    /** Keeps the first three characters: `138********`. */
    fun hideAfter(phone: CharSequence?): String = mask(phone, keepHead = 3, keepTail = 0)

    /** The part before the masked tail, i.e. the area code of a landline number. */
    fun subBefore(phone: CharSequence?): String = keepBefore(phone, 4)

    /** The middle part of a number, with head and tail masked. */
    fun subBetween(phone: CharSequence?): String = hideBetween(phone)

    /** The tail of a number, with the head masked. */
    fun subAfter(phone: CharSequence?): String = keepTail(phone, 4)

    /** The area code of a landline number. */
    fun subTelBefore(value: CharSequence?): String = subBefore(value)

    /** The subscriber number of a landline number. */
    fun subTelAfter(value: CharSequence?): String = subAfter(value)

    private fun mask(phone: CharSequence?, keepHead: Int, keepTail: Int): String {
        val text = phone?.toString() ?: return StrPool.EMPTY
        if (text.isEmpty()) return text
        val head = text.take(keepHead.coerceIn(0, text.length))
        val tail = text.takeLast(keepTail.coerceIn(0, text.length - head.length))
        val stars = (text.length - head.length - tail.length).coerceAtLeast(0)
        return head + StrPool.EMPTY.padEnd(stars, '*') + tail
    }

    private fun keepBefore(phone: CharSequence?, tailLength: Int): String {
        val text = phone?.toString() ?: return StrPool.EMPTY
        return text.dropLast(tailLength.coerceAtMost(text.length))
    }

    private fun keepTail(phone: CharSequence?, tailLength: Int): String {
        val text = phone?.toString() ?: return StrPool.EMPTY
        return text.takeLast(tailLength.coerceAtMost(text.length))
    }
}
