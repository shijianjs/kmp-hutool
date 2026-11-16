package io.github.shijianjs.kmphutool.core.text.finder

import io.github.shijianjs.kmphutool.core.lang.Assert

/**
 * Finds the next position accepted by a [io.github.shijianjs.kmphutool.core.lang.Matcher], e.g. a
 * class of characters such as digits.
 *
 * Port of `cn.hutool.core.text.finder.CharMatcherFinder`.
 *
 * @hutool cn.hutool.core.text.finder.CharMatcherFinder
 */
class CharMatcherFinder(private val matcher: io.github.shijianjs.kmphutool.core.lang.Matcher<Char>) :
    TextFinder() {

    override fun start(from: Int): Int {
        val text = Assert.notNull(this.text, "Text to find must be not null!")
        val limit = getValidEndIndex()
        if (negative) {
            for (i in from downTo limit + 1) {
                if (matcher.match(text[i])) return i
            }
        } else {
            for (i in from until limit) {
                if (matcher.match(text[i])) return i
            }
        }
        return Finder.INDEX_NOT_FOUND
    }

    override fun end(start: Int): Int = if (start < 0) Finder.INDEX_NOT_FOUND else start + 1
}
