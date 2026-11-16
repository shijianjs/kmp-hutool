package io.github.shijianjs.kmphutool.core.text

import io.github.shijianjs.kmphutool.core.util.StrUtil

/**
 * Scores a password's strength, the way `CheckPasswordStrength` does: character classes, length,
 * repetition and a small dictionary of common passwords.
 *
 * Port of `cn.hutool.core.text.PasswdStrength`. The returned score keeps upstream's scale (`0..14`),
 * so thresholds behave identically; the enum fixes upstream's `MIDIUM` typo to [Level.MEDIUM].
 *
 * @hutool cn.hutool.core.text.PasswdStrength
 */
object PasswdStrength {
    /** Strength buckets, from weakest to strongest. */
    enum class Level { EASY, MEDIUM, STRONG, VERY_STRONG, EXTREMELY_STRONG }

    private enum class CharType { NUM, SMALL_LETTER, CAPITAL_LETTER, OTHER_CHAR }

    private val DICTIONARY = arrayOf(
        "password", "abc123", "iloveyou", "adobe123", "123123", "sunshine", "1314520",
        "a1b2c3", "123qwe", "aaa111", "qweasd", "admin", "passwd",
    )

    private val SIZE_TABLE = intArrayOf(
        9, 99, 999, 9999, 99999, 999999, 9999999, 99999999, 999999999, Int.MAX_VALUE,
    )

    /** Raw score; higher means stronger. A `null` password throws [IllegalArgumentException]. */
    fun check(passwd: String?): Int {
        requireNotNull(passwd) { "password is empty" }
        val len = passwd.length
        var level = 0

        if (countLetter(passwd, CharType.NUM) > 0) level++
        if (countLetter(passwd, CharType.SMALL_LETTER) > 0) level++
        if (len > 4 && countLetter(passwd, CharType.CAPITAL_LETTER) > 0) level++
        if (len > 6 && countLetter(passwd, CharType.OTHER_CHAR) > 0) level++

        if (len > 4 && countLetter(passwd, CharType.NUM) > 0 && countLetter(passwd, CharType.SMALL_LETTER) > 0 ||
            countLetter(passwd, CharType.NUM) > 0 && countLetter(passwd, CharType.CAPITAL_LETTER) > 0 ||
            countLetter(passwd, CharType.NUM) > 0 && countLetter(passwd, CharType.OTHER_CHAR) > 0 ||
            countLetter(passwd, CharType.SMALL_LETTER) > 0 && countLetter(passwd, CharType.CAPITAL_LETTER) > 0 ||
            countLetter(passwd, CharType.SMALL_LETTER) > 0 && countLetter(passwd, CharType.OTHER_CHAR) > 0 ||
            countLetter(passwd, CharType.CAPITAL_LETTER) > 0 && countLetter(passwd, CharType.OTHER_CHAR) > 0
        ) {
            level++
        }

        if (len > 6 && countLetter(passwd, CharType.NUM) > 0 && countLetter(passwd, CharType.SMALL_LETTER) > 0 &&
            countLetter(passwd, CharType.CAPITAL_LETTER) > 0 ||
            countLetter(passwd, CharType.NUM) > 0 && countLetter(passwd, CharType.SMALL_LETTER) > 0 &&
            countLetter(passwd, CharType.OTHER_CHAR) > 0 ||
            countLetter(passwd, CharType.NUM) > 0 && countLetter(passwd, CharType.CAPITAL_LETTER) > 0 &&
            countLetter(passwd, CharType.OTHER_CHAR) > 0 ||
            countLetter(passwd, CharType.SMALL_LETTER) > 0 && countLetter(passwd, CharType.CAPITAL_LETTER) > 0 &&
            countLetter(passwd, CharType.OTHER_CHAR) > 0
        ) {
            level++
        }

        if (len > 8 && countLetter(passwd, CharType.NUM) > 0 && countLetter(passwd, CharType.SMALL_LETTER) > 0 &&
            countLetter(passwd, CharType.CAPITAL_LETTER) > 0 && countLetter(passwd, CharType.OTHER_CHAR) > 0
        ) {
            level++
        }

        if (len > 6 && countLetter(passwd, CharType.NUM) >= 3 && countLetter(passwd, CharType.SMALL_LETTER) >= 3 ||
            countLetter(passwd, CharType.NUM) >= 3 && countLetter(passwd, CharType.CAPITAL_LETTER) >= 3 ||
            countLetter(passwd, CharType.NUM) >= 3 && countLetter(passwd, CharType.OTHER_CHAR) >= 2 ||
            countLetter(passwd, CharType.SMALL_LETTER) >= 3 && countLetter(passwd, CharType.CAPITAL_LETTER) >= 3 ||
            countLetter(passwd, CharType.SMALL_LETTER) >= 3 && countLetter(passwd, CharType.OTHER_CHAR) >= 2 ||
            countLetter(passwd, CharType.CAPITAL_LETTER) >= 3 && countLetter(passwd, CharType.OTHER_CHAR) >= 2
        ) {
            level++
        }

        if (len > 8 && countLetter(passwd, CharType.NUM) >= 2 && countLetter(passwd, CharType.SMALL_LETTER) >= 2 &&
            countLetter(passwd, CharType.CAPITAL_LETTER) >= 2 ||
            countLetter(passwd, CharType.NUM) >= 2 && countLetter(passwd, CharType.SMALL_LETTER) >= 2 &&
            countLetter(passwd, CharType.OTHER_CHAR) >= 2 ||
            countLetter(passwd, CharType.NUM) >= 2 && countLetter(passwd, CharType.CAPITAL_LETTER) >= 2 &&
            countLetter(passwd, CharType.OTHER_CHAR) >= 2 ||
            countLetter(passwd, CharType.SMALL_LETTER) >= 2 && countLetter(passwd, CharType.CAPITAL_LETTER) >= 2 &&
            countLetter(passwd, CharType.OTHER_CHAR) >= 2
        ) {
            level++
        }

        if (len > 10 && countLetter(passwd, CharType.NUM) >= 2 && countLetter(passwd, CharType.SMALL_LETTER) >= 2 &&
            countLetter(passwd, CharType.CAPITAL_LETTER) >= 2 && countLetter(passwd, CharType.OTHER_CHAR) >= 2
        ) {
            level++
        }

        if (countLetter(passwd, CharType.OTHER_CHAR) >= 3) level++
        if (countLetter(passwd, CharType.OTHER_CHAR) >= 6) level++

        if (len > 12) {
            level++
            if (len >= 16) level++
        }

        // Penalties: sequential alphabets, keyboard rows, ascending/descending digits, one repeated
        // character, and the small dictionary above.
        if ("abcdefghijklmnopqrstuvwxyz".contains(passwd) || "ABCDEFGHIJKLMNOPQRSTUVWXYZ".contains(passwd)) {
            level--
        }
        if ("qwertyuiop".contains(passwd) || "asdfghjkl".contains(passwd) || "zxcvbnm".contains(passwd)) {
            level--
        }
        if (StrUtil.isNumeric(passwd) && ("01234567890".contains(passwd) || "09876543210".contains(passwd))) {
            level--
        }

        if (countLetter(passwd, CharType.NUM) == len ||
            countLetter(passwd, CharType.SMALL_LETTER) == len ||
            countLetter(passwd, CharType.CAPITAL_LETTER) == len
        ) {
            level--
        }

        if (len % 2 == 0) {
            val part1 = passwd.substring(0, len / 2)
            val part2 = passwd.substring(len / 2)
            if (part1 == part2) level--
            if (isCharEquals(part1) && isCharEquals(part2)) level--
        }
        if (len % 3 == 0) {
            val part1 = passwd.substring(0, len / 3)
            val part2 = passwd.substring(len / 3, len / 3 * 2)
            val part3 = passwd.substring(len / 3 * 2)
            if (part1 == part2 && part2 == part3) level--
        }

        if (StrUtil.isNumeric(passwd) && len in 6..8) {
            var year = 0
            if (len == 8 || len == 6) year = passwd.substring(0, len - 4).toInt()
            val size = sizeOfInt(year)
            val month = passwd.substring(size, size + 2).toInt()
            val day = passwd.substring(size + 2, len).toInt()
            if (year in 1950..2049 && month in 1..12 && day in 1..31) level--
        }

        for (s in DICTIONARY) {
            if (passwd == s || s.contains(passwd)) {
                level--
                break
            }
        }

        if (len <= 6) {
            level--
            if (len <= 4) {
                level--
                if (len <= 3) level = 0
            }
        }

        if (isCharEquals(passwd)) level = 0
        return if (level < 0) 0 else level
    }

    /** Buckets [check] into [Level]; upstream's thresholds, including `10..12` for VERY_STRONG. */
    fun getLevel(passwd: String?): Level = when (check(passwd)) {
        in 0..3 -> Level.EASY
        in 4..6 -> Level.MEDIUM
        in 7..9 -> Level.STRONG
        in 10..12 -> Level.VERY_STRONG
        else -> Level.EXTREMELY_STRONG
    }

    private fun checkCharacterType(c: Char): CharType = when {
        c in '0'..'9' -> CharType.NUM
        c in 'A'..'Z' -> CharType.CAPITAL_LETTER
        c in 'a'..'z' -> CharType.SMALL_LETTER
        else -> CharType.OTHER_CHAR
    }

    private fun countLetter(passwd: String?, type: CharType): Int {
        if (passwd == null) return 0
        var count = 0
        for (c in passwd) if (checkCharacterType(c) == type) count++
        return count
    }

    private fun sizeOfInt(x: Int): Int {
        for (i in SIZE_TABLE.indices) if (x <= SIZE_TABLE[i]) return i + 1
        return SIZE_TABLE.size
    }

    /** `true` when every character of the (non-empty) string is the same. */
    private fun isCharEquals(cs: String): Boolean {
        if (cs.isEmpty()) return false
        val first = cs[0]
        return cs.all { it == first }
    }
}
