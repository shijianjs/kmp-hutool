package io.github.shijianjs.kmphutool.core.text.finder

import io.github.shijianjs.kmphutool.core.lang.Assert

/**
 * Base class for finders that walk a [CharSequence].
 *
 * Port of `cn.hutool.core.text.finder.TextFinder`: [endIndex] is exclusive and may be left at `-1`
 * to mean "until the end", with [negative] switching the search direction.
 *
 * @hutool cn.hutool.core.text.finder.TextFinder
 */
abstract class TextFinder : Finder {
    protected var text: CharSequence? = null
    protected var endIndex: Int = -1
    protected var negative: Boolean = false

    open fun setText(text: CharSequence): TextFinder {
        this.text = Assert.notNull(text, "Text must be not null!")
        return this
    }

    open fun setEndIndex(endIndex: Int): TextFinder {
        this.endIndex = endIndex
        return this
    }

    open fun setNegative(negative: Boolean): TextFinder {
        this.negative = negative
        return this
    }

    /**
     * The effective exclusive end index: `-1` in reverse mode, otherwise the smaller of [endIndex]
     * and the text length.
     */
    protected fun getValidEndIndex(): Int {
        if (negative && endIndex == -1) return -1
        val length = text?.length ?: 0
        return if (endIndex < 0) endIndex + length + 1 else minOf(endIndex, length)
    }
}
