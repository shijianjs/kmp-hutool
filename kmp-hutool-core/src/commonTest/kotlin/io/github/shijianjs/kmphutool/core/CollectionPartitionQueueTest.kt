package io.github.shijianjs.kmphutool.core

import io.github.shijianjs.kmphutool.core.collection.AvgPartition
import io.github.shijianjs.kmphutool.core.collection.BoundedPriorityQueue
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class CollectionPartitionQueueTest {
    @Test
    fun avgPartitionSpreadsRemainder() {
        val list = listOf(1, 2, 3, 4, 5, 6, 7)
        val partition = AvgPartition(list, 3)
        assertEquals(3, partition.size())
        assertEquals(listOf(1, 2, 3), partition.get(0))
        assertEquals(listOf(4, 5), partition.get(1))
        assertEquals(listOf(6, 7), partition.get(2))
        assertEquals(7, partition.partitions().sumOf { it.size })
    }

    @Test
    fun boundedQueueKeepsOrderAndCapacity() {
        val queue = BoundedPriorityQueue<Int>(2)
        queue.offer(5)
        queue.offer(1)
        queue.offer(9)
        assertEquals(2, queue.size())
        assertTrue(queue.peek() != null)
        assertEquals(listOf(1, 5), queue.toList())
        assertEquals(5, queue.poll())
    }
}

