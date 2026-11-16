package io.github.shijianjs.kmphutool.core.text.finder

import io.github.shijianjs.kmphutool.core.lang.Assert

/**
 * Finds a single character, optionally ignoring case.
 *
 * Port of `cn.hutool.core.text.finder.CharFinder`.
 *
 * @hutool cn.hutool.core.text.finder.CharFinder
 */
class CharFinder(
    private val c: Char,
    private val caseInsensitive: Boolean = false,
) : TextFinder() {

    override fun start(from: Int): Int {
        val text = Assert.notNull(this.text, "Text to find must be not null!")
        val limit = getValidEndIndex()
        if (negative) {
            for (i in from downTo limit + 1) {
                if (equalsChar(text[i])) return i
            }
        } else {
            for (i in from until limit) {
                if (equalsChar(text[i])) return i
            }
        }
        return Finder.INDEX_NOT_FOUND
    }

    override fun end(start: Int): Int = if (start < 0) Finder.INDEX_NOT_FOUND else start + 1

    private fun equalsChar(other: Char): Boolean =
        if (caseInsensitive) c.equals(other, ignoreCase = true) else c == other
}
