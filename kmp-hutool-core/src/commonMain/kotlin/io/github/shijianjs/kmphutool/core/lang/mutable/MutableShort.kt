package io.github.shijianjs.kmphutool.core.lang.mutable

/**
 * Mutable `Short` holder with arithmetic helpers.
 *
 * Port of `cn.hutool.core.lang.mutable.MutableShort`. Written as a plain Kotlin class instead of a
 * `java.lang.Number` subclass so it exists on every target.
 *
 * @hutool cn.hutool.core.lang.mutable.MutableShort
 */
class MutableShort(value: Short = 0.toShort()) : Mutable<Short>, Comparable<MutableShort> {
    private var value: Short = value

    constructor(value: Number) : this(value.toShort())

    override fun get(): Short = value

    override fun set(value: Short) {
        this.value = value
    }

    fun increment(): MutableShort = apply { value = (value + 1).toShort() }

    fun decrement(): MutableShort = apply { value = (value - 1).toShort() }

    fun add(operand: Short): MutableShort = apply { value = (value + operand).toShort() }

    fun subtract(operand: Short): MutableShort = apply { value = (value - operand).toShort() }

    override fun compareTo(other: MutableShort): Int = value.compareTo(other.value)

    override fun equals(other: Any?): Boolean = other is MutableShort && value == other.value

    override fun hashCode(): Int = value.hashCode()

    override fun toString(): String = value.toString()
}
