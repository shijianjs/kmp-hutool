package io.github.shijianjs.kmphutool.core.util

/**
 * Boolean helpers: parsing the many spellings of "true", negating nullable values, folding arrays
 * with and/or/xor, and rendering booleans as other types.
 *
 * Port of `cn.hutool.core.util.BooleanUtil`.
 *
 * Kotlin has no boxed primitives, so upstream's pairs such as `toInt`/`toInteger`,
 * `toChar`/`toCharacter` or `toStringTrueFalse` collapse into one function each; the primitive
 * versions remain and return the same values. `isBoolean(Class)` is dropped because Kotlin has no
 * `Class` in `commonMain`; use `value is Boolean` at the call site.
 *
 * @hutool cn.hutool.core.util.BooleanUtil
 */
object BooleanUtil {
    /** Spellings accepted as `true`, matching upstream, including the Chinese ones. */
    private val TRUE_SET: Set<String> = setOf(
        "true", "yes", "y", "t", "ok", "correct", "success", "on", "1",
        "是", "对", "真", "對", "正确", "开", "开启", "√", "☑",
    )

    /** Spellings accepted as `false`, matching upstream, including the Chinese ones. */
    private val FALSE_SET: Set<String> = setOf(
        "false", "no", "n", "f", "wrong", "fail", "off", "0",
        "否", "错", "假", "錯", "错误", "关", "关闭", "×", "☒",
    )

    /** The opposite value, or `null` for `null`. */
    fun negate(bool: Boolean?): Boolean? = when (bool) {
        null -> null
        true -> false
        false -> true
    }

    /** `true` only when the boxed value is exactly `true`; `null` counts as false. */
    fun isTrue(bool: Boolean?): Boolean = bool == true

    /** `true` only when the boxed value is exactly `false`; `null` counts as false. */
    fun isFalse(bool: Boolean?): Boolean = bool == false

    /** `true` when [valueStr] trims and lowercases to one of the accepted spellings. */
    fun toBoolean(valueStr: String?): Boolean {
        if (valueStr == null || valueStr.isBlank()) return false
        return TRUE_SET.contains(valueStr.trim().lowercase())
    }

    /** `true`/`false` for a recognised spelling, `null` for anything else or a blank input. */
    fun toBooleanObject(valueStr: String?): Boolean? {
        if (valueStr == null || valueStr.isBlank()) return null
        val normalized = valueStr.trim().lowercase()
        if (TRUE_SET.contains(normalized)) return true
        if (FALSE_SET.contains(normalized)) return false
        return null
    }

    /** `1` for `true`, `0` for `false`. */
    fun toInt(value: Boolean): Int = if (value) 1 else 0

    /** `'\u0001'` for `true`, `'\u0000'` for `false`. */
    fun toChar(value: Boolean): Char = toInt(value).toChar()

    /** `1b` for `true`, `0b` for `false`. */
    fun toByte(value: Boolean): Byte = toInt(value).toByte()

    /** `1L` for `true`, `0L` for `false`. */
    fun toLong(value: Boolean): Long = toInt(value).toLong()

    /** `1` for `true`, `0` for `false`. */
    fun toShort(value: Boolean): Short = toInt(value).toShort()

    /** `1.0f` for `true`, `0.0f` for `false`. */
    fun toFloat(value: Boolean): Float = toInt(value).toFloat()

    /** `1.0` for `true`, `0.0` for `false`. */
    fun toDouble(value: Boolean): Double = toInt(value).toDouble()

    /** `"true"` / `"false"`. */
    fun toStringTrueFalse(bool: Boolean): String = if (bool) "true" else "false"

    /** `"on"` / `"off"`. */
    fun toStringOnOff(bool: Boolean): String = if (bool) "on" else "off"

    /** `"yes"` / `"no"`. */
    fun toStringYesNo(bool: Boolean): String = if (bool) "yes" else "no"

    /** [trueString] for `true`, [falseString] for `false`. */
    fun toString(bool: Boolean, trueString: String, falseString: String): String =
        if (bool) trueString else falseString

    /** Like the primitive overload, but `null` yields [nullString]. */
    fun toString(bool: Boolean?, trueString: String, falseString: String, nullString: String?): String? =
        when (bool) {
            null -> nullString
            true -> trueString
            false -> falseString
        }

    /** Logical and over a non-empty array; throws [IllegalArgumentException] for an empty one. */
    fun and(vararg array: Boolean): Boolean {
        require(array.isNotEmpty()) { "The Array must not be empty !" }
        return array.all { it }
    }

    /** Logical and over a non-empty boxed array; a `null` element counts as false. */
    fun andOfWrap(vararg array: Boolean?): Boolean {
        require(array.isNotEmpty()) { "The Array must not be empty !" }
        return array.all { isTrue(it) }
    }

    /** Logical or over a non-empty array; throws [IllegalArgumentException] for an empty one. */
    fun or(vararg array: Boolean): Boolean {
        require(array.isNotEmpty()) { "The Array must not be empty !" }
        return array.any { it }
    }

    /** Logical or over a non-empty boxed array; only `true` counts. */
    fun orOfWrap(vararg array: Boolean?): Boolean {
        require(array.isNotEmpty()) { "The Array must not be empty !" }
        return array.any { isTrue(it) }
    }

    /**
     * Logical xor over a non-empty array: `true` when the number of `true` elements is odd.
     *
     * This is parity, not "exactly one true" — use [exactlyOneTrue] for mutual exclusion.
     */
    fun xor(vararg array: Boolean): Boolean {
        require(array.isNotEmpty()) { "The Array must not be empty" }
        var result = false
        for (element in array) result = result xor element
        return result
    }

    /** `true` when exactly one element is `true`; several `true`s return `false` immediately. */
    fun exactlyOneTrue(vararg array: Boolean): Boolean {
        require(array.isNotEmpty()) { "The Array must not be empty" }
        var count = 0
        for (b in array) {
            if (b) {
                count++
                if (count > 1) return false
            }
        }
        return count == 1
    }

    /** Logical xor over a non-empty boxed array; a `null` element counts as false. */
    fun xorOfWrap(vararg array: Boolean?): Boolean {
        require(array.isNotEmpty()) { "The Array must not be empty !" }
        return xor(*array.map { isTrue(it) }.toBooleanArray())
    }
}
