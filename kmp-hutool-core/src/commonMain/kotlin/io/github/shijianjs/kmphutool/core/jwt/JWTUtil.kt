package io.github.shijianjs.kmphutool.core.jwt

import io.github.shijianjs.kmphutool.core.jwt.signers.JWTSigner

/**
 * Static helpers for creating and verifying JWTs.
 *
 * Port of `cn.hutool.jwt.JWTUtil`. Only the `JWTSigner` overloads are kept: the byte-array
 * convenience overloads need Hutool's platform HMAC signer factory, so callers on common code pass
 * the signer they already have.
 *
 * @hutool cn.hutool.jwt.JWTUtil
 */
object JWTUtil {
    fun createToken(payload: Map<String, Any?>, signer: JWTSigner): String =
        createToken(null, payload, signer)

    fun createToken(headers: Map<String, Any?>?, payload: Map<String, Any?>, signer: JWTSigner): String =
        JWT.create()
            .also { jwt -> headers?.let { jwt.addHeaders(it) } }
            .addPayloads(payload)
            .setSigner(signer)
            .sign()

    fun parseToken(token: String): JWT = JWT.of(token)

    fun verify(token: String, signer: JWTSigner): Boolean = JWT.of(token).verify(signer)
}
