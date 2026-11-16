package io.github.shijianjs.kmphutool.core.crypto.digest.otp

import io.github.shijianjs.kmphutool.core.codec.Base32
import io.github.shijianjs.kmphutool.core.crypto.CryptoException
import io.github.shijianjs.kmphutool.core.crypto.digest.HmacAlgorithm
import io.github.shijianjs.kmphutool.core.util.RandomUtil

/**
 * HMAC-based one-time passwords (RFC 4226).
 *
 * Port of `cn.hutool.crypto.digest.otp.HOTP`. Only HMAC-SHA1 is built in (the RFC 4226 default);
 * other HMAC algorithms need a platform crypto provider.
 *
 * @hutool cn.hutool.crypto.digest.otp.HOTP
 */
open class HOTP(
    passwordLength: Int = DEFAULT_PASSWORD_LENGTH,
    algorithm: HmacAlgorithm = HOTP_HMAC_ALGORITHM,
    private val key: ByteArray,
) {
    private val passwordLength: Int = passwordLength
    private val modDivisor: Int
    private val buffer = ByteArray(8)

    init {
        if (passwordLength >= MOD_DIVISORS.size) {
            throw IllegalArgumentException("Password length must be < ${MOD_DIVISORS.size}")
        }
        if (algorithm != HmacAlgorithm.HmacSHA1) {
            throw CryptoException("HOTP currently supports HmacSHA1 in common code")
        }
        this.modDivisor = MOD_DIVISORS[passwordLength]
    }

    open fun generate(counter: Long): Int {
        for (i in 0 until 8) {
            buffer[7 - i] = (counter ushr (8 * i)).toByte()
        }
        return truncate(HmacSha1.digest(key, buffer))
    }

    fun getPasswordLength(): Int = passwordLength

    fun getAlgorithm(): String = HOTP_HMAC_ALGORITHM.value

    private fun truncate(digest: ByteArray): Int {
        val offset = digest[digest.size - 1].toInt() and 0x0f
        val binary = ((digest[offset].toInt() and 0x7f) shl 24) or
            ((digest[offset + 1].toInt() and 0xff) shl 16) or
            ((digest[offset + 2].toInt() and 0xff) shl 8) or
            (digest[offset + 3].toInt() and 0xff)
        return binary % modDivisor
    }

    companion object {
        private val MOD_DIVISORS = intArrayOf(1, 10, 100, 1000, 10000, 100000, 1000000, 10000000, 100000000)

        const val DEFAULT_PASSWORD_LENGTH = 6

        val HOTP_HMAC_ALGORITHM: HmacAlgorithm = HmacAlgorithm.HmacSHA1

        fun generateSecretKey(numBytes: Int): String =
            Base32.encode(RandomUtil.randomBytes(numBytes))
    }
}
