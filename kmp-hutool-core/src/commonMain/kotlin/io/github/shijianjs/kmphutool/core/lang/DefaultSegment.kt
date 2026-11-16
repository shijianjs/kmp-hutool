package io.github.shijianjs.kmphutool.core.lang

/**
 * Default [Segment] implementation.
 *
 * Port of `cn.hutool.core.lang.DefaultSegment`.
 *
 * @hutool cn.hutool.core.lang.DefaultSegment
 */
open class DefaultSegment<T : Number>(
    private val startValue: T,
    private val endValue: T,
) : Segment<T> {
    override fun getStartIndex(): T = startValue

    override fun getEndIndex(): T = endValue
}

