package io.github.shijianjs.kmphutool.core.comparator

/**
 * Orders values by their position in a fixed array; values not in the array go first by default.
 *
 * Port of `cn.hutool.core.comparator.IndexedComparator`. Upstream's map constructor is private, so
 * here the array is expanded into a `HashMap`, exactly as its public constructor does.
 *
 * @hutool cn.hutool.core.comparator.IndexedComparator
 */
class IndexedComparator<T>(
    private val atEndIfMiss: Boolean = false,
    objs: Array<out T>,
) : Comparator<T> {
    private val order: Map<T, Int>

    init {
        val map = HashMap<T, Int>(objs.size * 2)
        for (index in objs.indices) {
            map[objs[index]] = index
        }
        order = map
    }

    override fun compare(o1: T, o2: T): Int {
        val index1 = getOrder(o1)
        val index2 = getOrder(o2)
        if (index1 == index2) {
            // Either both are missing or both are the same element.
            return if (index1 < 0 || index1 == order.size) 1 else 0
        }
        return index1.compareTo(index2)
    }

    private fun getOrder(value: T): Int = order[value] ?: if (atEndIfMiss) order.size else -1
}
