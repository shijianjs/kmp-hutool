package io.github.shijianjs.kmphutool.core.lang

/**
 * An inclusive `[min, max]` range over a [Comparable] type.
 *
 * Port of `cn.hutool.core.lang.Range`. The Java class is a `Supplier` that walks a stepped range;
 * the port keeps the range itself ([contains], [containsAll]) and leaves iteration to Kotlin
 * sequences built from [step]. `toString` uses the same `min ~ max` form as upstream.
 *
 * @hutool cn.hutool.core.lang.Range
 */
class Range<T : Comparable<T>>(
    /** The lower bound, inclusive. */
    val min: T,
    /** The upper bound, inclusive. */
    val max: T,
) {
    init {
        require(min <= max) { "min must be <= max" }
    }

    /** `true` when [value] is inside `min..max`. */
    fun contains(value: T): Boolean = value in min..max

    /** `true` when every value of [other] is inside this range. */
    fun containsAll(other: Range<T>): Boolean = other.min in min..max && other.max in min..max

    override fun equals(other: Any?): Boolean = other is Range<*> && min == other.min && max == other.max

    override fun hashCode(): Int = 31 * min.hashCode() + max.hashCode()

    override fun toString(): String = "$min ~ $max"

    companion object {
        /** A range from [min] to [max]. */
        fun <T : Comparable<T>> of(min: T, max: T): Range<T> = Range(min, max)
    }
}
