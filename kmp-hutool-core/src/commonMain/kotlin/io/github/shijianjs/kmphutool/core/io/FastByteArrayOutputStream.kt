package io.github.shijianjs.kmphutool.core.io

/**
 * A byte sink backed by [FastByteBuffer] that grows by adding chunks.
 *
 * Port of `cn.hutool.core.io.FastByteArrayOutputStream`. `java.io.OutputStream` and `Charset` do
 * not exist in common code, so this is a plain writer: use [write], [toByteArray] and [toString].
 * [toString] decodes UTF-8; other charset names are rejected.
 *
 * @hutool cn.hutool.core.io.FastByteArrayOutputStream
 */
class FastByteArrayOutputStream(size: Int = 1024) {
    private val buffer = FastByteBuffer(size)

    fun write(b: ByteArray, off: Int, len: Int) {
        buffer.append(b, off, len)
    }

    fun write(b: Int) {
        buffer.append(b.toByte())
    }

    fun size(): Int = buffer.size()

    /** No-op, matching upstream. */
    fun close() = Unit

    fun reset() {
        buffer.reset()
    }

    fun toByteArray(): ByteArray = buffer.toArray()

    override fun toString(): String = toString("UTF-8")

    /**
     * Decodes the buffer. Only `UTF-8` (case-insensitive) is supported in common code; UTF-16 and
     * platform charsets need a target-specific implementation.
     */
    fun toString(charsetName: String): String {
        require(charsetName.equals("UTF-8", ignoreCase = true) || charsetName.equals("UTF8", ignoreCase = true)) {
            "Only UTF-8 is portable in common code, got: $charsetName"
        }
        return toByteArray().decodeToString()
    }
}
