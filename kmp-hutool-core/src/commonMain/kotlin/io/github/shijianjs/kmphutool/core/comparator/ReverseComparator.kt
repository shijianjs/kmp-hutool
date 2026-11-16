package io.github.shijianjs.kmphutool.core.comparator

/**
 * Reverses the order of the wrapped comparator.
 *
 * Port of `cn.hutool.core.comparator.ReverseComparator`. A `null` comparator means the natural
 * [ComparableComparator], as upstream does.
 *
 * @hutool cn.hutool.core.comparator.ReverseComparator
 */
class ReverseComparator<E>(comparator: Comparator<in E>? = null) : Comparator<E> {
    @Suppress("UNCHECKED_CAST")
    private val comparator: Comparator<in E> =
        comparator ?: (ComparableComparator as Comparator<in E>)

    override fun compare(a: E, b: E): Int = comparator.compare(b, a)

    override fun hashCode(): Int = "ReverseComparator".hashCode() xor comparator.hashCode()

    override fun equals(other: Any?): Boolean =
        this === other || (other is ReverseComparator<*> && comparator == other.comparator)

    override fun toString(): String = "ReverseComparator($comparator)"
}
