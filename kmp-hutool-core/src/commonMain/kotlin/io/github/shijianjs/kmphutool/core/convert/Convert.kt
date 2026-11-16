package io.github.shijianjs.kmphutool.core.convert

import io.github.shijianjs.kmphutool.core.util.NumberUtil
import io.github.shijianjs.kmphutool.core.util.StrUtil

/**
 * Value conversion, the `Convert` of every "turn this into that" call site.
 *
 * Port of `cn.hutool.core.convert.Convert`. Hutool selects a converter by runtime type
 * (`Class<T>` / `TypeReference`); commonMain has no runtime type tokens, so every conversion here is
 * an explicit function. Java-only conversions (`toDate`/`toLocalDateTime`/`toInstant`, `toBigDecimal`,
 * `toBigInteger`, `byte[]` codecs, the Chinese-numeral helpers) are not ported.
 *
 * @hutool cn.hutool.core.convert.Convert
 */
object Convert {
    /** `toString()` of [value], or [defaultValue] when it is `null`. */
    fun toStr(value: Any?, defaultValue: String? = null): String? =
        value?.toString() ?: defaultValue

    /** [value] as an [Int]; strings are parsed, numbers are truncated or converted. */
    fun toInt(value: Any?, defaultValue: Int? = null): Int? = when (value) {
        null -> defaultValue
        is Int -> value
        is Number -> NumberUtil.toInt(value) ?: defaultValue
        is Boolean -> if (value) 1 else 0
        else -> value.toString().trim().toIntOrNull() ?: defaultValue
    }

    /** [value] as a [Long]; strings are parsed, numbers converted. */
    fun toLong(value: Any?, defaultValue: Long? = null): Long? = when (value) {
        null -> defaultValue
        is Long -> value
        is Number -> NumberUtil.toLong(value) ?: defaultValue
        is Boolean -> if (value) 1L else 0L
        else -> value.toString().trim().toLongOrNull() ?: defaultValue
    }

    /** [value] as a [Double]; strings are parsed, numbers widened. */
    fun toDouble(value: Any?, defaultValue: Double? = null): Double? = when (value) {
        null -> defaultValue
        is Number -> value.toDouble().takeIf { it.isFinite() } ?: defaultValue
        else -> value.toString().trim().toDoubleOrNull()?.takeIf { it.isFinite() } ?: defaultValue
    }

    /** [value] as a [Float]. */
    fun toFloat(value: Any?, defaultValue: Float? = null): Float? = when (value) {
        null -> defaultValue
        is Number -> value.toFloat().takeIf { it.isFinite() } ?: defaultValue
        else -> value.toString().trim().toFloatOrNull()?.takeIf { it.isFinite() } ?: defaultValue
    }

    /** [value] as a [Short]. */
    fun toShort(value: Any?, defaultValue: Short? = null): Short? = when (value) {
        null -> defaultValue
        is Number -> value.toString().trim().toShortOrNull() ?: defaultValue
        else -> value.toString().trim().toShortOrNull() ?: defaultValue
    }

    /** [value] as a [Byte]. */
    fun toByte(value: Any?, defaultValue: Byte? = null): Byte? = when (value) {
        null -> defaultValue
        is Number -> value.toString().trim().toByteOrNull() ?: defaultValue
        else -> value.toString().trim().toByteOrNull() ?: defaultValue
    }

    /** [value] as a [Boolean]; text is true for `true`, `1`, `yes`, `y`, `on` and false for the negatives. */
    fun toBool(value: Any?, defaultValue: Boolean? = null): Boolean? = when (value) {
        null -> defaultValue
        is Boolean -> value
        is Number -> value.toDouble() != 0.0
        else -> when (value.toString().trim().lowercase()) {
            "true", "1", "yes", "y", "on" -> true
            "false", "0", "no", "n", "off" -> false
            else -> defaultValue
        }
    }

    /** [value] as a [Char]: a one-character string or the first character of a longer one. */
    fun toChar(value: Any?, defaultValue: Char? = null): Char? = when (value) {
        null -> defaultValue
        is Char -> value
        is Number -> value.toInt().toChar()
        else -> value.toString().firstOrNull() ?: defaultValue
    }

    /** [value] as a list: a collection is copied, an array is converted, a string is split on `,`. */
    fun toList(value: Any?): List<Any?> = when (value) {
        null -> emptyList()
        is List<*> -> value
        is Collection<*> -> value.toList()
        is Array<*> -> value.toList()
        is CharSequence -> StrUtil.split(value, ',', isTrim = true).filter { it.isNotEmpty() }
        else -> listOf(value)
    }

    /** [value] as a set; see [toList] for the accepted inputs. */
    fun toSet(value: Any?): Set<Any?> = toList(value).toSet()

    /**
     * A string array from [value]: a collection/array is converted, a string is split on `,`.
     */
    fun toStrArray(value: Any?): Array<String> =
        toList(value).map { it?.toString() ?: "" }.toTypedArray()

    /** [value] as an int array; elements that do not parse become `0`. */
    fun toIntArray(value: Any?): IntArray = toList(value).map { toInt(it, 0) ?: 0 }.toIntArray()

    /** An enum constant whose name equals [value] (ignoring case), or [defaultValue]. */
    inline fun <reified T : Enum<T>> toEnum(value: Any?, defaultValue: T? = null): T? {
        if (value == null) return defaultValue
        val name = value.toString().trim()
        return enumValues<T>().firstOrNull { it.name.equals(name, ignoreCase = true) } ?: defaultValue
    }

    /**
     * Converts ASCII characters to full-width forms (SBC → DBC direction reversed): `"abc"` → `"ａｂｃ"`.
     * Spaces become the ideographic space.
     */
    fun toSBC(text: CharSequence?): String? {
        if (text == null) return null
        val result = StringBuilder(text.length)
        for (ch in text) {
            result.append(
                when {
                    ch == ' ' -> '\u3000'
                    ch.code in 33..126 -> (ch.code + 0xFEE0).toChar()
                    else -> ch
                },
            )
        }
        return result.toString()
    }

    /** Converts full-width characters back to half-width: `"ａｂｃ"` → `"abc"`. */
    fun toDBC(text: CharSequence?): String? {
        if (text == null) return null
        val result = StringBuilder(text.length)
        for (ch in text) {
            result.append(
                when {
                    ch == '\u3000' -> ' '
                    ch.code in 0xFF01..0xFF5E -> (ch.code - 0xFEE0).toChar()
                    else -> ch
                },
            )
        }
        return result.toString()
    }
}
