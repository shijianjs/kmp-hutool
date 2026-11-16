package io.github.shijianjs.kmphutool.core.jwt

import kotlinx.datetime.Instant

/**
 * Registered JWT payload claims and their fluent setters.
 *
 * Port of `cn.hutool.jwt.RegisteredPayload`, with [Instant] replacing `java.util.Date`. Time claims
 * are stored as epoch seconds, as required by RFC 7519.
 *
 * @hutool cn.hutool.jwt.RegisteredPayload
 */
interface RegisteredPayload<T : RegisteredPayload<T>> {
    fun setPayload(name: String, value: Any?): T

    fun setIssuer(issuer: String): T = setPayload(ISSUER, issuer)

    fun setSubject(subject: String): T = setPayload(SUBJECT, subject)

    fun setAudience(vararg audience: String): T = setPayload(AUDIENCE, audience.toList())

    fun setExpiresAt(expiresAt: Instant): T = setPayload(EXPIRES_AT, expiresAt.epochSeconds)

    fun setNotBefore(notBefore: Instant): T = setPayload(NOT_BEFORE, notBefore.epochSeconds)

    fun setIssuedAt(issuedAt: Instant): T = setPayload(ISSUED_AT, issuedAt.epochSeconds)

    fun setJWTId(jwtId: String): T = setPayload(JWT_ID, jwtId)

    companion object {
        const val ISSUER = "iss"
        const val SUBJECT = "sub"
        const val AUDIENCE = "aud"
        const val EXPIRES_AT = "exp"
        const val NOT_BEFORE = "nbf"
        const val ISSUED_AT = "iat"
        const val JWT_ID = "jti"
    }
}
