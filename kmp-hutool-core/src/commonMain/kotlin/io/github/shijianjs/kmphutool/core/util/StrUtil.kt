package io.github.shijianjs.kmphutool.core.util

import io.github.shijianjs.kmphutool.core.text.StrFormatter
import io.github.shijianjs.kmphutool.core.text.StrJoiner
import io.github.shijianjs.kmphutool.core.text.StrPool
import io.github.shijianjs.kmphutool.core.text.StrSplitter

/**
 * String helpers covering the checks, conversions and comparisons that Hutool puts in `StrUtil`.
 *
 * This one object is the port of **two** upstream classes: `cn.hutool.core.text.CharSequenceUtil`
 * (where Hutool 5.8 moved the implementation) and `cn.hutool.core.util.StrUtil` (the facade that only
 * delegates to it). Kotlin has no reason to keep that split, so both surfaces live here; the
 * member Javadoc keeps the upstream name where the behaviour is subtle.
 *
 * Deliberately absent, because they need something commonMain does not have:
 * `normalize` (`java.text.Normalizer`), `subPreGbk` (GBK code page), `hide` (deprecated upstream),
 * `getGeneralField`/`genGetter`/`genSetter` (JavaBean reflection), `byteBuffer` (`java.nio`),
 * `move` (shelf/in-memory string model), `fill`/`fillBefore`/`fillAfter` (deprecated upstream).
 *
 * @hutool cn.hutool.core.util.StrUtil
 * @hutool cn.hutool.core.text.CharSequenceUtil
 */
object StrUtil {
    // ---------------------------------------------------------------------------------------------
    // Emptiness and blankness
    // ---------------------------------------------------------------------------------------------

    /** `true` when [cs] is `null` or has length 0. */
    fun isEmpty(cs: CharSequence?): Boolean = cs == null || cs.isEmpty()

    /** `true` when [cs] is neither `null` nor empty. */
    fun isNotEmpty(cs: CharSequence?): Boolean = !isEmpty(cs)

    /** `true` when [cs] is `null`, empty, or made of blank characters only. */
    fun isBlank(cs: CharSequence?): Boolean {
        if (isEmpty(cs)) return true
        for (ch in cs!!) {
            if (!CharUtil.isBlankChar(ch)) return false
        }
        return true
    }

    /** `true` when [cs] contains at least one non-blank character. */
    fun isNotBlank(cs: CharSequence?): Boolean = !isBlank(cs)

    /**
     * `true` when the argument is a blank string; anything that is not a [CharSequence] (including
     * `null`) counts as blank.
     */
    fun isBlankIfStr(obj: Any?): Boolean = obj !is CharSequence || isBlank(obj)

    /** `true` when every argument is blank (an empty argument list is `true`). */
    fun isAllBlank(vararg css: CharSequence?): Boolean = css.all { isBlank(it) }

    /** `true` when every argument is non-blank. */
    fun isAllNotBlank(vararg css: CharSequence?): Boolean = css.all { isNotBlank(it) }

    /** `true` when every argument is empty. */
    fun isAllEmpty(vararg css: CharSequence?): Boolean = css.all { isEmpty(it) }

    /** `true` when every argument is non-empty. */
    fun isAllNotEmpty(vararg css: CharSequence?): Boolean = css.all { isNotEmpty(it) }

    /** `true` when at least one argument is blank. */
    fun hasBlank(vararg css: CharSequence?): Boolean = css.any { isBlank(it) }

    /** `true` when at least one argument is empty. */
    fun hasEmpty(vararg css: CharSequence?): Boolean = css.any { isEmpty(it) }

    // ---------------------------------------------------------------------------------------------
    // Defaults
    // ---------------------------------------------------------------------------------------------

    /** Returns [defaultValue] when [cs] is `null`. */
    fun nullToDefault(cs: CharSequence?, defaultValue: CharSequence?): String? =
        cs?.toString() ?: defaultValue?.toString()

    /** Returns the empty string when [cs] is `null`. */
    fun nullToEmpty(cs: CharSequence?): String = cs?.toString() ?: StrPool.EMPTY

    /** Returns [defaultValue] when [cs] is `null` or empty. */
    fun emptyToDefault(cs: CharSequence?, defaultValue: CharSequence?): String? =
        if (isEmpty(cs)) defaultValue?.toString() else cs.toString()

    /** Returns [defaultValue] when [cs] is `null`, empty or blank. */
    fun blankToDefault(cs: CharSequence?, defaultValue: CharSequence?): String? =
        if (isBlank(cs)) defaultValue?.toString() else cs.toString()

    /** Returns `null` when [cs] is empty, [cs] otherwise. */
    fun emptyToNull(cs: CharSequence?): String? = if (isEmpty(cs)) null else cs.toString()

    /** Returns the empty string when [cs] is `null`. */
    fun emptyIfNull(cs: CharSequence?): String = cs?.toString() ?: StrPool.EMPTY

    /** Returns the first non-empty argument, or `null`. */
    fun firstNonEmpty(vararg css: CharSequence?): String? =
        css.firstOrNull { isNotEmpty(it) }?.toString()

    /** Returns the first non-blank argument, or `null`. */
    fun firstNonBlank(vararg css: CharSequence?): String? =
        css.firstOrNull { isNotBlank(it) }?.toString()

    /** Returns the first non-null argument, or `null`. */
    fun firstNonNull(vararg css: CharSequence?): String? = css.firstOrNull { it != null }?.toString()

    // ---------------------------------------------------------------------------------------------
    // Trimming and cleaning
    // ---------------------------------------------------------------------------------------------

    /** Trims both ends; `null` stays `null`. */
    fun trim(cs: CharSequence?): String? = cs?.toString()?.trim()

    /** Trims the start; `null` stays `null`. */
    fun trimStart(cs: CharSequence?): String? = cs?.toString()?.trimStart()

    /** Trims the end; `null` stays `null`. */
    fun trimEnd(cs: CharSequence?): String? = cs?.toString()?.trimEnd()

    /** Trims both ends, returning the empty string for `null`. */
    fun trimToEmpty(cs: CharSequence?): String = trim(cs) ?: StrPool.EMPTY

    /** Trims both ends, returning `null` when the result is empty. */
    fun trimToNull(cs: CharSequence?): String? = emptyToNull(trim(cs))

    /** Removes every blank character. */
    fun cleanBlank(cs: CharSequence?): String {
        if (isEmpty(cs)) return StrPool.EMPTY
        val result = StringBuilder(cs!!.length)
        for (ch in cs) {
            if (!CharUtil.isBlankChar(ch)) result.append(ch)
        }
        return result.toString()
    }

    /**
     * Trims both ends and collapses every run of blank characters inside into one space.
     * `null` becomes the empty string.
     */
    fun strip(cs: CharSequence?): String {
        if (isEmpty(cs)) return StrPool.EMPTY
        val text = cs!!.toString().trim()
        val result = StringBuilder(text.length)
        var pendingBlank = false
        for (ch in text) {
            if (CharUtil.isBlankChar(ch)) {
                pendingBlank = result.isNotEmpty()
            } else {
                if (pendingBlank) result.append(StrPool.SPACE)
                pendingBlank = false
                result.append(ch)
            }
        }
        return result.toString()
    }

    /** Removes all occurrences of the given strings from both ends of [cs]. */
    fun stripAll(cs: CharSequence?, vararg strs: CharSequence): String {
        var text = cs?.toString() ?: return StrPool.EMPTY
        var changed = true
        while (changed) {
            changed = false
            for (str in strs) {
                if (str.isNotEmpty() && text.startsWith(str) && text.length >= str.length * 2) {
                    text = text.substring(str.length)
                    changed = true
                }
                if (str.isNotEmpty() && text.endsWith(str) && text.length >= str.length * 2) {
                    text = text.substring(0, text.length - str.length)
                    changed = true
                }
            }
        }
        return text
    }

    // ---------------------------------------------------------------------------------------------
    // Length and bytes
    // ---------------------------------------------------------------------------------------------

    /** The length of [cs], 0 for `null`. */
    fun length(cs: CharSequence?): Int = cs?.length ?: 0

    /** The summed length of every argument. */
    fun totalLength(vararg css: CharSequence?): Int = css.sumOf { length(it) }

    /** The length of [cs] in UTF-8 bytes, 0 for `null`. */
    fun byteLength(cs: CharSequence?): Int = cs?.toString()?.encodeToByteArray()?.size ?: 0

    /** Encodes [cs] as UTF-8. */
    fun bytes(cs: CharSequence?): ByteArray = cs?.toString()?.encodeToByteArray() ?: ByteArray(0)

    /** Encodes [cs] as UTF-8; the name Hutool uses most often. */
    fun utf8Bytes(cs: CharSequence?): ByteArray = bytes(cs)

    // ---------------------------------------------------------------------------------------------
    // Substrings
    // ---------------------------------------------------------------------------------------------

    /** Substring from [startInclude] to the end; `null` or out-of-range gives the empty string. */
    fun sub(cs: CharSequence?, startInclude: Int): String =
        sub(cs, startInclude, if (cs == null) 0 else cs.length)

    /**
     * Substring with [startInclude] inclusive and [endExclude] exclusive, clamped to the input
     * instead of throwing.
     */
    fun sub(cs: CharSequence?, startInclude: Int, endExclude: Int): String {
        if (isEmpty(cs)) return StrPool.EMPTY
        val text = cs!!
        val start = startInclude.coerceIn(0, text.length)
        val end = endExclude.coerceIn(0, text.length)
        return if (end <= start) StrPool.EMPTY else text.subSequence(start, end).toString()
    }

    /** The last [length] characters of [cs]; a shorter input is returned whole. */
    fun subSuf(cs: CharSequence?, length: Int): String? {
        if (isEmpty(cs) || length <= 0) return null
        val start = cs!!.length - length
        return cs.subSequence(if (start < 0) 0 else start, cs.length).toString()
    }

    /** The first [length] characters of [cs]; a shorter input is returned whole. */
    fun subPre(cs: CharSequence?, length: Int): String? =
        if (isEmpty(cs) || length <= 0) null else sub(cs, 0, length)

    /** [length] characters starting at [fromIndex]. */
    fun subWithLength(cs: CharSequence?, fromIndex: Int, length: Int): String? {
        if (isEmpty(cs)) return null
        val end = fromIndex + length
        return sub(cs, fromIndex, if (end > cs!!.length) cs.length else end)
    }

    /** Substring by code points, so a surrogate pair is never split. */
    fun subByCodePoint(cs: CharSequence?, startInclude: Int, endExclude: Int): String {
        if (isEmpty(cs)) return StrPool.EMPTY
        var start = startInclude
        var end = endExclude
        if (start < 0) start = 0
        if (end > cs!!.length) end = cs.length
        if (start > end) return StrPool.EMPTY
        // Widen the range until both ends sit on a code point boundary.
        if (start > 0 && cs[start].isLowSurrogate()) start--
        if (end < cs.length && cs[end - 1].isHighSurrogate()) end++
        return cs.subSequence(start, end).toString()
    }

    /** The text between the first [prefix] and the first [suffix] after it, or `null`. */
    fun subBetween(cs: CharSequence?, prefix: CharSequence, suffix: CharSequence): String? {
        if (isEmpty(cs)) return null
        val start = cs!!.toString().indexOf(prefix.toString())
        if (start < 0) return null
        val from = start + prefix.length
        val end = cs.toString().indexOf(suffix.toString(), from)
        if (end < 0) return null
        return sub(cs, from, end)
    }

    /** Every text between [prefix] and [suffix]: the body of each `{...}` block. */
    fun subBetweenAll(cs: CharSequence?, prefix: CharSequence, suffix: CharSequence): List<String> {
        if (isEmpty(cs) || prefix.isEmpty() || suffix.isEmpty()) return emptyList()
        val text = cs.toString()
        val result = mutableListOf<String>()
        var index = 0
        while (index < text.length) {
            val start = text.indexOf(prefix.toString(), index)
            if (start < 0) break
            val from = start + prefix.length
            val end = text.indexOf(suffix.toString(), from)
            if (end < 0) break
            result.add(text.substring(from, end))
            index = end + suffix.length
        }
        return result
    }

    /** The text before the first occurrence of [sep]. */
    fun subBefore(cs: CharSequence?, sep: CharSequence, isLastSeparator: Boolean = false): String? {
        if (isEmpty(cs)) return null
        val text = cs.toString()
        val index = if (isLastSeparator) text.lastIndexOf(sep.toString()) else text.indexOf(sep.toString())
        return if (index < 0) null else text.substring(0, index)
    }

    /** The text after the first occurrence of [sep]. */
    fun subAfter(cs: CharSequence?, sep: CharSequence, isLastSeparator: Boolean = false): String? {
        if (isEmpty(cs)) return null
        val text = cs.toString()
        val index = if (isLastSeparator) text.lastIndexOf(sep.toString()) else text.indexOf(sep.toString())
        return if (index < 0) null else text.substring(index + sep.length)
    }

    /**
     * Cuts [cs] into `before`, `between` and `after` around [prefix] and [suffix].
     *
     * @return a triple whose parts are `null` when their delimiter is missing
     */
    fun cut(cs: CharSequence?, prefix: CharSequence, suffix: CharSequence): Triple<String?, String?, String?> {
        if (isEmpty(cs)) return Triple(null, null, null)
        val text = cs.toString()
        val prefixIndex = text.indexOf(prefix.toString())
        val suffixIndex = text.indexOf(suffix.toString())
        val before = if (prefixIndex < 0) null else text.substring(0, prefixIndex)
        val between = if (prefixIndex < 0 || suffixIndex < 0 || suffixIndex < prefixIndex) {
            null
        } else {
            text.substring(prefixIndex + prefix.length, suffixIndex)
        }
        val after = if (suffixIndex < 0) null else text.substring(suffixIndex + suffix.length)
        return Triple(before, between, after)
    }

    // ---------------------------------------------------------------------------------------------
    // Splitting and joining
    // ---------------------------------------------------------------------------------------------

    /** Splits [cs] by [separator]; see [StrSplitter.split]. */
    fun split(
        cs: CharSequence?,
        separator: CharSequence,
        limit: Int = -1,
        isTrim: Boolean = false,
        ignoreEmpty: Boolean = false,
        ignoreCase: Boolean = false,
    ): List<String> = StrSplitter.split(cs, separator, limit, isTrim, ignoreEmpty, ignoreCase)

    /** Splits [cs] by a single-character [separator]. */
    fun split(
        cs: CharSequence?,
        separator: Char,
        limit: Int = -1,
        isTrim: Boolean = false,
        ignoreEmpty: Boolean = false,
        ignoreCase: Boolean = false,
    ): List<String> = StrSplitter.split(cs, separator, limit, isTrim, ignoreEmpty, ignoreCase)

    /** Splits [cs] by [separator] into an array. */
    fun splitToArray(
        cs: CharSequence?,
        separator: CharSequence,
        limit: Int = -1,
        isTrim: Boolean = false,
        ignoreEmpty: Boolean = false,
        ignoreCase: Boolean = false,
    ): Array<String> = StrSplitter.splitToArray(cs, separator, limit, isTrim, ignoreEmpty, ignoreCase)

    /** Splits [cs] by [separator], trimming every part and dropping empty ones. */
    fun splitTrim(cs: CharSequence?, separator: CharSequence): List<String> =
        StrSplitter.splitTrim(cs, separator)

    /** Splits [cs] by [separator] and trims every part into an array. */
    fun splitTrimToArray(cs: CharSequence?, separator: CharSequence): Array<String> =
        StrSplitter.splitTrim(cs, separator).toTypedArray()

    /** Splits [cs] by [separator] into integers; parts that do not parse are skipped. */
    fun splitToInt(cs: CharSequence?, separator: CharSequence): List<Int> =
        split(cs, separator, isTrim = true).mapNotNull { it.toIntOrNull() }

    /** Splits [cs] by [separator] into longs; parts that do not parse are skipped. */
    fun splitToLong(cs: CharSequence?, separator: CharSequence): List<Long> =
        split(cs, separator, isTrim = true).mapNotNull { it.toLongOrNull() }

    /** Joins [iterable] with [conjunction], wrapped in [prefix] and [suffix]. */
    fun join(
        iterable: Iterable<*>?,
        conjunction: CharSequence,
        prefix: CharSequence = StrPool.EMPTY,
        suffix: CharSequence = StrPool.EMPTY,
    ): String {
        val joiner = StrJoiner.of(conjunction, prefix, suffix).setNullMode(StrJoiner.NullMode.IGNORE)
        iterable?.forEach { joiner.append(it) }
        return joiner.toString()
    }

    /** Joins the arguments with [conjunction]. */
    fun join(conjunction: CharSequence, vararg objs: Any?): String = join(objs.toList(), conjunction)

    /** Concatenates the arguments; `null` arguments are skipped. */
    fun concat(vararg objs: Any?): String {
        val builder = StringBuilder()
        for (obj in objs) {
            if (obj != null) builder.append(obj)
        }
        return builder.toString()
    }

    /** Repeats [cs] [count] times. */
    fun repeat(cs: CharSequence, count: Int): String = cs.toString().repeat(count)

    /** Repeats [cs] [count] times, joined with [conjunction]. */
    fun repeatAndJoin(cs: CharSequence, conjunction: CharSequence, count: Int): String =
        (0 until count).joinToString(conjunction.toString()) { cs.toString() }

    /** Repeats [cs] until it is at least [length] characters long, then cuts to [length]. */
    fun repeatByLength(cs: CharSequence, length: Int): String {
        if (cs.isEmpty()) return StrPool.EMPTY
        val text = cs.toString()
        val result = StringBuilder(length)
        while (result.length < length) result.append(text)
        return result.substring(0, length)
    }

    /** Formats [template] with `{}` placeholders. See [StrFormatter.format]. */
    fun format(template: CharSequence, vararg args: Any?): String = StrFormatter.format(template, *args)

    /**
     * Formats a template whose placeholders are **indexes**: `{0}`, `{1}`, ... — the syntax
     * Log4j/SLF4J users expect when arguments repeat.
     */
    fun indexedFormat(template: CharSequence, vararg args: Any?): String {
        if (isEmpty(template) || args.isEmpty()) return emptyIfNull(template)
        val result = StringBuilder(template.length)
        var index = 0
        while (index < template.length) {
            val ch = template[index]
            if (ch == StrPool.DELIM_START) {
                val close = template.indexOf(StrPool.DELIM_END, index + 1)
                if (close > index) {
                    val number = template.subSequence(index + 1, close).toString().trim().toIntOrNull()
                    if (number != null) {
                        if (number in args.indices) result.append(args[number] ?: "null")
                        index = close + 1
                        continue
                    }
                }
            }
            result.append(ch)
            index++
        }
        return result.toString()
    }

    // ---------------------------------------------------------------------------------------------
    // Prefix and suffix surgery
    // ---------------------------------------------------------------------------------------------

    /** Removes [prefix] from the start when present. */
    fun removePrefix(cs: CharSequence?, prefix: CharSequence): String? {
        if (isEmpty(cs)) return null
        val text = cs.toString()
        return if (prefix.isNotEmpty() && text.startsWith(prefix)) text.substring(prefix.length) else text
    }

    /** Removes [suffix] from the end when present. */
    fun removeSuffix(cs: CharSequence?, suffix: CharSequence): String? {
        if (isEmpty(cs)) return null
        val text = cs.toString()
        return if (suffix.isNotEmpty() && text.endsWith(suffix)) {
            text.substring(0, text.length - suffix.length)
        } else {
            text
        }
    }

    /** Removes [prefix] from the start when present, ignoring case. */
    fun removePrefixIgnoreCase(cs: CharSequence?, prefix: CharSequence): String? {
        if (isEmpty(cs)) return null
        val text = cs.toString()
        return if (prefix.isNotEmpty() && text.startsWith(prefix, ignoreCase = true)) {
            text.substring(prefix.length)
        } else {
            text
        }
    }

    /** Removes [suffix] from the end when present, ignoring case. */
    fun removeSuffixIgnoreCase(cs: CharSequence?, suffix: CharSequence): String? {
        if (isEmpty(cs)) return null
        val text = cs.toString()
        return if (suffix.isNotEmpty() && text.endsWith(suffix, ignoreCase = true)) {
            text.substring(0, text.length - suffix.length)
        } else {
            text
        }
    }

    /** Removes every occurrence of each argument. */
    fun removeAll(cs: CharSequence?, vararg strs: CharSequence?): String {
        var text = cs?.toString() ?: return StrPool.EMPTY
        for (str in strs) {
            if (str.isNullOrEmpty()) continue
            text = text.replace(str.toString(), StrPool.EMPTY)
        }
        return text
    }

    /** Removes `\r`, `\n` and `\r\n`. */
    fun removeAllLineBreaks(cs: CharSequence?): String =
        removeAll(cs, StrPool.CRLF, StrPool.CR_STR, StrPool.LF_STR)

    /** Removes every occurrence of each single character listed in the arguments. */
    fun removeAny(cs: CharSequence?, vararg strs: CharSequence): String {
        var text = cs?.toString() ?: return StrPool.EMPTY
        for (str in strs) {
            for (ch in str) text = text.replace(ch.toString(), StrPool.EMPTY)
        }
        return text
    }

    /** Prepends [prefix] when [cs] does not already start with it. */
    fun addPrefixIfNot(cs: CharSequence?, prefix: CharSequence): String {
        val text = emptyIfNull(cs)
        return if (prefix.isEmpty() || text.startsWith(prefix)) text else prefix.toString() + text
    }

    /** Appends [suffix] when [cs] does not already end with it. */
    fun addSuffixIfNot(cs: CharSequence?, suffix: CharSequence): String {
        val text = emptyIfNull(cs)
        return if (suffix.isEmpty() || text.endsWith(suffix)) text else text + suffix
    }

    /** Prepends [prefix] when [cs] does not already start with it. */
    fun prependIfMissing(cs: CharSequence?, prefix: CharSequence): String = addPrefixIfNot(cs, prefix)

    /** Prepends [prefix] when [cs] does not already start with it, ignoring case. */
    fun prependIfMissingIgnoreCase(cs: CharSequence?, prefix: CharSequence): String {
        val text = emptyIfNull(cs)
        return if (prefix.isEmpty() || text.startsWith(prefix, ignoreCase = true)) {
            text
        } else {
            prefix.toString() + text
        }
    }

    /** Appends [suffix] when [cs] does not already end with it. */
    fun appendIfMissing(cs: CharSequence?, suffix: CharSequence): String = addSuffixIfNot(cs, suffix)

    /** Appends [suffix] when [cs] does not already end with it, ignoring case. */
    fun appendIfMissingIgnoreCase(cs: CharSequence?, suffix: CharSequence): String {
        val text = emptyIfNull(cs)
        return if (suffix.isEmpty() || text.endsWith(suffix, ignoreCase = true)) {
            text
        } else {
            text + suffix
        }
    }

    /** Wraps [cs] in [wrap] when it does not already start and end with it. */
    fun wrap(cs: CharSequence?, wrap: CharSequence): String =
        StringBuilder().append(wrap).append(emptyIfNull(cs)).append(wrap).toString()

    /** Wraps [cs] in [prefix] and [suffix]. */
    fun wrap(cs: CharSequence?, prefix: CharSequence, suffix: CharSequence): String =
        StringBuilder().append(prefix).append(emptyIfNull(cs)).append(suffix).toString()

    /** Removes one layer of [prefix] and [suffix] when both are present. */
    fun unWrap(cs: CharSequence?, prefix: CharSequence, suffix: CharSequence): String {
        if (isEmpty(cs)) return StrPool.EMPTY
        val text = cs.toString()
        return if (text.startsWith(prefix) && text.endsWith(suffix) && text.length >= prefix.length + suffix.length) {
            text.substring(prefix.length, text.length - suffix.length)
        } else {
            text
        }
    }

    /** Removes one layer of [wrap] from both ends of [cs]. */
    fun unWrap(cs: CharSequence?, wrap: CharSequence): String {
        if (isEmpty(cs)) return StrPool.EMPTY
        val text = cs.toString()
        return if (text.startsWith(wrap) && text.endsWith(wrap) && text.length >= wrap.length * 2) {
            text.substring(wrap.length, text.length - wrap.length)
        } else {
            text
        }
    }

    /** Wraps [cs] in [wrap] only when it is not wrapped already. */
    fun wrapIfMissing(cs: CharSequence?, wrap: CharSequence): String =
        wrapIfMissing(cs, wrap, wrap)

    /** Wraps [cs] in [prefix] and [suffix] only when either side is missing. */
    fun wrapIfMissing(cs: CharSequence?, prefix: CharSequence, suffix: CharSequence): String {
        if (isEmpty(cs)) return wrap(cs, prefix, suffix)
        val text = cs.toString()
        val needsPrefix = prefix.isNotEmpty() && !text.startsWith(prefix)
        val needsSuffix = suffix.isNotEmpty() && !text.endsWith(suffix)
        return StringBuilder()
            .apply {
                if (needsPrefix) append(prefix)
                append(text)
                if (needsSuffix) append(suffix)
            }.toString()
    }

    /** Wraps [cs] in [prefix] and [suffix]. */
    fun wrapAll(cs: CharSequence?, prefix: CharSequence, suffix: CharSequence): String =
        StringBuilder().append(prefix).append(emptyIfNull(cs)).append(suffix).toString()

    /** Wraps [cs] in [prefix] and [suffix] only when it is not wrapped already. */
    fun wrapAllIfMissing(cs: CharSequence?, prefix: CharSequence, suffix: CharSequence): String {
        if (isEmpty(cs)) return wrapAll(cs, prefix, suffix)
        val text = cs.toString()
        val result = StringBuilder()
        if (!text.startsWith(prefix)) result.append(prefix)
        result.append(text)
        if (!text.endsWith(suffix)) result.append(suffix)
        return result.toString()
    }

    /** Pairs characters into wrapping delimiters: `"[a}{b]"` gets `{`/`}` inserted correctly. */
    fun wrapAllWithPair(cs: CharSequence?, prefix: CharSequence, suffix: CharSequence): String {
        if (isEmpty(cs)) return wrapAll(cs, prefix, suffix)
        if (prefix.isEmpty() || suffix.isEmpty()) return wrapAll(cs, prefix, suffix)
        val text = cs.toString()
        val open = prefix[0]
        val close = suffix[suffix.length - 1]
        val result = StringBuilder()
        var depth = 0
        var index = 0
        while (index < text.length) {
            val ch = text[index]
            if (ch == open) {
                depth++
            } else if (ch == close) {
                depth--
                if (depth < 0) {
                    result.append(prefix)
                    depth++
                }
            }
            result.append(ch)
            index++
        }
        if (depth > 0) {
            repeat(depth) { result.append(suffix) }
        } else {
            result.append(suffix)
        }
        if (!result.startsWith(prefix)) result.insert(0, prefix)
        return result.toString()
    }

    /** [wrapAllWithPair], skipping the insertion when [cs] is wrapped already. */
    fun wrapAllWithPairIfMissing(cs: CharSequence?, prefix: CharSequence, suffix: CharSequence): String =
        if (isWrap(cs, prefix, suffix)) emptyIfNull(cs) else wrapAllWithPair(cs, prefix, suffix)

    /** `true` when [cs] starts and ends with [wrap]. */
    fun isWrap(cs: CharSequence?, wrap: CharSequence): Boolean = isWrap(cs, wrap, wrap)

    /** `true` when [cs] starts with [prefix] and ends with [suffix]. */
    fun isWrap(cs: CharSequence?, prefix: CharSequence, suffix: CharSequence): Boolean =
        isNotEmpty(cs) && cs!!.toString().startsWith(prefix) && cs.toString().endsWith(suffix)

    /** `true` when [cs] is surrounded by [prefix] and [suffix]. */
    fun isSurround(cs: CharSequence?, prefix: CharSequence, suffix: CharSequence): Boolean =
        isWrap(cs, prefix, suffix)

    // ---------------------------------------------------------------------------------------------
    // Case conversion and naming
    // ---------------------------------------------------------------------------------------------

    /** Upper-cases the first character only. */
    fun upperFirst(cs: CharSequence?): String {
        if (isEmpty(cs)) return emptyIfNull(cs)
        val text = cs.toString()
        if (text[0].isUpperCase()) return text
        return text[0].uppercaseChar() + text.substring(1)
    }

    /** Lower-cases the first character only. */
    fun lowerFirst(cs: CharSequence?): String {
        if (isEmpty(cs)) return emptyIfNull(cs)
        val text = cs.toString()
        if (text[0].isLowerCase()) return text
        return text[0].lowercaseChar() + text.substring(1)
    }

    /** Upper-cases the first character and prepends [pre]. */
    fun upperFirstAndAddPre(cs: CharSequence?, pre: CharSequence): String =
        StringBuilder().append(pre).append(upperFirst(cs)).toString()

    /** Removes [pre], then lower-cases the first character. */
    fun removePreAndLowerFirst(cs: CharSequence?, pre: CharSequence): String {
        if (isEmpty(cs) || !cs!!.toString().startsWith(pre)) return emptyIfNull(cs)
        return lowerFirst(cs.toString().substring(pre.length))
    }

    /** Removes [suf], then lower-cases the first character. */
    fun removeSufAndLowerFirst(cs: CharSequence?, suf: CharSequence): String {
        if (isEmpty(cs) || !cs!!.toString().endsWith(suf)) return emptyIfNull(cs)
        return lowerFirst(cs.toString().substring(0, cs.length - suf.length))
    }

    /**
     * Converts `some_name`/`SOME_NAME` to `someName`. The first character of [cs] is upper-cased
     * when [upperCaseFirst] is `true`.
     */
    fun toCamelCase(cs: CharSequence?, lowerCaseFirst: Boolean = true): String {
        if (isEmpty(cs)) return StrPool.EMPTY
        val sb = StringBuilder(cs!!.length)
        var upperNext = !lowerCaseFirst
        for (ch in cs) {
            if (ch == StrPool.UNDERLINE || ch == StrPool.DASHED || ch == StrPool.SPACE) {
                upperNext = true
            } else {
                sb.append(if (upperNext) ch.uppercaseChar() else ch.lowercaseChar())
                upperNext = false
            }
        }
        return sb.toString()
    }

    /**
     * Converts `someName`/`SOME_NAME` to `some_name`, matching Hutool's `toUnderlineCase`
     * (including its handling of digits after a lower-case letter).
     */
    fun toUnderlineCase(cs: CharSequence?, symbol: Char = StrPool.UNDERLINE): String {
        if (isEmpty(cs)) return StrPool.EMPTY
        val sb = StringBuilder(cs!!.length + 8)
        var preChar = 0.toChar()
        for (ch in cs) {
            val upperCase = ch.isUpperCase()
            if (upperCase && sb.isNotEmpty() && preChar != symbol) {
                sb.append(symbol)
            }
            sb.append(ch.lowercaseChar())
            preChar = ch
        }
        return sb.toString()
    }

    /** Separates camel-case words with [symbol]: `someName` → `some.name`. */
    fun toSymbolCase(cs: CharSequence?, symbol: Char): String {
        if (isEmpty(cs)) return StrPool.EMPTY
        val sb = StringBuilder(cs!!.length + 8)
        var preChar = 0.toChar()
        for (ch in cs) {
            val upperCase = ch.isUpperCase()
            if (upperCase && sb.isNotEmpty() && preChar != symbol && !preChar.isUpperCase()) {
                sb.append(symbol)
            }
            sb.append(if (upperCase) ch.lowercaseChar() else ch)
            preChar = ch
        }
        return sb.toString()
    }

    /** Lower-cases the whole string; `null` becomes the empty string. */
    fun toLowerCase(cs: CharSequence?): String = cs?.toString()?.lowercase() ?: StrPool.EMPTY

    /** Upper-cases the whole string; `null` becomes the empty string. */
    fun toUpperCase(cs: CharSequence?): String = cs?.toString()?.uppercase() ?: StrPool.EMPTY

    /** Same as [toLowerCase]; kept because Hutool shipped the misspelt name first. */
    fun toLoweCase(cs: CharSequence?): String = toLowerCase(cs)

    /** Swaps the case of every character: `aBc` → `AbC`. */
    fun swapCase(cs: CharSequence?): String {
        if (isEmpty(cs)) return StrPool.EMPTY
        val sb = StringBuilder(cs!!.length)
        for (ch in cs) {
            sb.append(if (ch.isUpperCase()) ch.lowercaseChar() else ch.uppercaseChar())
        }
        return sb.toString()
    }

    // ---------------------------------------------------------------------------------------------
    // Comparison
    // ---------------------------------------------------------------------------------------------

    /** `true` when both arguments are `null`, or both are equal character by character. */
    fun equals(cs1: CharSequence?, cs2: CharSequence?): Boolean =
        if (cs1 == null) cs2 == null else cs1.toString() == cs2?.toString()

    /** [equals] ignoring case. */
    fun equalsIgnoreCase(cs1: CharSequence?, cs2: CharSequence?): Boolean =
        if (cs1 == null) cs2 == null else cs1.toString().equals(cs2?.toString() ?: return false, ignoreCase = true)

    /** `true` when [cs] equals any of [css]. */
    fun equalsAny(cs: CharSequence?, vararg css: CharSequence?): Boolean =
        css.any { equals(cs, it) }

    /** [equalsAny] ignoring case. */
    fun equalsAnyIgnoreCase(cs: CharSequence?, vararg css: CharSequence?): Boolean =
        css.any { equalsIgnoreCase(cs, it) }

    /** `true` when the character at [index] of [cs] equals [c]. */
    fun equalsCharAt(cs: CharSequence?, c: Char, index: Int): Boolean =
        isNotEmpty(cs) && cs!!.length > index && index >= 0 && cs[index] == c

    /**
     * `true` when the substring of [cs] starting at [index] equals [other] (or is equal ignoring
     * case when [ignoreCase] is `true`).
     */
    fun isSubEquals(cs: CharSequence?, other: CharSequence?, ignoreCase: Boolean, startInclude: Int = 0): Boolean {
        if (cs == null || other == null) return false
        val end = startInclude + other.length
        if (startInclude < 0 || end > cs.length || cs.length - startInclude < other.length) return false
        return cs.subSequence(startInclude, end).toString()
            .equals(other.toString(), ignoreCase = ignoreCase)
    }

    /** Lexicographic comparison; `null` sorts before any value. */
    fun compare(cs1: CharSequence?, cs2: CharSequence?): Int = when {
        cs1 == null && cs2 == null -> 0
        cs1 == null -> -1
        cs2 == null -> 1
        else -> cs1.toString().compareTo(cs2.toString())
    }

    /** [compare] ignoring case. */
    fun compareIgnoreCase(cs1: CharSequence?, cs2: CharSequence?): Int = when {
        cs1 == null && cs2 == null -> 0
        cs1 == null -> -1
        cs2 == null -> 1
        else -> cs1.toString().lowercase().compareTo(cs2.toString().lowercase())
    }

    /**
     * Compares two dot-separated version strings numerically per segment: `1.10` sorts after `1.9`.
     * Numeric segments compare as numbers, non-numeric ones lexicographically.
     */
    fun compareVersion(version1: String?, version2: String?): Int {
        if (version1 == null && version2 == null) return 0
        if (version1 == null) return -1
        if (version2 == null) return 1
        val parts1 = version1.split(StrPool.DOT_STR)
        val parts2 = version2.split(StrPool.DOT_STR)
        val size = maxOf(parts1.size, parts2.size)
        for (index in 0 until size) {
            val p1 = parts1.getOrElse(index) { "0" }
            val p2 = parts2.getOrElse(index) { "0" }
            val n1 = p1.toLongOrNull()
            val n2 = p2.toLongOrNull()
            val result = if (n1 != null && n2 != null) n1.compareTo(n2) else p1.compareTo(p2)
            if (result != 0) return result
        }
        return 0
    }

    // ---------------------------------------------------------------------------------------------
    // Searching
    // ---------------------------------------------------------------------------------------------

    /** Index of [searchChar] in [cs], `-1` when absent. */
    fun indexOf(cs: CharSequence?, searchChar: Char, fromIndex: Int = 0): Int {
        if (isEmpty(cs)) return -1
        val text = cs!!
        if (fromIndex < 0) return -1
        for (index in fromIndex until text.length) {
            if (text[index] == searchChar) return index
        }
        return -1
    }

    /** Index of the first character of [searchStr] in [cs], `-1` when absent. */
    fun indexOf(cs: CharSequence?, searchStr: CharSequence, fromIndex: Int = 0): Int =
        if (isEmpty(cs)) -1 else cs!!.toString().indexOf(searchStr.toString(), fromIndex.coerceAtLeast(0))

    /** [indexOf] ignoring case. */
    fun indexOfIgnoreCase(cs: CharSequence?, searchStr: CharSequence, fromIndex: Int = 0, ignoreCase: Boolean = true): Int {
        if (isEmpty(cs) || isEmpty(searchStr)) return -1
        val text = cs.toString()
        val search = searchStr.toString()
        for (index in fromIndex.coerceAtLeast(0)..(text.length - search.length)) {
            if (text.regionMatches(index, search, 0, search.length, ignoreCase)) return index
        }
        return -1
    }

    /** The index of the [ordinalIndex]-th occurrence of [searchStr], `-1` when there are fewer. */
    fun ordinalIndexOf(cs: CharSequence?, searchStr: CharSequence, ordinalIndex: Int = 1): Int {
        if (isEmpty(cs) || isEmpty(searchStr) || ordinalIndex < 1) return -1
        val text = cs.toString()
        var index = -1
        var found = 0
        while (found < ordinalIndex) {
            index = text.indexOf(searchStr.toString(), index + 1)
            if (index < 0) return -1
            found++
        }
        return index
    }

    /** Last index of [searchChar], `-1` when absent. */
    fun lastIndexOf(cs: CharSequence?, searchChar: Char): Int =
        if (isEmpty(cs)) -1 else cs!!.toString().lastIndexOf(searchChar)

    /** Last index of [searchStr], `-1` when absent. */
    fun lastIndexOf(cs: CharSequence?, searchStr: CharSequence): Int =
        if (isEmpty(cs)) -1 else cs!!.toString().lastIndexOf(searchStr.toString())

    /** `true` when [cs] contains [searchStr]. */
    fun contains(cs: CharSequence?, searchStr: CharSequence): Boolean =
        isNotEmpty(cs) && searchStr.isNotEmpty() && cs!!.toString().contains(searchStr)

    /** `true` when [cs] contains any of [searchStrs]. */
    fun containsAny(cs: CharSequence?, vararg searchStrs: CharSequence?): Boolean =
        searchStrs.any { !it.isNullOrEmpty() && contains(cs, it) }

    /** `true` when [cs] contains every one of [searchStrs]. */
    fun containsAll(cs: CharSequence?, vararg searchStrs: CharSequence?): Boolean =
        searchStrs.all { !it.isNullOrEmpty() && contains(cs, it) }

    /** [contains] ignoring case. */
    fun containsIgnoreCase(cs: CharSequence?, searchStr: CharSequence): Boolean =
        indexOfIgnoreCase(cs, searchStr) >= 0

    /** `true` when [cs] contains any of [searchStrs], ignoring case. */
    fun containsAnyIgnoreCase(cs: CharSequence?, vararg searchStrs: CharSequence?): Boolean =
        searchStrs.any { !it.isNullOrEmpty() && containsIgnoreCase(cs, it) }

    /** `true` when [cs] contains a blank character. */
    fun containsBlank(cs: CharSequence?): Boolean {
        if (isEmpty(cs)) return false
        for (ch in cs!!) if (CharUtil.isBlankChar(ch)) return true
        return false
    }

    /** `true` when [cs] is made only of characters from [strs]. */
    fun containsOnly(cs: CharSequence?, vararg strs: CharSequence): Boolean {
        if (isEmpty(cs)) return false
        val allowed = strs.joinToString(StrPool.EMPTY)
        if (allowed.isEmpty()) return false
        for (ch in cs!!) if (!allowed.contains(ch)) return false
        return true
    }

    /** Counts the non-overlapping occurrences of [searchStr] in [cs]. */
    fun count(cs: CharSequence?, searchStr: CharSequence): Int {
        if (isEmpty(cs) || isEmpty(searchStr)) return 0
        val text = cs.toString()
        val search = searchStr.toString()
        var count = 0
        var index = text.indexOf(search)
        while (index >= 0) {
            count++
            index = text.indexOf(search, index + search.length)
        }
        return count
    }

    // ---------------------------------------------------------------------------------------------
    // Padding, masking and fitting
    // ---------------------------------------------------------------------------------------------

    /**
     * Masks the middle of [cs], keeping [left] characters at the start and [right] at the end.
     *
     * @param left characters to keep at the start
     * @param right characters to keep at the end
     * @param fixedChar the mask character
     */
    fun desensitized(cs: CharSequence?, left: Int, right: Int, fixedChar: Char = '*'): String {
        if (isEmpty(cs)) return StrPool.EMPTY
        val text = cs.toString()
        val length = text.length
        if (left + right >= length) return text
        val masked = StringBuilder(length)
        masked.append(text, 0, left)
        repeat(length - left - right) { masked.append(fixedChar) }
        masked.append(text, length - right, length)
        return masked.toString()
    }

    /** Cuts [cs] to at most [length] characters. */
    fun maxLength(cs: CharSequence?, length: Int): String? =
        if (isEmpty(cs)) null else if (cs!!.length <= length) cs.toString() else sub(cs, 0, length)

    /**
     * Cuts [cs] to at most [maxLength] characters, ending with `...` when something was cut.
     */
    fun brief(cs: CharSequence?, maxLength: Int): String? {
        if (isEmpty(cs)) return null
        val text = cs.toString()
        if (text.length <= maxLength) return text
        return sub(text, 0, maxLength - 3) + "..."
    }

    /**
     * Centers [cs] in a field of [size] characters, using [padChar]. The extra character (for an
     * odd difference) goes to the right, as Hutool does.
     */
    fun center(cs: CharSequence?, size: Int, padChar: Char = StrPool.SPACE): String {
        val text = emptyIfNull(cs)
        if (text.length >= size) return text
        val total = size - text.length
        val left = total / 2
        val right = total - left
        return StringBuilder(size)
            .append(padChar.toString().repeat(left))
            .append(text)
            .append(padChar.toString().repeat(right))
            .toString()
    }

    /** Pads [cs] on the left until it is [size] characters long. */
    fun padPre(cs: CharSequence?, size: Int, padChar: Char = StrPool.SPACE): String {
        val text = emptyIfNull(cs)
        if (text.length >= size) return text
        return padChar.toString().repeat(size - text.length) + text
    }

    /** Pads [cs] on the right until it is [size] characters long. */
    fun padAfter(cs: CharSequence?, size: Int, padChar: Char = StrPool.SPACE): String {
        val text = emptyIfNull(cs)
        if (text.length >= size) return text
        return text + padChar.toString().repeat(size - text.length)
    }

    /**
     * Fixes the length: shorter input is padded on the right, longer input is cut. `null` produces
     * `length` pad characters.
     */
    fun fixLength(cs: CharSequence?, length: Int, padChar: Char = StrPool.SPACE): String {
        if (cs == null) return padChar.toString().repeat(length)
        return if (cs.length > length) sub(cs, 0, length) else padAfter(cs, length, padChar)
    }

    // ---------------------------------------------------------------------------------------------
    // Predicates
    // ---------------------------------------------------------------------------------------------

    /** `true` when every character of [cs] is a digit (an empty string is not numeric). */
    fun isNumeric(cs: CharSequence?): Boolean {
        if (isEmpty(cs)) return false
        for (ch in cs!!) if (!ch.isDigit()) return false
        return true
    }

    /** `true` when [cs] has no upper-case letters. */
    fun isLowerCase(cs: CharSequence?): Boolean {
        if (isEmpty(cs)) return false
        for (ch in cs!!) if (ch.isUpperCase()) return false
        return true
    }

    /** `true` when [cs] has no lower-case letters. */
    fun isUpperCase(cs: CharSequence?): Boolean {
        if (isEmpty(cs)) return false
        for (ch in cs!!) if (ch.isLowerCase()) return false
        return true
    }

    /** `true` when every character of [cs] satisfies [matcher]. */
    fun isAllCharMatch(cs: CharSequence?, matcher: (Char) -> Boolean): Boolean {
        if (isEmpty(cs)) return false
        for (ch in cs!!) if (!matcher(ch)) return false
        return true
    }

    // ---------------------------------------------------------------------------------------------
    // Starts-with / ends-with
    // ---------------------------------------------------------------------------------------------

    /** `true` when [cs] starts with [prefix]. */
    fun startWith(cs: CharSequence?, prefix: CharSequence): Boolean =
        isNotEmpty(cs) && cs!!.toString().startsWith(prefix)

    /** [startWith] ignoring case. */
    fun startWithIgnoreCase(cs: CharSequence?, prefix: CharSequence): Boolean =
        isNotEmpty(cs) && cs!!.toString().startsWith(prefix, ignoreCase = true)

    /** `true` when [cs] starts with [prefix] while ignoring the case of [prefix]'s first character only. */
    fun startWithIgnoreEquals(cs: CharSequence?, prefix: CharSequence): Boolean {
        if (isEmpty(cs) || isEmpty(prefix)) return false
        val text = cs.toString()
        if (text.length < prefix.length) return false
        // The first character is compared ignoring case; the rest must match exactly.
        if (!CharUtil.equals(text[0], prefix[0], caseInsensitive = true)) return false
        return text.regionMatches(1, prefix.toString(), 1, prefix.length - 1, false)
    }

    /** `true` when [cs] starts with any of [prefixes]. */
    fun startWithAny(cs: CharSequence?, vararg prefixes: CharSequence?): Boolean =
        prefixes.any { !it.isNullOrEmpty() && startWith(cs, it) }

    /** [startWithAny] ignoring case. */
    fun startWithAnyIgnoreCase(cs: CharSequence?, vararg prefixes: CharSequence?): Boolean =
        prefixes.any { !it.isNullOrEmpty() && startWithIgnoreCase(cs, it) }

    /** `true` when [cs] ends with [suffix]. */
    fun endWith(cs: CharSequence?, suffix: CharSequence): Boolean =
        isNotEmpty(cs) && cs!!.toString().endsWith(suffix)

    /** [endWith] ignoring case. */
    fun endWithIgnoreCase(cs: CharSequence?, suffix: CharSequence): Boolean =
        isNotEmpty(cs) && cs!!.toString().endsWith(suffix, ignoreCase = true)

    /** `true` when [cs] ends with any of [suffixes]. */
    fun endWithAny(cs: CharSequence?, vararg suffixes: CharSequence?): Boolean =
        suffixes.any { !it.isNullOrEmpty() && endWith(cs, it) }

    /** [endWithAny] ignoring case. */
    fun endWithAnyIgnoreCase(cs: CharSequence?, vararg suffixes: CharSequence?): Boolean =
        suffixes.any { !it.isNullOrEmpty() && endWithIgnoreCase(cs, it) }

    // ---------------------------------------------------------------------------------------------
    // Conversions
    // ---------------------------------------------------------------------------------------------

    /** `toString()` of [obj], or `null` when [obj] is `null`. */
    fun toStringOrNull(obj: Any?): String? = obj?.toString()

    /** `toString()` of [obj], or the empty string when [obj] is `null`. */
    fun toStringOrEmpty(obj: Any?): String = obj?.toString() ?: StrPool.EMPTY

    /** An identity cast that makes a `CharSequence?` usable where a `String` is required. */
    fun str(obj: Any?): String? = if (obj is CharSequence) obj.toString() else obj?.toString()

    /** A [StringBuilder] pre-filled with [cs]. */
    fun strBuilder(cs: CharSequence? = null): StringBuilder =
        if (cs == null) StringBuilder() else StringBuilder(cs)

    /** Reverses [cs] by code point, keeping surrogate pairs in order. */
    fun reverse(cs: CharSequence?): String {
        if (isEmpty(cs)) return StrPool.EMPTY
        val text = cs.toString()
        return text.reversed()
    }
}
