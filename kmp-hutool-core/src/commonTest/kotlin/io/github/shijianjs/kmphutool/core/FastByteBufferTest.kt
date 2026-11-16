package io.github.shijianjs.kmphutool.core

import io.github.shijianjs.kmphutool.core.io.FastByteBuffer
import io.github.shijianjs.kmphutool.core.io.IORuntimeException
import kotlin.test.Test
import kotlin.test.assertContentEquals
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class FastByteBufferTest {
    @Test
    fun appendsAcrossChunks() {
        val buffer = FastByteBuffer(2)
        buffer.append(byteArrayOf(1, 2, 3))
        buffer.append(4)
        buffer.append(byteArrayOf(5, 6))
        assertEquals(6, buffer.size())
        assertContentEquals(byteArrayOf(1, 2, 3, 4, 5, 6), buffer.toArray())
        assertContentEquals(byteArrayOf(3, 4), buffer.toArray(2, 2))
        assertEquals(4.toByte(), buffer.get(3))
        buffer.reset()
        assertEquals(0, buffer.size())
    }

    @Test
    fun wrapsIoFailures() {
        assertEquals("boom", assertFailsWith<IORuntimeException> { throw IORuntimeException("boom") }.message)
    }
}
