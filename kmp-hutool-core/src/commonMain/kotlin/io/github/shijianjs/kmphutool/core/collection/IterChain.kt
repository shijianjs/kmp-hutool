package io.github.shijianjs.kmphutool.core.collection

import io.github.shijianjs.kmphutool.core.lang.Chain

/**
 * Concatenates several iterators into one traversal.
 *
 * Port of `cn.hutool.core.collection.IterChain` (Jodd lineage). Duplicate iterators are rejected,
 * matching upstream.
 *
 * @hutool cn.hutool.core.collection.IterChain
 */
class IterChain<T>(vararg iterators: Iterator<T>) : Iterator<T>, Chain<Iterator<T>, IterChain<T>> {
    private val allIterators = mutableListOf<Iterator<T>>()
    private var currentIter = -1

    init {
        iterators.forEach { addChain(it) }
    }

    override fun addChain(element: Iterator<T>): IterChain<T> {
        require(!allIterators.contains(element)) { "Duplicate iterator" }
        allIterators.add(element)
        return this
    }

    override fun hasNext(): Boolean {
        if (currentIter == -1) currentIter = 0
        for (i in currentIter until allIterators.size) {
            if (allIterators[i].hasNext()) {
                currentIter = i
                return true
            }
        }
        return false
    }

    override fun next(): T {
        if (!hasNext()) throw NoSuchElementException()
        return allIterators[currentIter].next()
    }
    override fun iterator(): Iterator<Iterator<T>> = allIterators.iterator()
}
