package io.github.shijianjs.kmphutool.core.codec

/**
 * Turns text back into a value.
 *
 * Port of `cn.hutool.core.codec.Decoder`.
 *
 * @hutool cn.hutool.core.codec.Decoder
 */
interface Decoder<T> {
    /** Decodes [data]; a malformed input throws the implementation's exception. */
    fun decode(data: String): T
}
