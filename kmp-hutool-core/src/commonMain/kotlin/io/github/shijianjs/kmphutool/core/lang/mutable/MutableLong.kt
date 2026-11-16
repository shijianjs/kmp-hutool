package io.github.shijianjs.kmphutool.core.lang.mutable

/**
 * Mutable `Long` holder with arithmetic helpers.
 *
 * Port of `cn.hutool.core.lang.mutable.MutableLong`. Written as a plain Kotlin class instead of a
 * `java.lang.Number` subclass so it exists on every target.
 *
 * @hutool cn.hutool.core.lang.mutable.MutableLong
 */
class MutableLong(value: Long = 0L) : Mutable<Long>, Comparable<MutableLong> {
    private var value: Long = value

    constructor(value: Number) : this(value.toLong())

    override fun get(): Long = value

    override fun set(value: Long) {
        this.value = value
    }

    fun increment(): MutableLong = apply { value = (value + 1).toLong() }

    fun decrement(): MutableLong = apply { value = (value - 1).toLong() }

    fun add(operand: Long): MutableLong = apply { value = (value + operand).toLong() }

    fun subtract(operand: Long): MutableLong = apply { value = (value - operand).toLong() }

    override fun compareTo(other: MutableLong): Int = value.compareTo(other.value)

    override fun equals(other: Any?): Boolean = other is MutableLong && value == other.value

    override fun hashCode(): Int = value.hashCode()

    override fun toString(): String = value.toString()
}
