package io.github.shijianjs.kmphutool.core.collection

import io.github.shijianjs.kmphutool.core.lang.Assert

/**
 * A live view of a collection with elements mapped through a transformation.
 *
 * Port of `cn.hutool.core.collection.TransCollection`. Kotlin's `Collection` is read-only by
 * contract, so mutation goes through [clear] and [removeIf] on the backing collection.
 *
 * @hutool cn.hutool.core.collection.TransCollection
 */
class TransCollection<F, T>(
    private val fromCollection: MutableCollection<F>,
    private val function: (F) -> T,
) : Collection<T> {
    init {
        Assert.notNull(fromCollection)
        Assert.notNull(function)
    }

    override val size: Int get() = fromCollection.size

    override fun isEmpty(): Boolean = fromCollection.isEmpty()

    override fun iterator(): Iterator<T> = TransIter(fromCollection.iterator(), function)

    override fun contains(element: T): Boolean = any { it == element }

    override fun containsAll(elements: Collection<T>): Boolean = elements.all { contains(it) }

    /** Applies [action] to every mapped element. */
    fun forEachMapped(action: (T) -> Unit) {
        fromCollection.forEach { action(function(it)) }
    }

    /** Removes elements whose transformed value matches [filter]. */
    fun removeIf(filter: (T) -> Boolean): Boolean {
        val toRemove = fromCollection.filter { filter(function(it)) }
        return fromCollection.removeAll(toRemove.toSet())
    }

    /** Clears the backing collection. */
    fun clear() {
        fromCollection.clear()
    }
}

