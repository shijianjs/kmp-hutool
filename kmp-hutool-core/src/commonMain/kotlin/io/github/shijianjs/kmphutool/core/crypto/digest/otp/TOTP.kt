package io.github.shijianjs.kmphutool.core.crypto.digest.otp

import io.github.shijianjs.kmphutool.core.crypto.digest.HmacAlgorithm
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds
import kotlinx.datetime.Instant

/**
 * Time-based one-time passwords (RFC 6238).
 *
 * Port of `cn.hutool.crypto.digest.otp.TOTP`, with `kotlin.time.Duration` and
 * [kotlinx.datetime.Instant] replacing `java.time.Duration`/`Instant`.
 *
 * @hutool cn.hutool.crypto.digest.otp.TOTP
 */
class TOTP(
    private val timeStep: Duration = DEFAULT_TIME_STEP,
    passwordLength: Int = HOTP.DEFAULT_PASSWORD_LENGTH,
    algorithm: HmacAlgorithm = HOTP.HOTP_HMAC_ALGORITHM,
    key: ByteArray,
) : HOTP(passwordLength, algorithm, key) {

    fun generate(timestamp: Instant): Int =
        generate(timestamp.toEpochMilliseconds() / timeStep.inWholeMilliseconds)

    fun validate(timestamp: Instant, offsetSize: Int, code: Int): Boolean {
        if (offsetSize == 0) return generate(timestamp) == code
        for (i in -offsetSize..offsetSize) {
            if (generate(timestamp + timeStep * i) == code) return true
        }
        return false
    }

    fun getTimeStep(): Duration = timeStep

    companion object {
        val DEFAULT_TIME_STEP: Duration = 30.seconds

        fun generateGoogleSecretKey(account: String, numBytes: Int): String =
            "otpauth://totp/$account?secret=${generateSecretKey(numBytes)}"
    }
}
