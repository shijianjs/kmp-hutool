package io.github.shijianjs.kmphutool.core.comparator

/**
 * Orders `null` explicitly, then delegates the non-null pair to the wrapped comparator.
 *
 * Port of `cn.hutool.core.comparator.NullComparator`. When the wrapped comparator is absent the two
 * values are compared through [Comparable] if both support it, otherwise they count as equal —
 * upstream's exact fallback.
 *
 * @hutool cn.hutool.core.comparator.NullComparator
 */
open class NullComparator<T>(
    private val nullGreater: Boolean,
    private val comparator: Comparator<T>?,
) : Comparator<T?> {
    override fun compare(a: T?, b: T?): Int = when {
        a === b -> 0
        a == null -> if (nullGreater) 1 else -1
        b == null -> if (nullGreater) -1 else 1
        else -> doCompare(a, b)
    }

    /** Compares two non-null values; override to customise while keeping the null rules. */
    protected open fun doCompare(a: T, b: T): Int {
        val cmp = comparator ?: return compareByComparable(a, b)
        return cmp.compare(a, b)
    }

    @Suppress("UNCHECKED_CAST")
    private fun compareByComparable(a: T, b: T): Int {
        if (a is Comparable<*> && b is Comparable<*>) {
            return (a as Comparable<Any>).compareTo(b)
        }
        return 0
    }
}
