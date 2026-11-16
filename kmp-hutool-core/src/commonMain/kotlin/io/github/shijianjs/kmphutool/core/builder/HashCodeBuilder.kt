package io.github.shijianjs.kmphutool.core.builder

/**
 * A hand-fed `hashCode` implementation: append field hashes, then read [toHashCode].
 *
 * Port of `cn.hutool.core.builder.HashCodeBuilder` (Commons Lang lineage). Reflection-based variants
 * are omitted together with the rest of the reflection support.
 *
 * @hutool cn.hutool.core.builder.HashCodeBuilder
 */
class HashCodeBuilder(
    private val initialOddNumber: Int = 17,
    private val multiplierOddNumber: Int = 37,
) : Builder<Int> {
    private var iTotal = initialOddNumber

    init {
        require(initialOddNumber % 2 == 1 && multiplierOddNumber % 2 == 1) {
            "HashCodeBuilder requires odd initial and multiplier values"
        }
    }

    /** Appends a [Long], folding its two halves like upstream. */
    fun append(value: Long): HashCodeBuilder =
        append(value.toInt()).append((value shr 32).toInt())

    fun append(value: Int): HashCodeBuilder = apply { iTotal = iTotal * multiplierOddNumber + value }

    fun append(value: Short): HashCodeBuilder = append(value.toInt())

    fun append(value: Char): HashCodeBuilder = append(value.code)

    fun append(value: Byte): HashCodeBuilder = append(value.toInt())

    /** `true`/`false` contribute the constants upstream uses (0 / 1). */
    fun append(value: Boolean): HashCodeBuilder = append(if (value) 0 else 1)

    fun append(value: Double): HashCodeBuilder = append(value.toRawBits())

    fun append(value: Float): HashCodeBuilder = append(value.toRawBits())

    /** Appends an object's hash; arrays fold in their elements. */
    fun append(value: Any?): HashCodeBuilder = apply {
        when (value) {
            null -> append(0)
            is BooleanArray -> value.forEach { append(it) }
            is ByteArray -> value.forEach { append(it) }
            is CharArray -> value.forEach { append(it) }
            is DoubleArray -> value.forEach { append(it) }
            is FloatArray -> value.forEach { append(it) }
            is IntArray -> value.forEach { append(it) }
            is LongArray -> value.forEach { append(it) }
            is ShortArray -> value.forEach { append(it) }
            is Array<*> -> value.forEach { append(it) }
            else -> append(value.hashCode())
        }
    }

    override fun build(): Int = toHashCode()

    /** The accumulated hash. */
    fun toHashCode(): Int = iTotal
}
