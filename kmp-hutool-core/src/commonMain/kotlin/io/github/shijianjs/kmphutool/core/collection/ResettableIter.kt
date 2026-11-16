package io.github.shijianjs.kmphutool.core.collection

/**
 * An iterator that can restart from the beginning.
 *
 * Port of `cn.hutool.core.collection.ResettableIter`.
 *
 * @hutool cn.hutool.core.collection.ResettableIter
 */
interface ResettableIter<E> : Iterator<E> {
    /** Restarts the walk; after the call the iterator behaves like a fresh one. */
    fun reset()
}
