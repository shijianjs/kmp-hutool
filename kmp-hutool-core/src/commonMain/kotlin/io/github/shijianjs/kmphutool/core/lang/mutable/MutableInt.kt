package io.github.shijianjs.kmphutool.core.lang.mutable

/**
 * Mutable `int` with the arithmetic helpers Hutool's holder offers.
 *
 * Port of `cn.hutool.core.lang.mutable.MutableInt`, minus the `Number` inheritance — Kotlin call
 * sites use [get] and the arithmetic methods instead.
 *
 * @hutool cn.hutool.core.lang.mutable.MutableInt
 */
class MutableInt(value: Int = 0) : Mutable<Int>, Comparable<MutableInt> {
    private var value: Int = value

    constructor(value: Number) : this(value.toInt())
    constructor(value: String) : this(value.toInt())

    override fun get(): Int = value

    override fun set(value: Int) {
        this.value = value
    }

    fun set(value: Number) {
        this.value = value.toInt()
    }

    fun increment(): MutableInt = apply { value++ }

    fun decrement(): MutableInt = apply { value-- }

    fun add(operand: Int): MutableInt = apply { value += operand }

    fun add(operand: Number): MutableInt = add(operand.toInt())

    fun subtract(operand: Int): MutableInt = apply { value -= operand }

    fun subtract(operand: Number): MutableInt = subtract(operand.toInt())

    override fun compareTo(other: MutableInt): Int = value.compareTo(other.value)

    override fun equals(other: Any?): Boolean = other is MutableInt && value == other.value

    override fun hashCode(): Int = value

    override fun toString(): String = value.toString()
}
