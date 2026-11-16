package io.github.shijianjs.kmphutool.core.text

/**
 * Joins values into one string, with the delimiter/prefix/suffix and the null policy configurable.
 *
 * Port of `cn.hutool.core.text.StrJoiner`.
 *
 * @hutool cn.hutool.core.text.StrJoiner
 */
class StrJoiner private constructor(
    private var delimiter: CharSequence,
    private var prefix: CharSequence = StrPool.EMPTY,
    private var suffix: CharSequence = StrPool.EMPTY,
) {
    private val builder = StringBuilder()

    /** How a `null` element is rendered. */
    enum class NullMode {
        /** The literal `null`, as `StringBuilder` does. */
        NULL,

        /** The element is skipped entirely. */
        IGNORE,

        /** The element becomes the empty string. */
        EMPTY,
    }

    private var nullMode = NullMode.NULL
    private var wrapElement = false
    private var hasElement = false
    private var emptyResult: String? = null

    /** A joiner that starts with [delimiter] only after the first element. */
    fun setDelimiter(delimiter: CharSequence): StrJoiner = apply { this.delimiter = delimiter }

    /** Text emitted before the first element. */
    fun setPrefix(prefix: CharSequence): StrJoiner = apply { this.prefix = prefix }

    /** Text emitted after the last element. */
    fun setSuffix(suffix: CharSequence): StrJoiner = apply { this.suffix = suffix }

    /** When `true`, every element is wrapped as `prefix + element + suffix`. */
    fun setWrapElement(wrapElement: Boolean): StrJoiner = apply { this.wrapElement = wrapElement }

    /** Sets how `null` elements are rendered. */
    fun setNullMode(nullMode: NullMode): StrJoiner = apply { this.nullMode = nullMode }

    /** The result returned when nothing was appended; `null` means "use the normal prefix/suffix". */
    fun setEmptyResult(emptyResult: String?): StrJoiner = apply { this.emptyResult = emptyResult }

    /** Appends one value. */
    fun append(obj: Any?): StrJoiner = appendValue(obj)

    /** Appends every element of [array]. */
    fun <T> append(array: Array<T>?): StrJoiner {
        array?.forEach { append(it) }
        return this
    }

    /** Appends every element of [iterable]. */
    fun <T> append(iterable: Iterable<T>?): StrJoiner {
        iterable?.forEach { append(it) }
        return this
    }

    /** Appends every element of [iterator]. */
    fun <T> append(iterator: Iterator<T>?): StrJoiner {
        while (iterator != null && iterator.hasNext()) append(iterator.next())
        return this
    }

    /** Appends [csq] as one element, not per-character. */
    fun append(csq: CharSequence?): StrJoiner = appendValue(csq)

    /** Appends one character. */
    fun append(c: Char): StrJoiner = appendElement(c.toString())

    /**
     * Appends [csq] with [startInclude] inclusive and [endExclude] exclusive.
     *
     * @throws IndexOutOfBoundsException when the range is outside [csq]
     */
    fun append(csq: CharSequence?, startInclude: Int, endExclude: Int): StrJoiner =
        appendElement(csq?.subSequence(startInclude, endExclude)?.toString() ?: StrPool.EMPTY)

    private fun appendValue(obj: Any?): StrJoiner {
        if (obj == null) {
            when (nullMode) {
                NullMode.NULL -> appendElement("null")
                NullMode.EMPTY -> appendElement(StrPool.EMPTY)
                NullMode.IGNORE -> Unit
            }
            return this
        }
        if (obj is Array<*>) {
            // A reference array is flattened, as Hutool does for Java arrays.
            obj.forEach { appendValue(it) }
            return this
        }
        appendElement(obj.toString())
        return this
    }

    /** Merges another joiner's content into this one, sharing the settings of the last one set. */
    fun merge(other: StrJoiner): StrJoiner {
        if (other.builder.isEmpty()) return this
        if (hasElement) builder.append(delimiter)
        builder.append(other.builder)
        hasElement = hasElement || other.hasElement
        return this
    }

    /** The length of the content appended so far, excluding prefix/suffix. */
    fun length(): Int = builder.length

    override fun toString(): String {
        if (!hasElement) {
            emptyResult?.let { return it }
        }
        // With element wrapping the prefix/suffix are already around every element.
        if (wrapElement) return builder.toString()
        return StringBuilder().append(prefix).append(builder).append(suffix).toString()
    }

    private fun appendElement(text: String): StrJoiner {
        if (hasElement) {
            builder.append(delimiter)
        } else {
            hasElement = true
        }
        if (wrapElement) builder.append(prefix)
        builder.append(text)
        if (wrapElement) builder.append(suffix)
        return this
    }

    companion object {
        /** Creates a joiner with [delimiter] between elements. */
        fun of(delimiter: CharSequence): StrJoiner = StrJoiner(delimiter)

        /** Creates a joiner with a delimiter, a prefix and a suffix. */
        fun of(delimiter: CharSequence, prefix: CharSequence, suffix: CharSequence): StrJoiner =
            StrJoiner(delimiter, prefix, suffix)

        /** Creates a joiner that copies another joiner's settings and content. */
        fun of(other: StrJoiner): StrJoiner = StrJoiner(other.delimiter, other.prefix, other.suffix)
            .setNullMode(other.nullMode)
            .setWrapElement(other.wrapElement)
            .merge(other)
    }
}
