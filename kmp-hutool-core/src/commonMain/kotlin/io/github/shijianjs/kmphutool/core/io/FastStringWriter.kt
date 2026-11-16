package io.github.shijianjs.kmphutool.core.io

import io.github.shijianjs.kmphutool.core.text.StrBuilder

/**
 * A fast, non-thread-safe string writer backed by [StrBuilder].
 *
 * Port of `cn.hutool.core.io.FastStringWriter`. `java.io.Writer` does not exist in a common
 * source set, so this is a plain class with the same `write` overloads; `flush()` and `close()`
 * are no-ops exactly as upstream.
 *
 * @hutool cn.hutool.core.io.FastStringWriter
 */
class FastStringWriter(@Suppress("UNUSED_PARAMETER") initialSize: Int = 16) {
    private val builder = StrBuilder()

    fun write(c: Int) {
        builder.append(c.toChar())
    }

    fun write(str: String) {
        builder.append(str)
    }

    fun write(str: String, off: Int, len: Int) {
        builder.append(str, off, off + len)
    }

    fun write(cbuf: CharArray) {
        builder.append(cbuf.concatToString())
    }

    fun write(cbuf: CharArray, off: Int, len: Int) {
        if (off < 0 || off > cbuf.size || len < 0 || off + len > cbuf.size || off + len < 0) {
            throw IndexOutOfBoundsException()
        }
        if (len == 0) return
        builder.append(cbuf.concatToString(), off, off + len)
    }

    fun flush() = Unit

    fun close() = Unit

    override fun toString(): String = builder.toString()
}
