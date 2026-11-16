package io.github.shijianjs.kmphutool.core.collection

/**
 * A priority queue that keeps at most [capacity] elements.
 *
 * Port of `cn.hutool.core.collection.BoundedPriorityQueue`. `java.util.PriorityQueue` is JVM-only,
 * so this keeps a sorted list instead; ordering and eviction follow upstream: elements are ordered
 * by the reversed comparator, and a full queue rejects a new element that does not beat the head.
 *
 * @hutool cn.hutool.core.collection.BoundedPriorityQueue
 */
class BoundedPriorityQueue<E>(
    private val capacity: Int,
    comparator: Comparator<in E>? = null,
) : Iterable<E> {
    private val compare: (E, E) -> Int = { a, b ->
        if (comparator != null) {
            comparator.compare(a, b)
        } else {
            @Suppress("UNCHECKED_CAST")
            (a as Comparable<E>).compareTo(b)
        }
    }
    private val elements = mutableListOf<E>()

    init {
        require(capacity > 0) { "capacity must be > 0" }
    }

    /** The element that would be evicted first: the head under reversed ordering. */
    fun peek(): E? = elements.firstOrNull()

    /** Adds [e], evicting the head when full; returns whether the queue changed. */
    fun offer(e: E): Boolean {
        if (elements.size >= capacity) {
            val head = elements.first()
            // Compare against the head using the same reversed ordering as upstream.
            if (-compare(e, head) <= 0) return true
            elements.removeAt(0)
        }
        insert(e)
        return true
    }

    /** Adds [e], throwing when it cannot be accepted. */
    fun add(e: E): Boolean = offer(e)

    /** Adds every element of [values]; returns whether the queue changed. */
    fun addAll(values: Array<out E>): Boolean {
        var changed = false
        values.forEach { changed = offer(it) || changed }
        return changed
    }

    /** Removes and returns the head element. */
    fun poll(): E? = if (elements.isEmpty()) null else elements.removeAt(0)

    /** Current number of elements. */
    fun size(): Int = elements.size

    /** `true` when no element is queued. */
    fun isEmpty(): Boolean = elements.isEmpty()

    /** Elements sorted by the original comparator. */
    fun toList(): MutableList<E> = elements.sortedWith(Comparator { a, b -> compare(a, b) }).toMutableList()

    override fun iterator(): Iterator<E> = toList().iterator()

    private fun insert(e: E) {
        var index = 0
        while (index < elements.size && -compare(e, elements[index]) > 0) index++
        elements.add(index, e)
    }
}
