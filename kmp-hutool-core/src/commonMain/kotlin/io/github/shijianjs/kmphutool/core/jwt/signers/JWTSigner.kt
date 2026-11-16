package io.github.shijianjs.kmphutool.core.jwt.signers

/**
 * Computes and verifies the third segment of a JWT.
 *
 * Port of `cn.hutool.jwt.signers.JWTSigner`. Concrete HMAC/RSA signers stay outside common
 * code; [NoneJWTSigner] is the unsigned variant.
 *
 * @hutool cn.hutool.jwt.signers.JWTSigner
 */
interface JWTSigner {
    fun sign(headerBase64: String, payloadBase64: String): String

    fun verify(headerBase64: String, payloadBase64: String, signBase64: String): Boolean

    fun getAlgorithm(): String

    fun getAlgorithmId(): String = AlgorithmUtil.getId(getAlgorithm())
}
