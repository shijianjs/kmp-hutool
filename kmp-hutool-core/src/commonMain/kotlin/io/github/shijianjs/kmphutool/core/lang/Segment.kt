package io.github.shijianjs.kmphutool.core.lang

import kotlin.math.abs

/**
 * A numeric interval, used to describe a range inside text or a collection.
 *
 * Port of `cn.hutool.core.lang.Segment`. Java's `length()` casts the result back to `T` through the
 * converter; Kotlin cannot materialise an arbitrary `T : Number`, so the length is a [Double] here.
 *
 * @hutool cn.hutool.core.lang.Segment
 */
interface Segment<T : Number> {
    /** Start index. */
    fun getStartIndex(): T

    /** End index. */
    fun getEndIndex(): T

    /** `abs(end - start)`. */
    fun length(): Double {
        val start = getStartIndex()
        val end = getEndIndex()
        return abs(end.toDouble() - start.toDouble())
    }
}
