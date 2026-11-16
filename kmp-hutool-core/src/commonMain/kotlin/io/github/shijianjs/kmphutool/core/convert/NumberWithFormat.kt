package io.github.shijianjs.kmphutool.core.convert

/**
 * A number paired with a date/format hint.
 *
 * Port of `cn.hutool.core.convert.NumberWithFormat`. The `TypeConverter` bridge and `Convert`
 * fallback are not ported; the value accessors and comparison are.
 *
 * @hutool cn.hutool.core.convert.NumberWithFormat
 */
class NumberWithFormat(val number: Number, val format: String?) : Number(), Comparable<NumberWithFormat> {
    override fun toInt(): Int = number.toInt()

    override fun toShort(): Short = number.toShort()

    override fun toByte(): Byte = number.toByte()

    override fun toLong(): Long = number.toLong()

    override fun toFloat(): Float = number.toFloat()

    override fun toDouble(): Double = number.toDouble()

    override fun toString(): String = number.toString()

    override fun compareTo(other: NumberWithFormat): Int {
        val a = number
        val b = other.number
        return when {
            a is Int && b is Int -> a.compareTo(b)
            a is Long && b is Long -> a.compareTo(b)
            a is Double && b is Double -> a.compareTo(b)
            else -> toDouble().compareTo(other.toDouble())
        }
    }
}
