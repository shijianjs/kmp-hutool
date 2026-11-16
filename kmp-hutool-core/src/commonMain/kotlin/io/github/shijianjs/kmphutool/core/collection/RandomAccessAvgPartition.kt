package io.github.shijianjs.kmphutool.core.collection

/**
 * [AvgPartition] over a list, mirroring Hutool's `RandomAccess` marker.
 *
 * Port of `cn.hutool.core.collection.RandomAccessAvgPartition`. Kotlin lists have no
 * `RandomAccess` marker interface, so this class exists for API parity.
 *
 * @hutool cn.hutool.core.collection.RandomAccessAvgPartition
 */
class RandomAccessAvgPartition<T>(list: List<T>, limit: Int) : AvgPartition<T>(list, limit)
