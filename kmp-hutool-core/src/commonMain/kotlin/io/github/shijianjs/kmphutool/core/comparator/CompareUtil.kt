package io.github.shijianjs.kmphutool.core.comparator

/**
 * Null-safe comparison helpers.
 *
 * Port of `cn.hutool.core.comparator.CompareUtil`. Kotlin's `compareValues` already behaves the
 * same way; the class stays for parity with Hutool code and for the explicit `nullGreater` flag.
 *
 * @hutool cn.hutool.core.comparator.CompareUtil
 */
object CompareUtil {
    /** Compares [a] and [b], treating `null` as smaller than any value. */
    fun <T : Comparable<T>> compare(a: T?, b: T?): Int = compare(a, b, false)

    /** Compares [a] and [b]; `null` is the largest value when [nullGreater] is `true`. */
    fun <T : Comparable<T>> compare(a: T?, b: T?, nullGreater: Boolean): Int = when {
        a === b -> 0
        a == null -> if (nullGreater) 1 else -1
        b == null -> if (nullGreater) -1 else 1
        else -> a.compareTo(b)
    }

    /** Compares [a] and [b] with [comparator], `null` treated as smaller. */
    fun <T> compare(a: T?, b: T?, comparator: Comparator<in T>): Int = compare(a, b, false, comparator)

    /** Compares [a] and [b] with [comparator], honouring [nullGreater]. */
    fun <T> compare(a: T?, b: T?, nullGreater: Boolean, comparator: Comparator<in T>): Int = when {
        a === b -> 0
        a == null -> if (nullGreater) 1 else -1
        b == null -> if (nullGreater) -1 else 1
        else -> comparator.compare(a, b)
    }

    /** The larger of [a] and [b], `null` losing against any value. */
    fun <T : Comparable<T>> max(a: T?, b: T?): T? = when {
        a == null -> b
        b == null -> a
        a >= b -> a
        else -> b
    }

    /** The smaller of [a] and [b], `null` losing against any value. */
    fun <T : Comparable<T>> min(a: T?, b: T?): T? = when {
        a == null -> b
        b == null -> a
        a <= b -> a
        else -> b
    }
}
