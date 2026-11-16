package io.github.shijianjs.kmphutool.core.codec

/**
 * Turns a value into text.
 *
 * Port of `cn.hutool.core.codec.Encoder`. The Java interface is bound to one input type at
 * implementation time; Kotlin uses the same shape so a codec class can implement both directions.
 *
 * @hutool cn.hutool.core.codec.Encoder
 */
interface Encoder<T> {
    /** Encodes [data]; implementations decide whether `null` is legal. */
    fun encode(data: T): String
}
