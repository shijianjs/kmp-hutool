package io.github.shijianjs.kmphutool.core.text

import io.github.shijianjs.kmphutool.core.util.CharUtil

/**
 * Splits strings into parts: by a literal separator, by a regular expression, by blank characters or
 * by a fixed length.
 *
 * Port of `cn.hutool.core.text.StrSplitter`. The separator is matched as a whole string, which is
 * the way Hutool's own `Separator`-based overload behaves for multi-character separators.
 *
 * @hutool cn.hutool.core.text.StrSplitter
 */
object StrSplitter {
    /**
     * Splits [text] around [separator].
     *
     * @param text the text to split; `null` produces an empty list
     * @param separator the string to split on
     * @param limit the maximum number of parts; `-1` (the default) means no limit and the trailing
     *   empty part is dropped, a positive value keeps the remainder in the last part
     * @param isTrim whether to trim every part
     * @param ignoreEmpty whether to drop empty parts
     * @param ignoreCase whether to match the separator without regard to case
     */
    fun split(
        text: CharSequence?,
        separator: CharSequence,
        limit: Int = -1,
        isTrim: Boolean = false,
        ignoreEmpty: Boolean = false,
        ignoreCase: Boolean = false,
    ): List<String> {
        if (text == null || text.isEmpty() && !ignoreEmpty) return if (text == null) emptyList() else listOf("")
        val parts = splitParts(text, separator.toString(), limit, isTrim, ignoreEmpty, ignoreCase)
        // A non-positive limit drops the trailing empty part, as Java's String.split does.
        if (limit <= 0 && parts.isNotEmpty() && parts.last().isEmpty()) {
            return parts.dropLast(1)
        }
        return parts
    }

    /** Splits [text] by [separator] and returns the parts as an array. See [split]. */
    fun splitToArray(
        text: CharSequence?,
        separator: CharSequence,
        limit: Int = -1,
        isTrim: Boolean = false,
        ignoreEmpty: Boolean = false,
        ignoreCase: Boolean = false,
    ): Array<String> = split(text, separator, limit, isTrim, ignoreEmpty, ignoreCase).toTypedArray()

    /** Splits [text] by [separator], trimming every part. */
    fun splitTrim(
        text: CharSequence?,
        separator: CharSequence,
        ignoreEmpty: Boolean = true,
        limit: Int = -1,
    ): List<String> = split(text, separator, limit, isTrim = true, ignoreEmpty = ignoreEmpty)

    /** Splits [text] by [separator], matching without regard to case. */
    fun splitIgnoreCase(
        text: CharSequence?,
        separator: CharSequence,
        limit: Int = -1,
        isTrim: Boolean = false,
        ignoreEmpty: Boolean = false,
    ): List<String> = split(text, separator, limit, isTrim, ignoreEmpty, ignoreCase = true)

    /** Splits [text] by [separator], trimming and matching without regard to case. */
    fun splitTrimIgnoreCase(
        text: CharSequence?,
        separator: CharSequence,
        limit: Int = -1,
        ignoreEmpty: Boolean = true,
    ): List<String> = split(text, separator, limit, isTrim = true, ignoreEmpty, ignoreCase = true)

    /**
     * Splits on a path separator: `/` or `\`, with every part trimmed and empty parts dropped.
     *
     * @param text the path to split
     */
    fun splitPath(text: CharSequence?): List<String> {
        if (text == null) return emptyList()
        val parts = mutableListOf<String>()
        val part = StringBuilder()
        for (ch in text) {
            if (CharUtil.isFileSeparator(ch)) {
                if (part.isNotEmpty()) {
                    parts.add(part.toString())
                    part.setLength(0)
                }
            } else {
                part.append(ch)
            }
        }
        if (part.isNotEmpty()) parts.add(part.toString())
        return parts
    }

    /** Splits a path into its parts and returns them as an array. See [splitPath]. */
    fun splitPathToArray(text: CharSequence?): Array<String> = splitPath(text).toTypedArray()

    /**
     * Splits [text] on the character or string [separator], matching it as a whole.
     */
    fun split(
        text: CharSequence?,
        separator: Char,
        limit: Int = -1,
        isTrim: Boolean = false,
        ignoreEmpty: Boolean = false,
        ignoreCase: Boolean = false,
    ): List<String> = split(text, separator.toString(), limit, isTrim, ignoreEmpty, ignoreCase)

    /** Splits [text] by a regular expression. See [split] for the meaning of [limit]. */
    fun splitByRegex(
        text: CharSequence?,
        separatorRegex: String,
        limit: Int = -1,
        isTrim: Boolean = false,
        ignoreEmpty: Boolean = false,
    ): List<String> {
        if (text == null) return emptyList()
        val parts = text.toString().split(Regex(separatorRegex), limit.coerceAtLeast(0))
            .let { if (limit <= 0) it.dropLastWhile(String::isEmpty) else it }
            .map { if (isTrim) it.trim() else it }
            .filter { !ignoreEmpty || it.isNotEmpty() }
        return parts
    }

    /**
     * Splits on any blank character (space, tab, full-width space, ...), as
     * `StrSplitter.split(text, limit)` does in Hutool.
     */
    fun splitByBlank(text: CharSequence?, limit: Int = -1): List<String> {
        if (text == null) return emptyList()
        val parts = mutableListOf<String>()
        val part = StringBuilder()
        for (ch in text) {
            if (CharUtil.isBlankChar(ch)) {
                if (part.isNotEmpty()) {
                    parts.add(part.toString())
                    part.setLength(0)
                    if (limit > 0 && parts.size == limit - 1) {
                        // The remainder is kept as one last part, blanks included.
                        part.append(text, text.indexOf(ch), text.length)
                        break
                    }
                }
            } else {
                part.append(ch)
            }
        }
        if (part.isNotEmpty()) parts.add(part.toString())
        return parts
    }

    /**
     * Cuts [text] into chunks of [len] characters; the last chunk may be shorter.
     *
     * @param text the text to split
     * @param len the chunk length, must be positive
     */
    fun splitByLength(text: CharSequence?, len: Int): Array<String> {
        if (text == null) return emptyArray()
        require(len > 0) { "Length must be greater than 1" }
        val count = (text.length + len - 1) / len
        return Array(count) { index -> text.substring(index * len, minOf(text.length, (index + 1) * len)) }
    }

    private fun splitParts(
        text: CharSequence,
        separator: String,
        limit: Int,
        isTrim: Boolean,
        ignoreEmpty: Boolean,
        ignoreCase: Boolean,
    ): List<String> {
        require(separator.isNotEmpty()) { "Separator must not be empty" }
        val raw = if (separator.length == 1) {
            splitOnChar(text, separator[0], limit, ignoreCase)
        } else {
            splitOnString(text, separator, limit, ignoreCase)
        }
        val parts = raw.map { if (isTrim) it.trim() else it }
        return if (ignoreEmpty) parts.filter { it.isNotEmpty() } else parts
    }

    private fun splitOnChar(text: CharSequence, separator: Char, limit: Int, ignoreCase: Boolean): List<String> {
        val parts = mutableListOf<String>()
        var start = 0
        for (index in text.indices) {
            val matches = if (ignoreCase) CharUtil.equals(text[index], separator, caseInsensitive = true) else text[index] == separator
            if (!matches) continue
            parts.add(text.subSequence(start, index).toString())
            start = index + 1
            if (limit > 0 && parts.size == limit - 1) break
        }
        parts.add(text.subSequence(start, text.length).toString())
        return parts
    }

    private fun splitOnString(text: CharSequence, separator: String, limit: Int, ignoreCase: Boolean): List<String> {
        val parts = mutableListOf<String>()
        var start = 0
        var index = 0
        while (index <= text.length - separator.length) {
            if (text.matchesAt(separator, index, ignoreCase)) {
                parts.add(text.subSequence(start, index).toString())
                start = index + separator.length
                index = start
                if (limit > 0 && parts.size == limit - 1) break
            } else {
                index++
            }
        }
        parts.add(text.subSequence(start, text.length).toString())
        return parts
    }

    private fun CharSequence.matchesAt(value: String, index: Int, ignoreCase: Boolean): Boolean {
        for (offset in value.indices) {
            val c1 = this[index + offset]
            val c2 = value[offset]
            if (if (ignoreCase) c1.lowercaseChar() != c2.lowercaseChar() else c1 != c2) return false
        }
        return true
    }
}
