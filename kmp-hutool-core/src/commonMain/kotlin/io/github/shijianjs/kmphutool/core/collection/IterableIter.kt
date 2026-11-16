package io.github.shijianjs.kmphutool.core.collection

/**
 * An `Iterator` that can also be iterated, so the same object works in a `for` loop and with
 * `hasNext`/`next`.
 *
 * Port of `cn.hutool.core.collection.IterableIter`.
 *
 * @hutool cn.hutool.core.collection.IterableIter
 */
interface IterableIter<E> : Iterator<E>, Iterable<E> {
    override fun iterator(): Iterator<E> = this
}
