package io.github.shijianjs.kmphutool.core.comparator

/**
 * Orders values by which of a fixed list of type predicates matches them first.
 *
 * Kotlin rewrite of `cn.hutool.core.comparator.InstanceComparator` (itself adapted from Spring).
 * `java.lang.Class` does not exist in `commonMain`, so the Java `Class<?>...` order becomes a
 * `vararg` of predicates, e.g. `InstanceComparator<Any>(false, { it is String }, { it is Int })`.
 * A value matching none goes first unless [atEndIfMiss] is set.
 *
 * @hutool cn.hutool.core.comparator.InstanceComparator
 */
class InstanceComparator<T>(
    private val atEndIfMiss: Boolean = false,
    vararg instanceOrder: (T) -> Boolean,
) : Comparator<T> {
    private val instanceOrder: Array<out (T) -> Boolean> = instanceOrder

    override fun compare(o1: T, o2: T): Int = getOrder(o1).compareTo(getOrder(o2))

    private fun getOrder(value: T): Int {
        if (value != null) {
            for (index in instanceOrder.indices) {
                if (instanceOrder[index](value)) return index
            }
        }
        return if (atEndIfMiss) instanceOrder.size else -1
    }
}
