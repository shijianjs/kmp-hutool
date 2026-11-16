package io.github.shijianjs.kmphutool.core.collection

/**
 * An iterator whose elements are computed on demand.
 *
 * Port of `cn.hutool.core.collection.ComputeIter`. Subclasses implement [computeNext] and return
 * `null` to signal the end; the computed element is cached until [next] consumes it, then cleared
 * so the next computation starts fresh (handles `null`-free sequences only, like upstream).
 *
 * @hutool cn.hutool.core.collection.ComputeIter
 */
abstract class ComputeIter<T : Any> : Iterator<T> {
    private var next: T? = null
    private var finished = false

    /** Produces the next element, or `null` when the sequence is exhausted. */
    protected abstract fun computeNext(): T?

    override fun hasNext(): Boolean {
        if (next != null) return true
        if (finished) return false
        val result = computeNext()
        if (result == null) {
            finished = true
            return false
        }
        next = result
        return true
    }

    override fun next(): T {
        if (!hasNext()) throw NoSuchElementException("No more lines")
        val result = next!!
        next = null
        return result
    }

    /** Marks the iterator finished, dropping any cached element. */
    fun finish() {
        finished = true
        next = null
    }

    /** Resets traversal state so the iterator can be reused. */
    fun resetState() {
        finished = false
        next = null
    }
}
