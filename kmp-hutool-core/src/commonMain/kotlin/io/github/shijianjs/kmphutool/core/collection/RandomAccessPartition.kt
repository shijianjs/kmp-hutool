package io.github.shijianjs.kmphutool.core.collection

/**
 * A [Partition] over a list, mirroring Hutool's `RandomAccess` marker.
 *
 * Port of `cn.hutool.core.collection.RandomAccessPartition`. Kotlin lists have no `RandomAccess`
 * marker interface, so the class exists for API parity and behaves exactly like [Partition].
 *
 * @hutool cn.hutool.core.collection.RandomAccessPartition
 */
class RandomAccessPartition<T>(list: List<T>, size: Int) : Partition<T>(size, list)
