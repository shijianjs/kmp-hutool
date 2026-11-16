package io.github.shijianjs.kmphutool.core.lang.mutable

/**
 * Mutable `boolean` holder.
 *
 * Port of `cn.hutool.core.lang.mutable.MutableBool`.
 *
 * @hutool cn.hutool.core.lang.mutable.MutableBool
 */
class MutableBool(value: Boolean = false) : Mutable<Boolean>, Comparable<MutableBool> {
    private var value: Boolean = value

    override fun get(): Boolean = value

    override fun set(value: Boolean) {
        this.value = value
    }

    /** Flips the value and returns this. */
    fun toggle(): MutableBool = apply { value = !value }

    override fun compareTo(other: MutableBool): Int = value.compareTo(other.value)

    override fun equals(other: Any?): Boolean = other is MutableBool && value == other.value

    override fun hashCode(): Int = value.hashCode()

    override fun toString(): String = value.toString()
}
