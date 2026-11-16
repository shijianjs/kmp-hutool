package io.github.shijianjs.kmphutool.core.codec

/**
 * Base62 convenience wrapper, UTF-8 text based.
 *
 * Port of `cn.hutool.core.codec.Base62`. Charset, `File` and `InputStream`/`OutputStream` overloads
 * are omitted with the rest of the I/O support; encode/decode the bytes yourself and call
 * [Base62Codec] when you need another charset.
 *
 * @hutool cn.hutool.core.codec.Base62
 */
object Base62 {
    /** Encodes text as Base62 (GMP alphabet). */
    fun encode(source: CharSequence): String =
        Base62Codec.INSTANCE.encode(source.toString().encodeToByteArray()).decodeToString()

    /** Encodes bytes as Base62 (GMP alphabet). */
    fun encode(source: ByteArray): String = Base62Codec.INSTANCE.encode(source).decodeToString()

    /** Encodes text as Base62 with the inverted alphabet. */
    fun encodeInverted(source: CharSequence): String = encodeInverted(source.toString().encodeToByteArray())

    /** Encodes bytes as Base62 with the inverted alphabet. */
    fun encodeInverted(source: ByteArray): String = Base62Codec.INSTANCE.encode(source, true).decodeToString()

    /** Decodes Base62 back to text. */
    fun decodeStr(source: CharSequence): String = decode(source).decodeToString()

    /** Decodes Base62 to bytes. */
    fun decode(base62Str: CharSequence): ByteArray =
        Base62Codec.INSTANCE.decode(base62Str.toString().encodeToByteArray())

    /** Decodes bytes containing Base62 text. */
    fun decode(base62Bytes: ByteArray): ByteArray = Base62Codec.INSTANCE.decode(base62Bytes)

    /** Decodes Base62 written with the inverted alphabet back to text. */
    fun decodeStrInverted(source: CharSequence): String = decodeInverted(source).decodeToString()

    /** Decodes Base62 written with the inverted alphabet to bytes. */
    fun decodeInverted(base62Str: CharSequence): ByteArray =
        Base62Codec.INSTANCE.decode(base62Str.toString().encodeToByteArray(), true)
}
