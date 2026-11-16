package io.github.shijianjs.kmphutool.core.builder

/**
 * A hand-fed `equals` implementation: append field comparisons, then ask [isEquals].
 *
 * Port of `cn.hutool.core.builder.EqualsBuilder` (Commons Lang lineage). The `reflectionEquals`
 * family is intentionally absent: this library does not port reflection, so callers append fields
 * explicitly.
 *
 * @hutool cn.hutool.core.builder.EqualsBuilder
 */
class EqualsBuilder : Builder<Boolean> {
    private var isEquals = true

    /** Folds in the result of `super.equals`. */
    fun appendSuper(superEquals: Boolean): EqualsBuilder = apply { if (isEquals) isEquals = superEquals }

    /** Compares two objects, treating arrays by content and `null` as unequal to a value. */
    fun append(lhs: Any?, rhs: Any?): EqualsBuilder = apply {
        if (!isEquals) return@apply
        when {
            lhs === rhs -> Unit
            lhs == null || rhs == null -> isEquals = false
            lhs is Array<*> && rhs is Array<*> -> isEquals = lhs.contentEquals(rhs)
            else -> isEquals = lhs == rhs
        }
    }

    fun append(lhs: Long, rhs: Long): EqualsBuilder = apply { if (isEquals) isEquals = lhs == rhs }
    fun append(lhs: Int, rhs: Int): EqualsBuilder = apply { if (isEquals) isEquals = lhs == rhs }
    fun append(lhs: Short, rhs: Short): EqualsBuilder = apply { if (isEquals) isEquals = lhs == rhs }
    fun append(lhs: Char, rhs: Char): EqualsBuilder = apply { if (isEquals) isEquals = lhs == rhs }
    fun append(lhs: Byte, rhs: Byte): EqualsBuilder = apply { if (isEquals) isEquals = lhs == rhs }
    fun append(lhs: Boolean, rhs: Boolean): EqualsBuilder = apply { if (isEquals) isEquals = lhs == rhs }
    fun append(lhs: Double, rhs: Double): EqualsBuilder = apply { if (isEquals) isEquals = lhs == rhs }
    fun append(lhs: Float, rhs: Float): EqualsBuilder = apply { if (isEquals) isEquals = lhs == rhs }

    /** `true` while every appended comparison has matched. */
    fun isEquals(): Boolean = isEquals

    override fun build(): Boolean = isEquals()

    /** Resets the builder so it can be reused. */
    fun reset() {
        isEquals = true
    }
}
