package io.github.shijianjs.kmphutool.core.codec

/**
 * Convenience entry points for Base85: UTF-8 strings and byte arrays, standard or Z85.
 *
 * Port of `cn.hutool.core.codec.Base85`. The Java overloads take `Charset`, `File` and `InputStream`,
 * none of which belong in `commonMain`; text is always encoded as UTF-8 here, and callers that already
 * have bytes use the `ByteArray` overloads directly.
 *
 * @hutool cn.hutool.core.codec.Base85
 */
object Base85 {
    /** Encodes [source] as UTF-8, standard alphabet. */
    fun encode(source: CharSequence): String = encode(source.toString().encodeToByteArray())

    /** Encodes [source], standard alphabet. */
    fun encode(source: ByteArray): String = Base85Codec.INSTANCE.encode(source).decodeToString()

    /** Encodes [source] as UTF-8 with the Z85 alphabet. */
    fun encodeZ85(source: CharSequence): String = encodeZ85(source.toString().encodeToByteArray())

    /** Encodes [source] with the Z85 alphabet. */
    fun encodeZ85(source: ByteArray): String = Base85Codec.INSTANCE.encode(source, Z85 = true).decodeToString()

    /** Decodes standard Base85 text into a UTF-8 string. */
    fun decodeStr(source: CharSequence): String = decode(source).decodeToString()

    /** Decodes standard Base85 text. */
    fun decode(source: CharSequence): ByteArray = decode(source.toString().encodeToByteArray())

    /** Decodes standard Base85 bytes. */
    fun decode(source: ByteArray): ByteArray = Base85Codec.INSTANCE.decode(source)

    /** Decodes Z85 text into a UTF-8 string. */
    fun decodeStrZ85(source: CharSequence): String = decodeZ85(source).decodeToString()

    /** Decodes Z85 text. */
    fun decodeZ85(source: CharSequence): ByteArray = decodeZ85(source.toString().encodeToByteArray())

    /** Decodes Z85 bytes. */
    fun decodeZ85(source: ByteArray): ByteArray = Base85Codec.INSTANCE.decode(source, Z85 = true)
}

