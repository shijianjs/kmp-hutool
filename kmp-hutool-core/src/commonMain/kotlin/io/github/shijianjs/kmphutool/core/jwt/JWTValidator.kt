package io.github.shijianjs.kmphutool.core.jwt

import io.github.shijianjs.kmphutool.core.exceptions.ValidateException
import io.github.shijianjs.kmphutool.core.jwt.signers.JWTSigner
import io.github.shijianjs.kmphutool.core.jwt.signers.NoneJWTSigner
import kotlin.time.Clock
import kotlin.time.ExperimentalTime
import kotlinx.datetime.Instant

/**
 * Validates a JWT's algorithm, signature and time claims.
 *
 * Port of `cn.hutool.jwt.JWTValidator`, with [Instant] replacing `java.util.Date`. Time claims are
 * read as epoch seconds.
 *
 * @hutool cn.hutool.jwt.JWTValidator
 */
class JWTValidator(private val jwt: JWT) {
    fun validateAlgorithm(): JWTValidator = validateAlgorithm(null)

    fun validateAlgorithm(signer: JWTSigner?): JWTValidator {
        val algorithmId = jwt.getAlgorithm()
        val actual = signer ?: jwt.getSigner()
        if (algorithmId.isNullOrEmpty()) {
            if (actual == null || actual is NoneJWTSigner) return this
            throw ValidateException("No algorithm defined in header!")
        }
        if (actual == null) throw IllegalArgumentException("No Signer for validate algorithm!")
        if (algorithmId != actual.getAlgorithmId()) {
            throw ValidateException(
                "Algorithm [$algorithmId] defined in header doesn't match to [${actual.getAlgorithmId()}]!",
            )
        }
        if (!jwt.verify(actual)) throw ValidateException("Signature verification failed!")
        return this
    }

    @OptIn(ExperimentalTime::class)
    fun validateDate(): JWTValidator = validateDate(Clock.System.now(), 0)

    fun validateDate(now: Instant): JWTValidator = validateDate(now, 0)

    fun validateDate(now: Instant, leeway: Long): JWTValidator {
        validateNotAfter(RegisteredPayload.NOT_BEFORE, jwt.getPayload(RegisteredPayload.NOT_BEFORE), now, leeway)
        validateNotBefore(RegisteredPayload.EXPIRES_AT, jwt.getPayload(RegisteredPayload.EXPIRES_AT), now, leeway)
        validateNotAfter(RegisteredPayload.ISSUED_AT, jwt.getPayload(RegisteredPayload.ISSUED_AT), now, leeway)
        return this
    }

    private fun validateNotAfter(name: String, value: Any?, now: Instant, leeway: Long) {
        val seconds = secondsOf(value) ?: return
        if (seconds > now.epochSeconds + leeway) {
            throw ValidateException("'$name':[$seconds] is after now:[${now.epochSeconds}]")
        }
    }

    private fun validateNotBefore(name: String, value: Any?, now: Instant, leeway: Long) {
        val seconds = secondsOf(value) ?: return
        if (seconds < now.epochSeconds - leeway) {
            throw ValidateException("'$name':[$seconds] is before now:[${now.epochSeconds}]")
        }
    }

    private fun secondsOf(value: Any?): Long? = when (value) {
        null -> null
        is Number -> value.toLong()
        else -> value.toString().toLongOrNull()
    }

    companion object {
        fun of(token: String): JWTValidator = JWTValidator(JWT.of(token))

        fun of(jwt: JWT): JWTValidator = JWTValidator(jwt)
    }
}
