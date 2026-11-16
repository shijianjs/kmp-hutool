package io.github.shijianjs.kmphutool.core.text.finder

/**
 * Finds a regex match. Kotlin's [Regex] replaces `java.util.regex.Pattern`/`Matcher`; the last match
 * is cached so [end] can answer without re-running the regex.
 *
 * Port of `cn.hutool.core.text.finder.PatternFinder`. Reverse search is unsupported upstream as
 * well; empty matches are reported as not found to avoid infinite replacement loops.
 *
 * @hutool cn.hutool.core.text.finder.PatternFinder
 */
class PatternFinder(private val pattern: Regex) : TextFinder() {
    private var lastMatch: MatchResult? = null

    constructor(regex: String, caseInsensitive: Boolean = false) :
        this(if (caseInsensitive) Regex(regex, RegexOption.IGNORE_CASE) else Regex(regex))

    override fun setText(text: CharSequence): TextFinder {
        lastMatch = null
        return super.setText(text)
    }

    override fun setNegative(negative: Boolean): TextFinder =
        throw UnsupportedOperationException("Negative is invalid for Pattern!")

    override fun start(from: Int): Int {
        val text = this.text ?: return Finder.INDEX_NOT_FOUND
        val match = pattern.find(text, from) ?: return Finder.INDEX_NOT_FOUND
        val end = match.range.last + 1
        if (end <= getValidEndIndex()) {
            val start = match.range.first
            if (start == end) return Finder.INDEX_NOT_FOUND
            lastMatch = match
            return start
        }
        return Finder.INDEX_NOT_FOUND
    }

    override fun end(start: Int): Int {
        val match = lastMatch ?: return Finder.INDEX_NOT_FOUND
        val end = match.range.last + 1
        val textLength = text?.length ?: 0
        val limit = if (endIndex < 0) textLength else minOf(endIndex, textLength)
        return if (end <= limit) end else Finder.INDEX_NOT_FOUND
    }

    override fun reset(): PatternFinder {
        lastMatch = null
        return this
    }
}
