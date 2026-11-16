package io.github.shijianjs.kmphutool.core.lang.hash

/**
 * A 128-bit value split into low and high 64-bit halves.
 *
 * Port of `cn.hutool.core.lang.hash.Number128`. Kotlin common code has no `Number` superclass, so the
 * value exposes primitive accessors instead.
 *
 * @hutool cn.hutool.core.lang.hash.Number128
 */
class Number128(lowValue: Long, highValue: Long) {
    var lowValue: Long = lowValue
    var highValue: Long = highValue

    /** `[low, high]`. */
    fun getLongArray(): LongArray = longArrayOf(lowValue, highValue)

    fun intValue(): Int = longValue().toInt()

    fun longValue(): Long = lowValue

    fun floatValue(): Float = longValue().toFloat()

    fun doubleValue(): Double = longValue().toDouble()

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Number128) return false
        return lowValue == other.lowValue && highValue == other.highValue
    }

    override fun hashCode(): Int = 31 * lowValue.hashCode() + highValue.hashCode()

    override fun toString(): String = "Number128(low=$lowValue, high=$highValue)"
}
