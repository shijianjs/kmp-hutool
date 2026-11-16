package io.github.shijianjs.kmphutool.core.jwt

/**
 * JWT payload claims.
 *
 * Port of `cn.hutool.jwt.JWTPayload`.
 *
 * @hutool cn.hutool.jwt.JWTPayload
 */
class JWTPayload : Claims(), RegisteredPayload<JWTPayload> {
    fun addPayloads(payloads: Map<String, *>): JWTPayload = apply { putAll(payloads) }

    override fun setPayload(name: String, value: Any?): JWTPayload = apply { setClaim(name, value) }
}
