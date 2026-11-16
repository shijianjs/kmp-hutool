package io.github.shijianjs.kmphutool.core.collection

/**
 * Splits a list into `limit` groups whose sizes differ by at most one.
 *
 * Port of `cn.hutool.core.collection.AvgPartition`. Kotlin has no `RandomAccess` marker, so the
 * separate `RandomAccessPartition`/`RandomAccessAvgPartition` variants are unnecessary and folded in.
 * Groups are views over the source list, so later changes to the source show through.
 *
 * @hutool cn.hutool.core.collection.AvgPartition
 */
class AvgPartition<T>(private val list: List<T>, private val limit: Int) {
    private val size: Int = list.size / (if (limit <= 0) 1 else limit)
    private val remainder: Int = if (limit <= 0) 0 else list.size % limit

    init {
        require(limit > 0) { "Partition limit must be > 0" }
    }

    /** The group at [index]; may be empty once the list runs out. */
    fun get(index: Int): List<T> {
        var start = index * size + minOf(index, remainder)
        var end = start + size
        if (index + 1 <= remainder) end += 1
        return list.subList(start, minOf(end, list.size))
    }

    /** Always [limit]: trailing groups can be empty. */
    fun size(): Int = limit

    /** All groups in order. */
    fun partitions(): List<List<T>> = (0 until limit).map { get(it) }

    override fun toString(): String = "AvgPartition(limit=$limit)"
}
