package io.github.shijianjs.kmphutool.core.comparator

/**
 * A [Comparator] backed by a Kotlin function, with upstream's null placement.
 *
 * Port of `cn.hutool.core.comparator.FuncComparator`. The Java version wraps a
 * `java.util.function.IntFunction`; the port takes a plain `(T, T) -> Int` and, when
 * [nullGreater] is `false`, puts `null` first instead of calling the function with it.
 *
 * @hutool cn.hutool.core.comparator.FuncComparator
 */
class FuncComparator<T>(
    private val compareFunc: (T, T) -> Int,
    private val nullGreater: Boolean = false,
) : Comparator<T?> {
    override fun compare(a: T?, b: T?): Int = when {
        a === b -> 0
        a == null -> if (nullGreater) 1 else -1
        b == null -> if (nullGreater) -1 else 1
        else -> compareFunc(a, b)
    }
}
