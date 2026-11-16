package io.github.shijianjs.kmphutool.core.collection

import io.github.shijianjs.kmphutool.core.lang.Assert
import io.github.shijianjs.kmphutool.core.lang.Filter

/**
 * Wraps an iterator and only surfaces elements accepted by a [Filter].
 *
 * Port of `cn.hutool.core.collection.FilterIter` (Apache Commons Collections lineage).
 *
 * @hutool cn.hutool.core.collection.FilterIter
 */
class FilterIter<E>(
    private val iterator: Iterator<E>,
    private val filter: Filter<in E>? = null,
) : Iterator<E> {
    private var nextObject: E? = null
    private var nextObjectSet = false

    init {
        Assert.notNull(iterator)
    }

    override fun hasNext(): Boolean = nextObjectSet || setNextObject()

    override fun next(): E {
        if (!nextObjectSet && !setNextObject()) throw NoSuchElementException()
        nextObjectSet = false
        @Suppress("UNCHECKED_CAST")
        return nextObject as E
    }
    /** The wrapped iterator. */
    fun getIterator(): Iterator<E> = iterator

    /** The filter, possibly `null`. */
    fun getFilter(): Filter<in E>? = filter

    private fun setNextObject(): Boolean {
        while (iterator.hasNext()) {
            val obj = iterator.next()
            if (filter == null || filter.accept(obj)) {
                nextObject = obj
                nextObjectSet = true
                return true
            }
        }
        return false
    }
}
