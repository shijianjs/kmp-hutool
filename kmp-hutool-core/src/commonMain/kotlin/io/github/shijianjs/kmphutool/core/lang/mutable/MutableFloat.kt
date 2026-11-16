package io.github.shijianjs.kmphutool.core.lang.mutable

/**
 * Mutable `Float` holder with arithmetic helpers.
 *
 * Port of `cn.hutool.core.lang.mutable.MutableFloat`. Written as a plain Kotlin class instead of a
 * `java.lang.Number` subclass so it exists on every target.
 *
 * @hutool cn.hutool.core.lang.mutable.MutableFloat
 */
class MutableFloat(value: Float = 0.0f) : Mutable<Float>, Comparable<MutableFloat> {
    private var value: Float = value

    constructor(value: Number) : this(value.toFloat())

    override fun get(): Float = value

    override fun set(value: Float) {
        this.value = value
    }

    fun increment(): MutableFloat = apply { value = (value + 1).toFloat() }

    fun decrement(): MutableFloat = apply { value = (value - 1).toFloat() }

    fun add(operand: Float): MutableFloat = apply { value = (value + operand).toFloat() }

    fun subtract(operand: Float): MutableFloat = apply { value = (value - operand).toFloat() }

    override fun compareTo(other: MutableFloat): Int = value.compareTo(other.value)

    override fun equals(other: Any?): Boolean = other is MutableFloat && value == other.value

    override fun hashCode(): Int = value.hashCode()

    override fun toString(): String = value.toString()
}
