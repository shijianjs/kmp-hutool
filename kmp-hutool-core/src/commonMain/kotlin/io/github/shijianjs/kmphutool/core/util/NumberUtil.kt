package io.github.shijianjs.kmphutool.core.util

import kotlin.math.ceil
import kotlin.math.floor
import kotlin.math.pow
import kotlin.math.round
import kotlin.math.sqrt

/**
 * Number parsing, formatting, rounding and arithmetic.
 *
 * Port of `cn.hutool.core.util.NumberUtil`. Hutool computes with `java.math.BigDecimal` so that money
 * arithmetic cannot drift; commonMain has no `BigDecimal`, so the numeric helpers here work on
 * [Double] and [Long] and the KDoc on each function says which one it uses. Members that only make
 * sense with `BigDecimal` (`add(BigDecimal, ...)`, `toBigDecimal`, `divisor`, `multiple`,
 * `appendRange`, `calculate`, the `byte[]` codecs) are not ported.
 *
 * @hutool cn.hutool.core.util.NumberUtil
 */
object NumberUtil {
    /** `true` when [value] is a number literal: optional sign, digits, at most one decimal point. */
    fun isNumber(value: CharSequence?): Boolean {
        if (value == null || value.isEmpty()) return false
        var index = 0
        var seenDigit = false
        var seenDot = false
        if (value[0] == '-' || value[0] == '+') {
            index = 1
            if (value.length == 1) return false
        }
        while (index < value.length) {
            val ch = value[index]
            when {
                ch.isDigit() -> seenDigit = true
                ch == '.' && !seenDot -> seenDot = true
                else -> return false
            }
            index++
        }
        return seenDigit
    }

    /** `true` when [value] is an integral number that fits in an [Int]. */
    fun isInteger(value: CharSequence?): Boolean = value != null && value.toString().trim().toIntOrNull() != null

    /** `true` when [value] is an integral number that fits in a [Long]. */
    fun isLong(value: CharSequence?): Boolean = value != null && value.toString().trim().toLongOrNull() != null

    /** `true` when [value] parses as a finite [Double]. */
    fun isDouble(value: CharSequence?): Boolean =
        value != null && value.toString().trim().toDoubleOrNull()?.isFinite() == true

    /** Parses [value] as an [Int], returning [defaultValue] when it is absent or malformed. */
    fun parseInt(value: CharSequence?, defaultValue: Int = 0): Int =
        value?.toString()?.trim()?.toIntOrNull() ?: defaultValue

    /** Parses [value] as a [Long], returning [defaultValue] when it is absent or malformed. */
    fun parseLong(value: CharSequence?, defaultValue: Long = 0L): Long =
        value?.toString()?.trim()?.toLongOrNull() ?: defaultValue

    /** Parses [value] as a [Double], returning [defaultValue] when it is absent or malformed. */
    fun parseDouble(value: CharSequence?, defaultValue: Double = 0.0): Double {
        val parsed = value?.toString()?.trim()?.toDoubleOrNull()
        return if (parsed == null || !parsed.isFinite()) defaultValue else parsed
    }

    /** Parses [value] as a [Float], returning [defaultValue] when it is absent or malformed. */
    fun parseFloat(value: CharSequence?, defaultValue: Float = 0f): Float {
        val parsed = value?.toString()?.trim()?.toFloatOrNull()
        return if (parsed == null || !parsed.isFinite()) defaultValue else parsed
    }

    /** Parses [value] as a [Short], returning [defaultValue] when it is absent or out of range. */
    fun parseShort(value: CharSequence?, defaultValue: Short = 0): Short =
        value?.toString()?.trim()?.toShortOrNull() ?: defaultValue

    /** Parses [value] as a [Byte], returning [defaultValue] when it is absent or out of range. */
    fun parseByte(value: CharSequence?, defaultValue: Byte = 0): Byte =
        value?.toString()?.trim()?.toByteOrNull() ?: defaultValue

    /** The string form of [value], or `null`. */
    fun toStr(value: Number?): String? = value?.toString()

    /** [value] as an [Int], `null` when it is not integral or out of range. */
    fun toInt(value: Number?): Int? = when (value) {
        null -> null
        is Int -> value
        is Long -> if (value in Int.MIN_VALUE..Int.MAX_VALUE) value.toInt() else null
        is Short -> value.toInt()
        is Byte -> value.toInt()
        is Double -> if (value.isFinite() && value % 1.0 == 0.0 && value in Int.MIN_VALUE.toDouble()..Int.MAX_VALUE.toDouble()) value.toInt() else null
        is Float -> if (value.isFinite()) toInt(value.toDouble()) else null
        else -> value.toString().trim().toIntOrNull()
    }

    /** [value] as a [Long], `null` when it is not integral or out of range. */
    fun toLong(value: Number?): Long? = when (value) {
        null -> null
        is Long -> value
        is Int -> value.toLong()
        is Short -> value.toLong()
        is Byte -> value.toLong()
        is Double -> if (value.isFinite() && value % 1.0 == 0.0) value.toLong() else null
        else -> value.toString().trim().toLongOrNull()
    }

    /** [value] as a [Double], `null` when it is absent or not finite. */
    fun toDouble(value: Number?): Double? = when (value) {
        null -> null
        else -> value.toDouble().takeIf { it.isFinite() }
    }

    /** A numeric string parsed as an [Int], `null` when it is malformed. */
    fun parseNumber(value: CharSequence?): Number? {
        if (value == null) return null
        val text = value.toString().trim()
        text.toLongOrNull()?.let { return it }
        return text.toDoubleOrNull()?.takeIf { it.isFinite() }
    }

    /** The larger of two numbers, compared as [Double]. */
    fun max(number1: Number, number2: Number, vararg others: Number): Number {
        var max = maxOf(number1.toDouble(), number2.toDouble())
        others.forEach { if (it.toDouble() > max) max = it.toDouble() }
        return max
    }

    /** The smaller of two numbers, compared as [Double]. */
    fun min(number1: Number, number2: Number, vararg others: Number): Number {
        var min = minOf(number1.toDouble(), number2.toDouble())
        others.forEach { if (it.toDouble() < min) min = it.toDouble() }
        return min
    }

    /** `true` when [number] is even. */
    fun isEven(number: Number): Boolean = toLong(number)?.let { it % 2 == 0L } ?: false

    /** `true` when [number] is odd. */
    fun isOdd(number: Number): Boolean = !isEven(number)

    /** `true` when [number] is a power of two. */
    fun isPowerOfTwo(number: Int): Boolean = number > 0 && number and (number - 1) == 0

    /** `true` when [value] is prime; values below 2 are not. */
    fun isPrimes(value: Int): Boolean {
        if (value < 2) return false
        if (value % 2 == 0) return value == 2
        val limit = sqrt(value.toDouble()).toInt()
        var divisor = 3
        while (divisor <= limit) {
            if (value % divisor == 0) return false
            divisor += 2
        }
        return true
    }

    /** `value!`; throws for negative input. */
    fun factorial(value: Int): Long {
        require(value >= 0) { "Factorial of a negative number is undefined" }
        var result = 1L
        for (index in 2..value) result *= index
        return result
    }

    /** Rounds [value] to [scale] decimals, half away from zero. */
    fun round(value: Double, scale: Int): Double {
        if (!value.isFinite()) return value
        val factor = 10.0.pow(scale)
        return round(value * factor) / factor
    }

    /** Rounds [value] to [scale] decimals and formats it with exactly that many decimals. */
    fun roundStr(value: Double, scale: Int): String = format(round(value, scale), scale)

    /** Rounds [value] to [scale] decimals towards zero. */
    fun roundDown(value: Double, scale: Int): Double {
        if (!value.isFinite()) return value
        val factor = 10.0.pow(scale)
        return truncate(value * factor) / factor
    }

    /** Rounds [value] to [scale] decimals half-to-even, as `RoundingMode.HALF_EVEN` does. */
    fun roundHalfEven(value: Double, scale: Int): Double {
        if (!value.isFinite()) return value
        val factor = 10.0.pow(scale)
        val scaled = value * factor
        val floorValue = kotlin.math.floor(scaled)
        val diff = scaled - floorValue
        val rounded = when {
            diff > 0.5 -> floorValue + 1.0
            diff < 0.5 -> floorValue
            // Exactly halfway: pick the even neighbour.
            else -> if (floorValue % 2.0 == 0.0) floorValue else floorValue + 1.0
        }
        return rounded / factor
    }

    /** Formats [value] with exactly [scale] decimals, without thousands separators. */
    fun format(value: Double, scale: Int): String {
        if (!value.isFinite()) return value.toString()
        val rounded = round(value, scale)
        if (scale <= 0) return rounded.toLong().toString()
        val text = rounded.toString()
        val dot = text.indexOf('.')
        if (dot < 0) return text + "." + "0".repeat(scale)
        val decimals = text.length - dot - 1
        return if (decimals >= scale) text.substring(0, dot + scale + 1) else text + "0".repeat(scale - decimals)
    }

    /** Formats [value] as an amount: two decimals, thousands separated by `,`. */
    fun decimalFormatMoney(value: Double): String {
        val negative = value < 0
        val formatted = format(kotlin.math.abs(value), 2)
        val dot = formatted.indexOf('.')
        val integerPart = formatted.substring(0, dot)
        val decimals = formatted.substring(dot)
        val grouped = StringBuilder()
        for (index in integerPart.indices) {
            if (index > 0 && (integerPart.length - index) % 3 == 0) grouped.append(',')
            grouped.append(integerPart[index])
        }
        return (if (negative) "-" else "") + grouped + decimals
    }

    /** Formats [value] as a percentage with [scale] decimals: `0.123` → `12.30%`. */
    fun formatPercent(value: Double, scale: Int = 2): String = format(value * 100, scale) + "%"

    /** [value] rounded up to the nearest whole number. */
    fun ceil(value: Double): Long = kotlin.math.ceil(value).toLong()

    /** [value] rounded down to the nearest whole number. */
    fun floor(value: Double): Long = kotlin.math.floor(value).toLong()

    /** Division rounding up: `ceilDiv(7, 2)` is `4`. */
    fun ceilDiv(value1: Long, value2: Long): Long {
        require(value2 != 0L) { "Divisor must not be zero" }
        var quotient = value1 / value2
        if (value1 % value2 != 0L) quotient += if ((value1 > 0) == (value2 > 0)) 1 else -1
        return quotient
    }

    /** Adds two numbers as [Double]. */
    fun add(value1: Number, value2: Number): Double = value1.toDouble() + value2.toDouble()

    /** Subtracts [value2] from [value1] as [Double]. */
    fun sub(value1: Number, value2: Number): Double = value1.toDouble() - value2.toDouble()

    /** Multiplies two numbers as [Double]. */
    fun mul(value1: Number, value2: Number): Double = value1.toDouble() * value2.toDouble()

    /** Divides [value1] by [value2] and rounds the result to [scale] decimals. */
    fun div(value1: Number, value2: Number, scale: Int = 10): Double {
        val divisor = value2.toDouble()
        require(divisor != 0.0) { "Divisor must not be zero" }
        return round(value1.toDouble() / divisor, scale)
    }

    /** `true` when [value] equals [other] after both are compared as [Double]. */
    fun equals(value: Number, other: Number): Boolean = value.toDouble() == other.toDouble()

    /** Compares two numbers as [Double]: `-1`, `0` or `1`. */
    fun compare(value1: Number, value2: Number): Int = value1.toDouble().compareTo(value2.toDouble())

    /** `true` when [value] is greater than [other]. */
    fun isGreater(value: Number, other: Number): Boolean = value.toDouble() > other.toDouble()

    /** `true` when [value] is less than [other]. */
    fun isLess(value: Number, other: Number): Boolean = value.toDouble() < other.toDouble()

    /** `true` when [value] is greater than or equal to [other]. */
    fun isGreaterOrEqual(value: Number, other: Number): Boolean = value.toDouble() >= other.toDouble()

    /** `true` when [value] is less than or equal to [other]. */
    fun isLessOrEqual(value: Number, other: Number): Boolean = value.toDouble() <= other.toDouble()

    /** `true` when `min <= value <= max`. */
    fun isIn(value: Number, min: Number, max: Number): Boolean =
        value.toDouble() >= min.toDouble() && value.toDouble() <= max.toDouble()

    /** Converts an integer written in binary to an [Int]: `"101"` → `5`. */
    fun binaryToInt(binaryStr: String): Int = binaryStr.toLong(2).toInt()

    /** Converts an integer written in binary to a [Long]. */
    fun binaryToLong(binaryStr: String): Long = binaryStr.toLong(2)

    /** The binary representation of [value]. */
    fun getBinaryStr(value: Long): String = value.toString(2)

    /** `null` becomes `0`. */
    fun null2Zero(value: Long?): Long = value ?: 0L

    /** `0` becomes `1`, anything else is returned as is. */
    fun zero2One(value: Long): Long = if (value == 0L) 1L else value

    /** `null` becomes `0`. */
    fun nullToZero(value: Int?): Int = value ?: 0

    /** The part of [value] after its decimal point, as an integer: `3.14` → `14`. */
    fun partValue(value: Double): Int {
        val text = value.toString()
        val dot = text.indexOf('.')
        if (dot < 0) return 0
        return text.substring(dot + 1).toIntOrNull() ?: 0
    }

    /** A `[min, max)` range of integers. */
    fun range(min: Int, max: Int): List<Int> = (min until max).toList()

    /** A `0..(count-1)` range. */
    fun range(count: Int): List<Int> = (0 until count).toList()

    private fun truncate(value: Double): Double =
        if (value >= 0) kotlin.math.floor(value) else kotlin.math.ceil(value)
}
