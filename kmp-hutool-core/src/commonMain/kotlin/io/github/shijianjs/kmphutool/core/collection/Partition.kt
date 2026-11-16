package io.github.shijianjs.kmphutool.core.collection

/**
 * Splits a collection into fixed-size groups.
 *
 * Port of `cn.hutool.core.collection.Partition`. Hutool's class is an `Iterator` that yields one
 * group per `next()`; the port computes the groups up front ([partitions]) because a Kotlin
 * `List<List<T>>` is what call sites actually want, and keeps [size] for the page-size semantics.
 *
 * @hutool cn.hutool.core.collection.Partition
 */
open class Partition<T> constructor(
    /** The number of elements in each group, except possibly the last. */
    val size: Int,
    private val source: List<T>,
) {
    init {
        require(size > 0) { "Partition size must be greater than 0" }
    }

    /** The groups, in order. */
    fun partitions(): List<List<T>> {
        if (source.isEmpty()) return emptyList()
        val groups = mutableListOf<List<T>>()
        var index = 0
        while (index < source.size) {
            groups.add(source.subList(index, minOf(index + size, source.size)).toList())
            index += size
        }
        return groups
    }

    /** The number of groups. */
    fun count(): Int = if (source.isEmpty()) 0 else (source.size + size - 1) / size

    override fun toString(): String = "Partition(size=$size, count=${count()})"

    companion object {
        /** Groups [collection] into chunks of [size] elements. */
        fun <T> of(collection: Collection<T>?, size: Int): Partition<T> =
            Partition(size, collection?.toList() ?: emptyList())

        /** Groups [collection] into chunks of at most [size] elements. */
        fun <T> partitions(collection: Collection<T>?, size: Int): List<List<T>> = of(collection, size).partitions()
    }
}
