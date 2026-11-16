package io.github.shijianjs.kmphutool.core.io

import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min

/**
 * A growable byte buffer that appends into chunks instead of reallocating one array.
 *
 * Port of `cn.hutool.core.io.FastByteBuffer`.
 *
 * @hutool cn.hutool.core.io.FastByteBuffer
 */
class FastByteBuffer(size: Int = 1024) {
    private var buffers = arrayOfNulls<ByteArray>(16)
    private var buffersCount = 0
    private var currentBufferIndex = -1
    private var currentBuffer: ByteArray? = null
    private var offset = 0
    private var byteSize = 0
    private val minChunkLen: Int = if (size <= 0) 1024 else abs(size)

    private fun needNewBuffer(newSize: Int) {
        val delta = newSize - byteSize
        val newBufferSize = max(minChunkLen, delta)
        currentBufferIndex++
        currentBuffer = ByteArray(newBufferSize)
        offset = 0
        if (currentBufferIndex >= buffers.size) {
            val newBuffers = arrayOfNulls<ByteArray>(buffers.size shl 1)
            buffers.copyInto(newBuffers, 0, 0, buffers.size)
            buffers = newBuffers
        }
        buffers[currentBufferIndex] = currentBuffer
        buffersCount++
    }

    /** Appends bytes `[off, off+len)` of [array]. */
    fun append(array: ByteArray, off: Int = 0, len: Int = array.size): FastByteBuffer {
        val end = off + len
        if (off < 0 || len < 0 || end > array.size) throw IndexOutOfBoundsException()
        if (len == 0) return this
        val newSize = byteSize + len
        var remaining = len

        val buffer = currentBuffer
        if (buffer != null) {
            val part = min(remaining, buffer.size - offset)
            array.copyInto(buffer, offset, end - remaining, end - remaining + part)
            remaining -= part
            offset += part
            byteSize += part
        }

        if (remaining > 0) {
            needNewBuffer(newSize)
            val part = min(remaining, currentBuffer!!.size - offset)
            array.copyInto(currentBuffer!!, offset, end - remaining, end - remaining + part)
            offset += part
            byteSize += part
        }
        return this
    }

    /** Appends [element]. */
    fun append(element: Byte): FastByteBuffer {
        val buffer = currentBuffer
        if (buffer == null || offset == buffer.size) needNewBuffer(byteSize + 1)
        currentBuffer!![offset] = element
        offset++
        byteSize++
        return this
    }

    /** Appends the whole of [buff]. */
    fun append(buff: FastByteBuffer): FastByteBuffer {
        if (buff.byteSize == 0) return this
        for (i in 0 until buff.currentBufferIndex) append(buff.buffers[i]!!)
        append(buff.currentBuffer!!, 0, buff.offset)
        return this
    }

    fun size(): Int = byteSize

    fun isEmpty(): Boolean = byteSize == 0

    fun index(): Int = currentBufferIndex

    fun offset(): Int = offset

    fun array(index: Int): ByteArray = buffers[index]!!

    fun reset() {
        byteSize = 0
        offset = 0
        currentBufferIndex = -1
        currentBuffer = null
        buffersCount = 0
    }

    /** All buffered bytes. */
    fun toArray(): ByteArray {
        val array = ByteArray(byteSize)
        if (currentBufferIndex == -1) return array
        var pos = 0
        for (i in 0 until currentBufferIndex) {
            val len = buffers[i]!!.size
            buffers[i]!!.copyInto(array, pos)
            pos += len
        }
        currentBuffer!!.copyInto(array, pos, 0, offset)
        return array
    }

    /** [len] bytes starting at logical [start]. */
    fun toArray(startIn: Int, len: Int): ByteArray {
        val array = ByteArray(len)
        if (len == 0) return array
        var start = startIn
        var i = 0
        while (start >= buffers[i]!!.size) {
            start -= buffers[i]!!.size
            i++
        }
        var remaining = len
        var pos = 0
        while (i < buffersCount) {
            val buf = buffers[i]!!
            val c = min(buf.size - start, remaining)
            buf.copyInto(array, pos, start, start + c)
            pos += c
            remaining -= c
            if (remaining == 0) break
            start = 0
            i++
        }
        return array
    }

    /** Byte at logical [index]. */
    fun get(indexIn: Int): Byte {
        if (indexIn >= byteSize || indexIn < 0) throw IndexOutOfBoundsException()
        var index = indexIn
        var ndx = 0
        while (true) {
            val b = buffers[ndx]!!
            if (index < b.size) return b[index]
            ndx++
            index -= b.size
        }
    }
}
