package io.github.shijianjs.kmphutool.core.lang.mutable

/**
 * Mutable `Double` holder with arithmetic helpers.
 *
 * Port of `cn.hutool.core.lang.mutable.MutableDouble`. Written as a plain Kotlin class instead of a
 * `java.lang.Number` subclass so it exists on every target.
 *
 * @hutool cn.hutool.core.lang.mutable.MutableDouble
 */
class MutableDouble(value: Double = 0.0) : Mutable<Double>, Comparable<MutableDouble> {
    private var value: Double = value

    constructor(value: Number) : this(value.toDouble())

    override fun get(): Double = value

    override fun set(value: Double) {
        this.value = value
    }

    fun increment(): MutableDouble = apply { value = (value + 1).toDouble() }

    fun decrement(): MutableDouble = apply { value = (value - 1).toDouble() }

    fun add(operand: Double): MutableDouble = apply { value = (value + operand).toDouble() }

    fun subtract(operand: Double): MutableDouble = apply { value = (value - operand).toDouble() }

    override fun compareTo(other: MutableDouble): Int = value.compareTo(other.value)

    override fun equals(other: Any?): Boolean = other is MutableDouble && value == other.value

    override fun hashCode(): Int = value.hashCode()

    override fun toString(): String = value.toString()
}
