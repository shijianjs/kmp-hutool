package io.github.shijianjs.kmphutool.core.util

import io.github.shijianjs.kmphutool.core.text.StrPool

/**
 * Character-level predicates and conversions that the string utilities are built on.
 *
 * Port of `cn.hutool.core.util.CharUtil`. Two members of the Java class are deliberately absent:
 * `isCharClass(Class)` is reflection over Java types and `getType(int)` returns
 * `Character.getType` constants — the port exposes [category], which returns the Kotlin
 * [CharCategory] of a char instead of an `int` whose meaning depends on the JDK.
 *
 * @hutool cn.hutool.core.util.CharUtil
 */
object CharUtil {
    /** `true` when [ch] is an ASCII character (0..127). */
    fun isAscii(ch: Char): Boolean = ch.code < 128

    /** `true` when [ch] is a printable ASCII character (32..126). */
    fun isAsciiPrintable(ch: Char): Boolean = ch.code in 32..126

    /** `true` when [ch] is an ASCII control character (0..31 or 127). */
    fun isAsciiControl(ch: Char): Boolean = ch.code < 32 || ch.code == 127

    /** `true` when [ch] is `A`-`Z` or `a`-`z`. */
    fun isLetter(ch: Char): Boolean = isLetterUpper(ch) || isLetterLower(ch)

    /** `true` when [ch] is `A`-`Z`. */
    fun isLetterUpper(ch: Char): Boolean = ch in 'A'..'Z'

    /** `true` when [ch] is `a`-`z`. */
    fun isLetterLower(ch: Char): Boolean = ch in 'a'..'z'

    /** `true` when [ch] is `0`-`9`. */
    fun isNumber(ch: Char): Boolean = ch in '0'..'9'

    /** `true` when [ch] is a hexadecimal digit (`0`-`9`, `a`-`f`, `A`-`F`). */
    fun isHexChar(ch: Char): Boolean = isNumber(ch) || ch in 'a'..'f' || ch in 'A'..'F'

    /** `true` when [ch] is a letter or a digit. */
    fun isLetterOrNumber(ch: Char): Boolean = isLetter(ch) || isNumber(ch)

    /** Returns [ch] as a one-character string. */
    fun toString(ch: Char): String = ch.toString()

    /**
     * `true` when [ch] counts as blank: a Unicode whitespace, a space character, a zero-width
     * character or one of the fillers that only look empty.
     */
    fun isBlankChar(ch: Char): Boolean {
        if (ch.isWhitespace()) return true
        return when (ch) {
            '\u00A0', '\u1680', '\u2000', '\u2001', '\u2002', '\u2003', '\u2004', '\u2005',
            '\u2006', '\u2007', '\u2008', '\u2009', '\u200A', '\u202F', '\u205F', '\u3000',
            '\uFEFF', '\u202A', '\u0000', '\u3164', '\u2800', '\u200C', '\u180E',
            -> true

            else -> false
        }
    }

    /**
     * `true` when the code point [c] counts as blank. Code points outside the BMP (surrogates) are
     * never blank, which matches the Java overload taking an `int`.
     */
    fun isBlankChar(c: Int): Boolean =
        if (c in 0..0xFFFF) isBlankChar(c.toChar()) else false

    /**
     * `true` when [ch] is part of an emoji sequence.
     *
     * The Java version asks whether the char is *not* in the printable, tab, LF, CR ranges; the
     * port keeps that rule so a surrogate half of a pair is recognised the same way.
     */
    fun isEmoji(ch: Char): Boolean {
        val code = ch.code
        val printable =
            code == 0x9 || code == 0xA || code == 0xD || code in 0x20..0xD7FF ||
                code in 0xE000..0xFFFD
        return !printable
    }

    /** `true` when [ch] is `/` or `\`, the separators used by Windows and Unix paths. */
    fun isFileSeparator(ch: Char): Boolean = ch == StrPool.SLASH || ch == StrPool.BACKSLASH

    /** Compares two characters, optionally ignoring case. */
    fun equals(c1: Char, c2: Char, caseInsensitive: Boolean): Boolean =
        if (caseInsensitive) c1.lowercaseChar() == c2.lowercaseChar() else c1 == c2

    /** The Unicode general category of [ch]; the Kotlin replacement for `Character.getType`. */
    fun category(ch: Char): CharCategory = ch.category

    /**
     * The hexadecimal value of [ch] in base 16, or `-1` when [ch] is not a hexadecimal digit.
     *
     * Kotlin has no `Character.digit`; the port uses `digitToIntOrNull(16)`, which accepts the same
     * characters and additionally ignores nothing (the Java version accepts full-width digits).
     */
    fun digit16(ch: Char): Int = ch.digitToIntOrNull(16) ?: -1

    /**
     * Converts a plain letter or digit to its enclosed form: `'1'` → `'①'`, `'A'` → `'Ⓐ'`,
     * `'a'` → `'ⓐ'`. Characters with no enclosed form are returned unchanged.
     */
    fun toCloseChar(ch: Char): Char = when {
        ch in '1'..'9' -> '①' + (ch - '1')
        ch in 'A'..'Z' -> 'Ⓐ' + (ch - 'A')
        ch in 'a'..'z' -> 'ⓐ' + (ch - 'a')
        else -> ch
    }

    /**
     * Converts a number in `1..20` to its enclosed form: `1` → `'①'`, `20` → `'⑳'`.
     *
     * @throws IllegalArgumentException when [number] is outside `1..20`
     */
    fun toCloseByNumber(number: Int): Char {
        require(number in 1..20) { "Number must be [1-20]" }
        return '①' + (number - 1)
    }
}
