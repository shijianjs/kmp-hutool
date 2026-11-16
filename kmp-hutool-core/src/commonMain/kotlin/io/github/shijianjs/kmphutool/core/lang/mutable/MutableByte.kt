package io.github.shijianjs.kmphutool.core.lang.mutable

/**
 * Mutable `Byte` holder with arithmetic helpers.
 *
 * Port of `cn.hutool.core.lang.mutable.MutableByte`. Written as a plain Kotlin class instead of a
 * `java.lang.Number` subclass so it exists on every target.
 *
 * @hutool cn.hutool.core.lang.mutable.MutableByte
 */
class MutableByte(value: Byte = 0.toByte()) : Mutable<Byte>, Comparable<MutableByte> {
    private var value: Byte = value

    constructor(value: Number) : this(value.toByte())

    override fun get(): Byte = value

    override fun set(value: Byte) {
        this.value = value
    }

    fun increment(): MutableByte = apply { value = (value + 1).toByte() }

    fun decrement(): MutableByte = apply { value = (value - 1).toByte() }

    fun add(operand: Byte): MutableByte = apply { value = (value + operand).toByte() }

    fun subtract(operand: Byte): MutableByte = apply { value = (value - operand).toByte() }

    override fun compareTo(other: MutableByte): Int = value.compareTo(other.value)

    override fun equals(other: Any?): Boolean = other is MutableByte && value == other.value

    override fun hashCode(): Int = value.hashCode()

    override fun toString(): String = value.toString()
}
