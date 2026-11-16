package io.github.shijianjs.kmphutool.core.util

/**
 * Regular-expression helpers over Kotlin's [Regex].
 *
 * Port of `cn.hutool.core.util.ReUtil`. Hutool takes `java.util.regex.Pattern` and offers `group`
 * names as strings; the port takes a pattern [String] or a [Regex] and addresses groups by index
 * (Kotlin does not expose named groups). Members that rely on `Matcher` internals (`extractMultiAndDelPre`)
 * are not ported.
 *
 * @hutool cn.hutool.core.util.ReUtil
 */
object ReUtil {
    /** `true` when the whole [text] matches [regex]. */
    fun isMatch(regex: String, text: CharSequence?): Boolean =
        text != null && Regex(regex).matches(text)

    /** `true` when [text] matches [pattern]. */
    fun isMatch(pattern: Regex, text: CharSequence?): Boolean = text != null && pattern.matches(text)

    /**
     * The text captured by group [group] of the first match, or `null` when there is no match.
     * Group `0` is the whole match.
     */
    fun get(regex: String, text: CharSequence?, group: Int = 0): String? {
        if (text == null) return null
        val match = Regex(regex).find(text) ?: return null
        return match.groupValues.getOrNull(group)
    }

    /** The whole first match, or `null`. */
    fun getGroup0(regex: String, text: CharSequence?): String? = get(regex, text, 0)

    /** The first capture group of the first match, or `null`. */
    fun getGroup1(regex: String, text: CharSequence?): String? = get(regex, text, 1)

    /** Group [group] of the first match, or [defaultValue] when there is no match. */
    fun get(regex: String, text: CharSequence?, group: Int, defaultValue: String?): String? =
        get(regex, text, group) ?: defaultValue

    /** Every occurrence of group [group], in order. */
    fun findAll(regex: String, text: CharSequence?, group: Int = 0): List<String> {
        if (text == null) return emptyList()
        return Regex(regex).findAll(text).mapNotNull { it.groupValues.getOrNull(group) }.toList()
    }

    /** Every whole match. */
    fun findAllGroup0(regex: String, text: CharSequence?): List<String> = findAll(regex, text, 0)

    /** Every first capture group. */
    fun findAllGroup1(regex: String, text: CharSequence?): List<String> = findAll(regex, text, 1)

    /** Replaces every match with [replacement]; `$1` style references work as in Kotlin's [Regex]. */
    fun replaceAll(regex: String, text: CharSequence, replacement: String): String =
        Regex(regex).replace(text, replacement)

    /** Replaces the first match with [replacement]. */
    fun replaceFirst(regex: String, text: CharSequence, replacement: String): String {
        val match = Regex(regex).find(text) ?: return text.toString()
        return text.replaceRange(match.range, replacement).toString()
    }

    /** Removes every match. */
    fun delAll(regex: String, text: CharSequence?): String =
        if (text == null) "" else Regex(regex).replace(text, "")

    /** Removes the first match. */
    fun delFirst(regex: String, text: CharSequence?): String =
        if (text == null) "" else replaceFirst(regex, text, "")

    /** Removes the last match. */
    fun delLast(regex: String, text: CharSequence?): String {
        if (text == null) return ""
        val match = Regex(regex).findAll(text).lastOrNull() ?: return text.toString()
        return text.replaceRange(match.range, "").toString()
    }

    /** How many times [regex] matches inside [text]. */
    fun count(regex: String, text: CharSequence?): Int =
        if (text == null) 0 else Regex(regex).findAll(text).count()

    /** `true` when [regex] matches anywhere in [text]. */
    fun contains(regex: String, text: CharSequence?): Boolean = text != null && Regex(regex).containsMatchIn(text)

    /** The index of the first match, `-1` when there is none. */
    fun indexOf(regex: String, text: CharSequence?): Int {
        if (text == null) return -1
        val match = Regex(regex).find(text) ?: return -1
        return match.range.first
    }

    /** The last index of a match, `-1` when there is none. */
    fun lastIndexOf(regex: String, text: CharSequence?): Int {
        if (text == null) return -1
        val match = Regex(regex).findAll(text).lastOrNull() ?: return -1
        return match.range.first
    }

    /** The first number found in [text], or `null`. */
    fun getFirstNumber(regex: String, text: CharSequence?): String? =
        text?.let { Regex(regex).find(it)?.value }

    /**
     * Expands a template with every group of the first match: `re.extractMulti(regex, text, "{0}-{1}")`
     * replaces `{n}` with group `n`.
     */
    /**
     * Expands a template with every group of the first match.
     *
     * Upstream uses `$1`, `$2`, ...; the port accepts `$n` as well as `{n}`, so both styles work.
     */
    fun extractMulti(regex: String, text: CharSequence?, template: String): String? {
        if (text == null) return null
        val match = Regex(regex).find(text) ?: return null
        var result = template
        // Descending, so "$1" is not mistaken for the prefix of "$12".
        for (number in match.groupValues.indices.reversed()) {
            val value = match.groupValues[number]
            result = result.replace("\$" + number, value).replace("{" + number + "}", value)
        }
        return result
    }


    /** Escapes every regular-expression metacharacter in [text], so it matches literally. */
    fun escape(text: CharSequence?): String? = text?.let { source ->
        val builder = StringBuilder(source.length * 2)
        for (ch in source) {
            if (ch in ESCAPE_CHARS) builder.append('\\')
            builder.append(ch)
        }
        builder.toString()
    }

    /** The characters that carry meaning in a regular expression. */
    private val ESCAPE_CHARS = setOf(
        '\\', '[', ']', '(', ')', '{', '}', '^', '$', '|', '.', '?', '*', '+',
    )
}
