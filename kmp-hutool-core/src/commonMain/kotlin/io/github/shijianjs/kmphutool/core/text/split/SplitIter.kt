package io.github.shijianjs.kmphutool.core.text.split

import io.github.shijianjs.kmphutool.core.collection.ComputeIter
import io.github.shijianjs.kmphutool.core.lang.Assert
import io.github.shijianjs.kmphutool.core.text.finder.TextFinder
import io.github.shijianjs.kmphutool.core.util.StrUtil

/**
 * Lazy string splitter: separators are located only as the iterator is walked.
 *
 * Port of `cn.hutool.core.text.split.SplitIter`. [limit] caps the number of pieces (the last piece
 * holds the remainder); [ignoreEmpty] drops empty pieces.
 *
 * @hutool cn.hutool.core.text.split.SplitIter
 */
class SplitIter(
    text: CharSequence,
    private val separatorFinder: TextFinder,
    limit: Int,
    private val ignoreEmpty: Boolean,
) : ComputeIter<String>() {
    private val text: String
    private val finder: TextFinder
    private val limit: Int
    private var offset = 0
    private var count = 0

    init {
        Assert.notNull(text, "Text must be not null!")
        this.text = text.toString()
        this.finder = separatorFinder.setText(text)
        this.limit = if (limit > 0) limit else Int.MAX_VALUE
    }

    override fun computeNext(): String? {
        if (count >= limit || offset > text.length) return null

        if (count == limit - 1) {
            if (ignoreEmpty && offset == text.length) return null
            count++
            return text.substring(offset)
        }

        var result: String
        var start: Int
        do {
            start = finder.start(offset)
            if (start < 0) {
                if (offset <= text.length) {
                    result = text.substring(offset)
                    if (!ignoreEmpty || result.isNotEmpty()) {
                        offset = Int.MAX_VALUE
                        return result
                    }
                }
                return null
            }
            result = text.substring(offset, start)
            offset = finder.end(start)
        } while (ignoreEmpty && result.isEmpty())

        count++
        return result
    }

    /** Restarts the split from the beginning. */
    fun reset() {
        finder.reset()
        offset = 0
        count = 0
        resetState()
    }

    /** All pieces, optionally trimmed. */
    fun toArray(trim: Boolean): Array<String> = toList(trim).toTypedArray()

    /** All pieces as a list, optionally trimmed. */
    fun toList(trim: Boolean): List<String> = toList { if (trim) (StrUtil.trim(it) ?: it) else it }

    /** All pieces mapped through [mapping]; empty mapped values are dropped when [ignoreEmpty]. */
    fun <T> toList(mapping: (String) -> T): List<T> {
        val result = ArrayList<T>()
        while (hasNext()) {
            val value = mapping(next())
            if (ignoreEmpty && (value == null || (value is String && value.isEmpty()))) continue
            result.add(value)
        }
        return result
    }
}

