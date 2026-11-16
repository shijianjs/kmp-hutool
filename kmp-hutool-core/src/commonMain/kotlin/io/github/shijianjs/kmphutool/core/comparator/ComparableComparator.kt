package io.github.shijianjs.kmphutool.core.comparator

/**
 * Compares values through their own [Comparable] implementation — the default ordering used when no
 * comparator is supplied.
 *
 * Port of `cn.hutool.core.comparator.ComparableComparator`. The Java class exposes a static `INSTANCE`
 * singleton; here the singleton is the object itself, so `ComparableComparator` is passed around
 * without a companion lookup.
 *
 * @hutool cn.hutool.core.comparator.ComparableComparator
 */
object ComparableComparator : Comparator<Any> {
    @Suppress("UNCHECKED_CAST")
    override fun compare(a: Any, b: Any): Int = (a as Comparable<Any>).compareTo(b)

    override fun hashCode(): Int = "ComparableComparator".hashCode()

    override fun toString(): String = "ComparableComparator"
}
