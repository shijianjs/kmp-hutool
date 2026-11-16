package io.github.shijianjs.kmphutool.core.codec

/**
 * Convenience facade for [Base32Codec]: RFC 4648 Base32 over UTF-8 bytes.
 *
 * Port of `cn.hutool.core.codec.Base32`. Upstream methods take a `java.nio.charset.Charset`; this
 * port always uses UTF-8 so the API stays free of platform charsets.
 *
 * @hutool cn.hutool.core.codec.Base32
 */
object Base32 {
    /** Encodes [bytes] with the standard alphabet. */
    fun encode(bytes: ByteArray): String = Base32Codec.INSTANCE.encode(bytes)

    /** Encodes the UTF-8 bytes of [source]. */
    fun encode(source: String): String = encode(source.encodeToByteArray())

    /** Encodes [bytes] with the "extended hex" alphabet. */
    fun encodeHex(bytes: ByteArray): String = Base32Codec.INSTANCE.encode(bytes, true)

    /** Encodes the UTF-8 bytes of [source] with the "extended hex" alphabet. */
    fun encodeHex(source: String): String = encodeHex(source.encodeToByteArray())

    /** Decodes [base32] with the standard alphabet. */
    fun decode(base32: String): ByteArray = Base32Codec.INSTANCE.decode(base32)

    /** Decodes [source] and returns the UTF-8 text it represents. */
    fun decodeStr(source: String): String = decode(source).decodeToString()

    /** Decodes [base32] with the "extended hex" alphabet. */
    fun decodeHex(base32: String): ByteArray = Base32Codec.INSTANCE.decode(base32, true)

    /** Decodes [source] with the "extended hex" alphabet and returns the UTF-8 text. */
    fun decodeStrHex(source: String): String = decodeHex(source).decodeToString()
}
