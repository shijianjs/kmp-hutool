package io.github.shijianjs.kmphutool.core.builder

/**
 * A hand-fed `compareTo` implementation.
 *
 * Port of `cn.hutool.core.builder.CompareToBuilder` (Commons Lang lineage). `null` sorts before any
 * value, arrays compare element by element, and comparison stops at the first difference. The
 * reflection-based `reflectionCompare` overloads are omitted.
 *
 * @hutool cn.hutool.core.builder.CompareToBuilder
 */
class CompareToBuilder : Builder<Int> {
    private var comparison = 0

    /** Compares two [Comparable]s with Hutool's null ordering. */
    fun append(lhs: Any?, rhs: Any?, comparator: Comparator<Any?>? = null): CompareToBuilder = apply {
        if (comparison != 0) return@apply
        comparison = when {
            lhs === rhs -> 0
            lhs == null -> -1
            rhs == null -> 1
            comparator != null -> comparator.compare(lhs, rhs)
            else -> compareValues(lhs, rhs)
        }
    }

    fun append(lhs: Long, rhs: Long): CompareToBuilder = apply { if (comparison == 0) comparison = lhs.compareTo(rhs) }
    fun append(lhs: Int, rhs: Int): CompareToBuilder = apply { if (comparison == 0) comparison = lhs.compareTo(rhs) }
    fun append(lhs: Short, rhs: Short): CompareToBuilder = apply { if (comparison == 0) comparison = lhs.compareTo(rhs) }
    fun append(lhs: Char, rhs: Char): CompareToBuilder = apply { if (comparison == 0) comparison = lhs.compareTo(rhs) }
    fun append(lhs: Byte, rhs: Byte): CompareToBuilder = apply { if (comparison == 0) comparison = lhs.compareTo(rhs) }
    fun append(lhs: Boolean, rhs: Boolean): CompareToBuilder = apply {
        if (comparison == 0) comparison = lhs.compareTo(rhs)
    }
    fun append(lhs: Double, rhs: Double): CompareToBuilder = apply {
        if (comparison == 0) comparison = lhs.compareTo(rhs)
    }
    fun append(lhs: Float, rhs: Float): CompareToBuilder = apply {
        if (comparison == 0) comparison = lhs.compareTo(rhs)
    }

    override fun build(): Int = toComparison()

    /** The comparison result: negative, zero, or positive. */
    fun toComparison(): Int = comparison

    @Suppress("UNCHECKED_CAST")
    private fun compareValues(lhs: Any, rhs: Any): Int {
        if (lhs is Array<*> && rhs is Array<*>) {
            if (lhs.size != rhs.size) return lhs.size.compareTo(rhs.size)
            for (i in lhs.indices) {
                append(lhs[i], rhs[i])
                if (comparison != 0) break
            }
            return comparison
        }
        return (lhs as Comparable<Any>).compareTo(rhs)
    }
}
