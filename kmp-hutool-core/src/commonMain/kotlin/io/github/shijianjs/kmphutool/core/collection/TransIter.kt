package io.github.shijianjs.kmphutool.core.collection

import io.github.shijianjs.kmphutool.core.lang.Assert

/**
 * Maps elements of an iterator through a transformation.
 *
 * Port of `cn.hutool.core.collection.TransIter`.
 *
 * @hutool cn.hutool.core.collection.TransIter
 */
class TransIter<F, T>(
    private val backingIterator: Iterator<F>,
    private val func: (F) -> T,
) : Iterator<T> {
    init {
        Assert.notNull(backingIterator)
        Assert.notNull(func)
    }

    override fun hasNext(): Boolean = backingIterator.hasNext()

    override fun next(): T = func(backingIterator.next())}
