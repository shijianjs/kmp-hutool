package io.github.shijianjs.kmphutool.core.codec

/**
 * Base58 helpers.
 *
 * Port of `cn.hutool.core.codec.Base58`. The Bitcoin-style `encodeChecked`/`decodeChecked` variant
 * needs SHA-256, which `commonMain` has no portable implementation of, so only the plain codec is
 * exposed here; compute a checksum with your platform's crypto provider and use [Base58Codec].
 *
 * @hutool cn.hutool.core.codec.Base58
 */
object Base58 {
    /** Encodes bytes without a version byte or checksum. */
    fun encode(data: ByteArray): String = Base58Codec.INSTANCE.encode(data)

    /** Decodes a plain Base58 string. */
    fun decode(encoded: CharSequence): ByteArray = Base58Codec.INSTANCE.decode(encoded)
}
