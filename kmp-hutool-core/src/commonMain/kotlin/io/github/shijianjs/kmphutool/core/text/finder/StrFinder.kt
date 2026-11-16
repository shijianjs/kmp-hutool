package io.github.shijianjs.kmphutool.core.text.finder

import io.github.shijianjs.kmphutool.core.lang.Assert

/**
 * Finds a literal string, optionally ignoring case.
 *
 * Port of `cn.hutool.core.text.finder.StrFinder`. Substring comparison is done character by
 * character because `commonMain` has no budget for a region-matching helper.
 *
 * @hutool cn.hutool.core.text.finder.StrFinder
 */
class StrFinder(
    private val strToFind: CharSequence,
    private val caseInsensitive: Boolean,
) : TextFinder() {

    init {
        Assert.notEmpty(strToFind)
    }

    override fun start(from: Int): Int {
        val text = Assert.notNull(this.text, "Text to find must be not null!")
        val subLength = strToFind.length
        val start = if (from < 0) 0 else from
        var limit = getValidEndIndex()
        if (negative) {
            for (i in start downTo limit + 1) {
                if (isSubEquals(text, i)) return i
            }
        } else {
            limit = limit - subLength + 1
            for (i in start until limit) {
                if (isSubEquals(text, i)) return i
            }
        }
        return Finder.INDEX_NOT_FOUND
    }

    override fun end(start: Int): Int =
        if (start < 0) Finder.INDEX_NOT_FOUND else start + strToFind.length

    private fun isSubEquals(text: CharSequence, index: Int): Boolean {
        if (index + strToFind.length > text.length) return false
        for (i in 0 until strToFind.length) {
            val a = text[index + i]
            val b = strToFind[i]
            if (caseInsensitive) {
                if (!a.equals(b, ignoreCase = true)) return false
            } else if (a != b) {
                return false
            }
        }
        return true
    }
}
