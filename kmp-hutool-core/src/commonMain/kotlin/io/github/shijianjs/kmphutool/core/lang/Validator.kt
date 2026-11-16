package io.github.shijianjs.kmphutool.core.lang

import io.github.shijianjs.kmphutool.core.exceptions.ValidateException
import io.github.shijianjs.kmphutool.core.util.CharUtil

/**
 * Pattern-based validators for the formats Hutool checks with `Validator`.
 *
 * Port of `cn.hutool.core.lang.Validator`. The `validate*` members throw
 * [io.github.shijianjs.kmphutool.core.exceptions.ValidateException] with the message produced from the
 * `{}` template and its arguments, exactly like upstream.
 *
 * Not ported: `isCitizenId`/`isCreditCode`/`isCarDrivingLicence`/`isCarVin`/`isChineseName`/`isPlateNumber`
 * (they need upstream's checksum tables and administrative-division data, which belong with a dedicated
 * data module rather than a regex), and the `Class<?>`-based overloads (`validateGeneral` with a type
 * token) — Kotlin passes a lambda instead.
 *
 * @hutool cn.hutool.core.lang.Validator
 */
object Validator {
    /** `true` when [value] is `true`. */
    fun isTrue(value: Boolean): Boolean = value

    /** `true` when [value] is `false`. */
    fun isFalse(value: Boolean): Boolean = !value

    /** Throws unless [value] is `true`. */
    fun validateTrue(value: Boolean, errorMsgTemplate: String = "The value must be true", vararg params: Any?) {
        if (!value) throw exception(errorMsgTemplate, value, *params)
    }

    /** Throws unless [value] is `false`. */
    fun validateFalse(value: Boolean, errorMsgTemplate: String = "The value must be false", vararg params: Any?) {
        if (value) throw exception(errorMsgTemplate, value, *params)
    }

    /** `true` when [value] is `null`. */
    fun isNull(value: Any?): Boolean = value == null

    /** `true` when [value] is not `null`. */
    fun isNotNull(value: Any?): Boolean = value != null

    /** Throws unless [value] is `null`. */
    fun <T> validateNull(value: T?, errorMsgTemplate: String = "The value must be null", vararg params: Any?) {
        if (value != null) throw exception(errorMsgTemplate, value, *params)
    }

    /** Throws unless [value] is not `null`. */
    fun <T : Any> validateNotNull(value: T?, errorMsgTemplate: String = "The value must not be null", vararg params: Any?): T {
        if (value == null) throw exception(errorMsgTemplate, value, *params)
        return value
    }

    /** `true` when [value] is empty (see `ObjectUtil.isEmpty`). */
    fun isEmpty(value: Any?): Boolean = io.github.shijianjs.kmphutool.core.util.ObjectUtil.isEmpty(value)

    /** `true` when [value] is not empty. */
    fun isNotEmpty(value: Any?): Boolean = !isEmpty(value)

    /** Throws unless [value] is empty. */
    fun <T> validateEmpty(value: T?, errorMsgTemplate: String = "The value must be empty", vararg params: Any?) {
        if (!isEmpty(value)) throw exception(errorMsgTemplate, value, *params)
    }

    /** Throws unless [value] is not empty. */
    fun <T> validateNotEmpty(value: T?, errorMsgTemplate: String = "The value must not be empty", vararg params: Any?) {
        if (isEmpty(value)) throw exception(errorMsgTemplate, value, *params)
    }

    /** `true` when [t1] equals [t2] by `==`. */
    fun equal(t1: Any?, t2: Any?): Boolean = t1 == t2

    /** Throws unless the two values are equal. */
    fun validateEqual(t1: Any?, t2: Any?, errorMsgTemplate: String = "The value must be equal to {}", vararg params: Any?) {
        if (!equal(t1, t2)) throw exception(errorMsgTemplate, t1, *params)
    }

    /** Throws when the two values are equal. */
    fun validateNotEqual(t1: Any?, t2: Any?, errorMsgTemplate: String = "The value must not be equal to {}", vararg params: Any?) {
        if (equal(t1, t2)) throw exception(errorMsgTemplate, t1, *params)
    }

    /** `true` when [value] matches the regular expression [regex]. */
    fun isMatchRegex(regex: String, value: CharSequence?): Boolean =
        value != null && Regex(regex).matches(value)

    /** Throws unless [value] matches [regex]. */
    fun <T> validateMatchRegex(regex: String, value: T?, errorMsgTemplate: String = "The value {} is not matched with the regex {}", vararg params: Any?) {
        if (value != null && !isMatchRegex(regex, value.toString())) throw exception(errorMsgTemplate, value, *params)
    }

    /**
     * `true` when the text is made of word characters only — letters, digits and `_`, the shape a
     * username has to have.
     */
    fun isGeneral(value: CharSequence?, min: Int = 0, max: Int = Int.MAX_VALUE): Boolean {
        if (value == null) return false
        if (value.length < min || value.length > max) return false
        for (ch in value) {
            if (!CharUtil.isLetterOrNumber(ch) && ch != '_') return false
        }
        return true
    }

    /** Throws unless the text is a word of [min]..[max] characters. */
    fun <T> validateGeneral(value: T?, min: Int = 0, max: Int = Int.MAX_VALUE, errorMsgTemplate: String = "The value must be a word", vararg params: Any?) {
        if (!isGeneral(value?.toString(), min, max)) throw exception(errorMsgTemplate, value, *params)
    }

    /** `true` when the text is made of letters only. */
    fun isLetter(value: CharSequence?): Boolean = value != null && value.all { CharUtil.isLetter(it) }

    /** Throws unless the text is made of letters only. */
    fun <T> validateLetter(value: T?, errorMsgTemplate: String = "The value {} must be made of letters", vararg params: Any?) {
        if (!isLetter(value?.toString())) throw exception(errorMsgTemplate, value, *params)
    }

    /** `true` when the text is made of upper-case letters only. */
    fun isUpperCase(value: CharSequence?): Boolean = value != null && value.all { CharUtil.isLetterUpper(it) }

    /** Throws unless the text is made of upper-case letters only. */
    fun <T> validateUpperCase(value: T?, errorMsgTemplate: String = "The value {} must be upper case", vararg params: Any?) {
        if (!isUpperCase(value?.toString())) throw exception(errorMsgTemplate, value, *params)
    }

    /** `true` when the text is made of lower-case letters only. */
    fun isLowerCase(value: CharSequence?): Boolean = value != null && value.all { CharUtil.isLetterLower(it) }

    /** Throws unless the text is made of lower-case letters only. */
    fun <T> validateLowerCase(value: T?, errorMsgTemplate: String = "The value {} must be lower case", vararg params: Any?) {
        if (!isLowerCase(value?.toString())) throw exception(errorMsgTemplate, value, *params)
    }

    /** `true` when the text is numeric (digits only, `-` and `.` allowed for signed decimals). */
    fun isNumber(value: CharSequence?): Boolean {
        if (value == null || value.isEmpty()) return false
        var seenDigit = false
        var seenDot = false
        var index = 0
        if (value[0] == '-') {
            index = 1
            if (value.length == 1) return false
        }
        while (index < value.length) {
            val ch = value[index]
            when {
                CharUtil.isNumber(ch) -> seenDigit = true
                ch == '.' && !seenDot -> seenDot = true
                else -> return false
            }
            index++
        }
        return seenDigit
    }

    /** `true` when the text contains at least one digit. */
    fun hasNumber(value: CharSequence?): Boolean = value != null && value.any { CharUtil.isNumber(it) }

    /** Throws unless the text is numeric. */
    fun validateNumber(value: String?, errorMsgTemplate: String = "The value {} must be a number", vararg params: Any?) {
        if (!isNumber(value)) throw exception(errorMsgTemplate, value, *params)
    }

    /** `true` when the text is a word: letters, digits and `_`. */
    fun isWord(value: CharSequence?): Boolean = isGeneral(value)

    /** Throws unless the text is a word. */
    fun <T> validateWord(value: T?, errorMsgTemplate: String = "The value {} must be a word", vararg params: Any?) {
        if (!isWord(value?.toString())) throw exception(errorMsgTemplate, value, *params)
    }

    /** `true` when the text looks like an amount: digits, at most two decimals, optional `,` groups. */
    fun isMoney(value: CharSequence?): Boolean {
        if (value == null || value.isEmpty()) return false
        return Regex("""^-?(?:\d{1,3}(?:,\d{3})+|\d+)(?:\.\d{1,2})?$""").matches(value)
    }

    /** Throws unless the text looks like an amount. */
    fun <T> validateMoney(value: T?, errorMsgTemplate: String = "The value {} must be money", vararg params: Any?) {
        if (!isMoney(value?.toString())) throw exception(errorMsgTemplate, value, *params)
    }

    /** `true` when the text is a Chinese postal code (six digits, not starting with 0). */
    fun isZipCode(value: CharSequence?): Boolean =
        value != null && Regex("""^[1-9]\d{5}$""").matches(value)

    /** Throws unless the text is a postal code. */
    fun <T> validateZipCode(value: T?, errorMsgTemplate: String = "The value {} must be a zip code", vararg params: Any?) {
        if (!isZipCode(value?.toString())) throw exception(errorMsgTemplate, value, *params)
    }

    /** `true` when the text looks like an e-mail address. */
    fun isEmail(value: CharSequence?): Boolean =
        value != null && Regex("""^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\.[A-Za-z]{2,}$""").matches(value)

    /** Throws unless the text looks like an e-mail address. */
    fun <T> validateEmail(value: T?, errorMsgTemplate: String = "The value {} must be an email", vararg params: Any?) {
        if (!isEmail(value?.toString())) throw exception(errorMsgTemplate, value, *params)
    }

    /** `true` when the text looks like a mainland China mobile number. */
    fun isMobile(value: CharSequence?): Boolean =
        value != null && Regex("""^1(?:3\d|4[5-9]|5[0-35-9]|6[2567]|7[0-8]|8\d|9\d)\d{8}$""").matches(value)

    /** Throws unless the text looks like a mobile number. */
    fun <T> validateMobile(value: T?, errorMsgTemplate: String = "The value {} must be a mobile number", vararg params: Any?) {
        if (!isMobile(value?.toString())) throw exception(errorMsgTemplate, value, *params)
    }

    /** `true` when the text is a valid IPv4 address. */
    fun isIpv4(value: CharSequence?): Boolean {
        if (value == null) return false
        val parts = value.split(".")
        if (parts.size != 4) return false
        return parts.all { part ->
            if (part.isEmpty() || part.length > 3 || !part.all { CharUtil.isNumber(it) }) {
                false
            } else {
                part.toInt() <= 255
            }
        }
    }

    /** Throws unless the text is a valid IPv4 address. */
    fun <T> validateIpv4(value: T?, errorMsgTemplate: String = "The value {} must be an IPv4 address", vararg params: Any?) {
        if (!isIpv4(value?.toString())) throw exception(errorMsgTemplate, value, *params)
    }

    /** `true` when the text is a valid IPv6 address. */
    fun isIpv6(value: CharSequence?): Boolean {
        if (value == null) return false
        return Regex("""^([0-9a-fA-F]{1,4}:){7}[0-9a-fA-F]{1,4}$|^::1$|^::$""").matches(value)
    }

    /** Throws unless the text is a valid IPv6 address. */
    fun <T> validateIpv6(value: T?, errorMsgTemplate: String = "The value {} must be an IPv6 address", vararg params: Any?) {
        if (!isIpv6(value?.toString())) throw exception(errorMsgTemplate, value, *params)
    }

    /** `true` when the text is a MAC address with `-` separators. */
    fun isMac(value: CharSequence?): Boolean =
        value != null && Regex("""^([0-9A-Fa-f]{2}-){5}[0-9A-Fa-f]{2}$""").matches(value)

    /** Throws unless the text is a MAC address. */
    fun <T> validateMac(value: T?, errorMsgTemplate: String = "The value {} must be a MAC address", vararg params: Any?) {
        if (!isMac(value?.toString())) throw exception(errorMsgTemplate, value, *params)
    }

    /** `true` when the text looks like an absolute URL with a scheme. */
    fun isUrl(value: CharSequence?): Boolean =
        value != null && Regex("""^(?:https?|ftp)://[^\s/$.?#].[^\s]*$""", RegexOption.IGNORE_CASE).matches(value)

    /** Throws unless the text looks like a URL. */
    fun <T> validateUrl(value: T?, errorMsgTemplate: String = "The value {} must be a URL", vararg params: Any?) {
        if (!isUrl(value?.toString())) throw exception(errorMsgTemplate, value, *params)
    }

    /** `true` when the text is made of Chinese characters only. */
    fun isChinese(value: CharSequence?): Boolean = value != null && value.all { isChineseChar(it) }

    /** `true` when the text contains at least one Chinese character. */
    fun hasChinese(value: CharSequence?): Boolean = value != null && value.any { isChineseChar(it) }

    /** Throws unless the text is made of Chinese characters only. */
    fun <T> validateChinese(value: T?, errorMsgTemplate: String = "The value {} must be Chinese", vararg params: Any?) {
        if (!isChinese(value?.toString())) throw exception(errorMsgTemplate, value, *params)
    }

    /** `true` when the text is a word of letters, digits or Chinese characters. */
    fun isGeneralWithChinese(value: CharSequence?): Boolean {
        if (value == null) return false
        return value.all { CharUtil.isLetterOrNumber(it) || isChineseChar(it) }
    }

    /** Throws unless the text is a word of letters, digits or Chinese characters. */
    fun <T> validateGeneralWithChinese(value: T?, errorMsgTemplate: String = "The value {} must be a word", vararg params: Any?) {
        if (!isGeneralWithChinese(value?.toString())) throw exception(errorMsgTemplate, value, *params)
    }

    /** `true` when the text is a canonical UUID (8-4-4-4-12 hex groups). */
    fun isUUID(value: CharSequence?): Boolean {
        if (value == null) return false
        val text = value.toString().replace("-", "")
        if (text.length != 32) return false
        return text.all { CharUtil.isHexChar(it) }
    }

    /** Throws unless the text is a UUID. */
    fun <T> validateUUID(value: T?, errorMsgTemplate: String = "The value {} must be a UUID", vararg params: Any?) {
        if (!isUUID(value?.toString())) throw exception(errorMsgTemplate, value, *params)
    }

    /** `true` when the text is hexadecimal with an optional `0x` prefix. */
    fun isHex(value: CharSequence?): Boolean {
        if (value == null || value.isEmpty()) return false
        val text = if (value.startsWith("0x") || value.startsWith("0X")) value.substring(2) else value
        return text.isNotEmpty() && text.all { CharUtil.isHexChar(it) }
    }

    /** Throws unless the text is hexadecimal. */
    fun <T> validateHex(value: T?, errorMsgTemplate: String = "The value {} must be hexadecimal", vararg params: Any?) {
        if (!isHex(value?.toString())) throw exception(errorMsgTemplate, value, *params)
    }

    /** `true` when [value] is between [min] and [max], inclusive. */
    fun isBetween(value: Number, min: Number, max: Number): Boolean =
        value.toDouble() in min.toDouble()..max.toDouble()

    /** Throws unless [value] is between [min] and [max]. */
    fun validateBetween(value: Number, min: Number, max: Number, errorMsgTemplate: String = "The value must be between {} and {}", vararg params: Any?) {
        if (!isBetween(value, min, max)) throw exception(errorMsgTemplate, value, *params)
    }

    /** `true` when the three parts form a real calendar date. */
    fun isBirthday(year: Int, month: Int, day: Int): Boolean {
        if (year < 1900 || year > 9999 || month !in 1..12 || day < 1) return false
        val daysInMonth = when (month) {
            2 -> if (isLeapYear(year)) 29 else 28
            4, 6, 9, 11 -> 30
            else -> 31
        }
        return day <= daysInMonth
    }

    /** `true` when the text is a `yyyy-MM-dd` date that exists in the calendar. */
    fun isBirthday(value: CharSequence?): Boolean {
        if (value == null) return false
        val parts = value.split("-")
        if (parts.size != 3) return false
        val year = parts[0].toIntOrNull() ?: return false
        val month = parts[1].toIntOrNull() ?: return false
        val day = parts[2].toIntOrNull() ?: return false
        return isBirthday(year, month, day)
    }

    /** Throws unless the text is an existing calendar date. */
    fun <T> validateBirthday(value: T?, errorMsgTemplate: String = "The value {} must be a birthday", vararg params: Any?) {
        if (!isBirthday(value?.toString())) throw exception(errorMsgTemplate, value, *params)
    }

    /** Throws when [index] is outside `0..size`. */
    fun checkIndexLimit(index: Int, size: Int) {
        require(index in 0..size) { "Index must be between 0 and $size" }
    }

    private fun isLeapYear(year: Int): Boolean =
        year % 4 == 0 && (year % 100 != 0 || year % 400 == 0)

    private fun isChineseChar(ch: Char): Boolean =
        ch.code in 0x4E00..0x9FFF || ch.code in 0x3400..0x4DBF || ch.code in 0xF900..0xFAFF

    private fun exception(template: String, value: Any?, vararg params: Any?): ValidateException =
        ValidateException(io.github.shijianjs.kmphutool.core.util.StrUtil.format(template, value, *params))
}
