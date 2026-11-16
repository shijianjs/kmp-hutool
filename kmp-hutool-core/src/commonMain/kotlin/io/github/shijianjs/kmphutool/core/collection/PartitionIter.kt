package io.github.shijianjs.kmphutool.core.collection

/**
 * Batches an iterator into lists of at most `partitionSize` elements.
 *
 * Port of `cn.hutool.core.collection.PartitionIter`. The last batch may be smaller; paging large
 * API payloads or `IN (...)` clauses is the usual use case.
 *
 * @hutool cn.hutool.core.collection.PartitionIter
 */
class PartitionIter<T>(
    private val iterator: Iterator<T>,
    private val partitionSize: Int,
) : Iterator<List<T>> {
    init {
        require(partitionSize > 0) { "partitionSize must be positive" }
    }

    override fun hasNext(): Boolean = iterator.hasNext()

    override fun next(): List<T> {
        val list = ArrayList<T>(partitionSize)
        for (i in 0 until partitionSize) {
            if (!iterator.hasNext()) break
            list.add(iterator.next())
        }
        return list
    }
}
