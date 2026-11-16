package io.github.shijianjs.kmphutool.core

import io.github.shijianjs.kmphutool.core.collection.RandomAccessAvgPartition
import io.github.shijianjs.kmphutool.core.collection.RandomAccessPartition
import kotlin.test.Test
import kotlin.test.assertEquals

class RandomAccessPartitionTest {
    @Test
    fun pagesAList() {
        val partition = RandomAccessPartition(listOf(1, 2, 3, 4, 5), 2)
        assertEquals(listOf(listOf(1, 2), listOf(3, 4), listOf(5)), partition.partitions())
        assertEquals(3, partition.count())
    }

    @Test
    fun spreadsRemainderOverFirstChunks() {
        val partition = RandomAccessAvgPartition(listOf(1, 2, 3, 4, 5), 3)
        assertEquals(listOf(listOf(1, 2), listOf(3, 4), listOf(5)), partition.partitions())
        assertEquals(3, partition.size())
    }
}
