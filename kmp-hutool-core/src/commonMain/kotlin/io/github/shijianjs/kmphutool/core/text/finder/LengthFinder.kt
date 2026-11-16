package io.github.shijianjs.kmphutool.core.text.finder

import io.github.shijianjs.kmphutool.core.lang.Assert

/**
 * Reports a fixed-length position, used for chunked slicing rather than content matching.
 *
 * Port of `cn.hutool.core.text.finder.LengthFinder`: the "match" is `from ± length`, clamped by the
 * text bounds.
 *
 * @hutool cn.hutool.core.text.finder.LengthFinder
 */
class LengthFinder(private val length: Int) : TextFinder() {

    init {
        Assert.assertTrue(length > 0, "Length must be great than 0")
    }

    override fun start(from: Int): Int {
        Assert.notNull(this.text, "Text to find must be not null!")
        val limit = getValidEndIndex()
        return if (negative) {
            val result = from - length
            if (result > limit) result else Finder.INDEX_NOT_FOUND
        } else {
            val result = from + length
            if (result < limit) result else Finder.INDEX_NOT_FOUND
        }
    }

    override fun end(start: Int): Int = start
}
