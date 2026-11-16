package io.github.shijianjs.kmphutool.core.comparator

import io.github.shijianjs.kmphutool.core.lang.Chain

/**
 * A chain of comparators: each one runs until a non-zero result, then the direction flag inverts it.
 *
 * Port of `cn.hutool.core.comparator.ComparatorChain`, copied upstream from Apache Commons Collections.
 * `BitSet` is JVM-only, so the reverse flags are a [MutableList] of booleans; the chain is skipped once
 * locked, exactly like upstream.
 *
 * @hutool cn.hutool.core.comparator.ComparatorChain
 */
class ComparatorChain<E> : Chain<Comparator<E>, ComparatorChain<E>>, Comparator<E> {
    private val chain: MutableList<Comparator<E>>
    private val orderingBits: MutableList<Boolean>
    private var lock = false

    /** An empty chain; [compare] throws until a comparator is added. */
    constructor() : this(mutableListOf(), mutableListOf())

    /** A chain holding one forward comparator. */
    constructor(comparator: Comparator<E>) : this(comparator, false)

    /** A chain holding one comparator, optionally reversed. */
    constructor(comparator: Comparator<E>, reverse: Boolean) : this(mutableListOf(comparator), mutableListOf(reverse))

    /** A chain over an existing comparator list, all forward. */
    constructor(list: List<Comparator<E>>) : this(list.toMutableList(), MutableList(list.size) { false })

    private constructor(list: MutableList<Comparator<E>>, bits: MutableList<Boolean>) {
        chain = list
        orderingBits = bits
    }

    /** Appends a forward comparator. */
    fun addComparator(comparator: Comparator<E>): ComparatorChain<E> = addComparator(comparator, false)

    /** Appends a comparator, optionally reversed. */
    fun addComparator(comparator: Comparator<E>, reverse: Boolean): ComparatorChain<E> {
        checkLocked()
        chain.add(comparator)
        orderingBits.add(reverse)
        return this
    }

    /** Replaces the comparator at [index], keeping its direction. */
    fun setComparator(index: Int, comparator: Comparator<E>): ComparatorChain<E> = setComparator(index, comparator, false)

    /** Replaces the comparator at [index] and its direction. */
    fun setComparator(index: Int, comparator: Comparator<E>, reverse: Boolean): ComparatorChain<E> {
        checkLocked()
        chain[index] = comparator
        orderingBits[index] = reverse
        return this
    }

    /** Marks [index] as forward. */
    fun setForwardSort(index: Int): ComparatorChain<E> {
        checkLocked()
        orderingBits[index] = false
        return this
    }

    /** Marks [index] as reversed. */
    fun setReverseSort(index: Int): ComparatorChain<E> {
        checkLocked()
        orderingBits[index] = true
        return this
    }

    /** Number of comparators. */
    fun size(): Int = chain.size

    /** Whether the chain has been locked by a comparison. */
    fun isLocked(): Boolean = lock

    override fun iterator(): Iterator<Comparator<E>> = chain.iterator()

    override fun addChain(element: Comparator<E>): ComparatorChain<E> = addComparator(element)

    override fun compare(o1: E, o2: E): Int {
        if (!lock) {
            checkChainIntegrity()
            lock = true
        }
        for (index in chain.indices) {
            val result = chain[index].compare(o1, o2)
            if (result != 0) {
                return if (orderingBits[index]) -result else result
            }
        }
        return 0
    }

    override fun hashCode(): Int = chain.hashCode() xor orderingBits.hashCode()

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is ComparatorChain<*>) return false
        return orderingBits == other.orderingBits && chain == other.chain
    }

    private fun checkLocked() {
        if (lock) throw UnsupportedOperationException("Comparator ordering cannot be changed after the first comparison is performed")
    }

    private fun checkChainIntegrity() {
        if (chain.isEmpty()) throw UnsupportedOperationException("ComparatorChains must contain at least one Comparator")
    }
}

