package io.github.shijianjs.kmphutool.core.collection

/**
 * Iterates over a Kotlin array with an optional `[start, end)` window.
 *
 * Port of `cn.hutool.core.collection.ArrayIter`. Upstream accepts any JVM array reflectively; in
 * Kotlin the element type is known, so primitive arrays use their own iterators instead.
 *
 * @hutool cn.hutool.core.collection.ArrayIter
 */
class ArrayIter<E>(
    private val array: Array<out E>,
    startIndex: Int = 0,
    endIndex: Int = -1,
) : Iterator<E> {
    private var endIndex: Int = array.size
    private var startIndex: Int = 0
    private var index: Int

    init {
        if (endIndex in 1 until array.size) this.endIndex = endIndex
        if (startIndex in 0 until this.endIndex) this.startIndex = startIndex
        index = this.startIndex
    }

    override fun hasNext(): Boolean = index < endIndex

    override fun next(): E {
        if (!hasNext()) throw NoSuchElementException()
        return array[index++]
    }
    /** The array being iterated. */
    fun getArray(): Array<out E> = array

    /** Rewinds to the start index. */
    fun reset() {
        index = startIndex
    }
}
