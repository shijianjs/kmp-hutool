package io.github.shijianjs.kmphutool.core.io

/**
 * A sink that discards every byte.
 *
 * Port of `cn.hutool.core.io.NullOutputStream`. `java.io.OutputStream` is JVM-only, so this class
 * exposes the `write` overloads directly and never throws.
 *
 * @hutool cn.hutool.core.io.NullOutputStream
 */
class NullOutputStream {
    fun write(b: Int) = Unit

    fun write(b: ByteArray) = Unit

    fun write(b: ByteArray, off: Int, len: Int) = Unit

    fun flush() = Unit

    fun close() = Unit
}
