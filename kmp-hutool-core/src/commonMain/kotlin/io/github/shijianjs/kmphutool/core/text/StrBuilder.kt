package io.github.shijianjs.kmphutool.core.text

/**
 * A mutable character sequence with the string helpers Hutool code reaches for most often.
 *
 * Port of `cn.hutool.core.text.StrBuilder`. The Java class extends `CharSequence` and exposes about
 * eighty methods; the port keeps the ones that make sense without mutable Java arrays. The content is
 * held as a [String] rather than a `StringBuilder` because `StringBuilder.delete`/`setCharAt`/`replace`
 * are JVM-only; every mutation rewrites the string, which is what Kotlin/JS and Wasm do internally
 * anyway. Every mutator returns `this`, so calls chain.
 *
 * @hutool cn.hutool.core.text.StrBuilder
 */
class StrBuilder(text: CharSequence? = null) : CharSequence {
    private var content: String = text?.toString() ?: StrPool.EMPTY

    override val length: Int
        get() = content.length

    override fun get(index: Int): Char = content[index]

    override fun subSequence(startIndex: Int, endIndex: Int): CharSequence =
        content.substring(startIndex.coerceIn(0, content.length), endIndex.coerceIn(0, content.length))

    /** `true` when nothing has been appended. */
    fun isEmpty(): Boolean = content.isEmpty()

    /** `true` when at least one character is present. */
    fun isNotEmpty(): Boolean = content.isNotEmpty()

    /** Removes every character. */
    fun clear(): StrBuilder = apply { content = StrPool.EMPTY }

    /** Appends [obj] via `toString`; `null` is skipped, as Hutool's `append(Object)` skips it. */
    fun append(obj: Any?): StrBuilder = apply {
        if (obj != null) content += obj.toString()
    }

    /** Appends one character. */
    fun append(ch: Char): StrBuilder = apply { content += ch }

    /** Appends [csq] as a whole; `null` is skipped. */
    fun append(csq: CharSequence?): StrBuilder = apply { if (csq != null) content += csq.toString() }

    /** Appends `csq[startInclude, endExclude)`; `null` is skipped. */
    fun append(csq: CharSequence?, startInclude: Int, endExclude: Int): StrBuilder = apply {
        if (csq != null) content += subOf(csq, startInclude, endExclude)
    }

    /** Inserts [obj] at [index]. */
    fun insert(index: Int, obj: Any?): StrBuilder = apply {
        val at = index.coerceIn(0, content.length)
        content = content.substring(0, at) + obj.toString() + content.substring(at)
    }

    /** Inserts [csq] at [index]. */
    fun insert(index: Int, csq: CharSequence?): StrBuilder = apply {
        val at = index.coerceIn(0, content.length)
        content = content.substring(0, at) + (csq?.toString() ?: StrPool.EMPTY) + content.substring(at)
    }

    /** Deletes `[startInclude, endExclude)`. */
    fun delete(startInclude: Int, endExclude: Int): StrBuilder = apply {
        val start = startInclude.coerceIn(0, content.length)
        val end = endExclude.coerceIn(start, content.length)
        content = content.removeRange(start, end)
    }

    /** Deletes the character at [index]. */
    fun deleteCharAt(index: Int): StrBuilder = apply {
        if (index in content.indices) content = content.removeRange(index, index + 1)
    }

    /** Replaces `[startInclude, endExclude)` with [str]. */
    fun replace(startInclude: Int, endExclude: Int, str: CharSequence?): StrBuilder = apply {
        val start = startInclude.coerceIn(0, content.length)
        val end = endExclude.coerceIn(start, content.length)
        content = content.replaceRange(start, end, str?.toString() ?: StrPool.EMPTY)
    }

    /** Writes [ch] at [index]. */
    fun setCharAt(index: Int, ch: Char): StrBuilder = apply {
        if (index in content.indices) content = content.replaceRange(index, index + 1, ch.toString())
    }

    /** Reverses the characters in place. */
    fun reverse(): StrBuilder = apply { content = content.reversed() }

    /** Trims the ends in place. */
    fun trim(): StrBuilder = apply { content = content.trim() }

    /** The first index of [str], or `-1`. */
    fun indexOf(str: CharSequence): Int = content.indexOf(str.toString())

    /** The last index of [str], or `-1`. */
    fun lastIndexOf(str: CharSequence): Int = content.lastIndexOf(str.toString())

    /** `true` when [str] occurs. */
    fun contains(str: CharSequence): Boolean = indexOf(str) >= 0

    /** The text between [startInclude] and [endExclude]. */
    fun subString(startInclude: Int, endExclude: Int): String =
        content.substring(startInclude.coerceIn(0, content.length), endExclude.coerceIn(0, content.length))

    /** Splits the content on [separator] and trims every part. */
    fun splitToArray(separator: CharSequence): Array<String> =
        StrSplitter.splitTrim(content, separator, true).toTypedArray()

    override fun toString(): String = content

    private fun subOf(csq: CharSequence, startInclude: Int, endExclude: Int): String {
        val start = startInclude.coerceIn(0, csq.length)
        val end = endExclude.coerceIn(start, csq.length)
        return csq.subSequence(start, end).toString()
    }
}
