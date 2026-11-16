package io.github.shijianjs.kmphutool.core.jwt

/**
 * JWT header claims (`alg`, `typ`, `cty`, `kid`).
 *
 * Port of `cn.hutool.jwt.JWTHeader`.
 *
 * @hutool cn.hutool.jwt.JWTHeader
 */
class JWTHeader : Claims() {
    fun setAlgorithm(algorithm: String): JWTHeader = apply { setClaim(ALGORITHM, algorithm) }

    fun setType(type: String): JWTHeader = apply { setClaim(TYPE, type) }

    fun setContentType(contentType: String): JWTHeader = apply { setClaim(CONTENT_TYPE, contentType) }

    fun setKeyId(keyId: String): JWTHeader = apply { setClaim(KEY_ID, keyId) }

    fun addHeaders(headers: Map<String, *>): JWTHeader = apply { putAll(headers) }

    companion object {
        const val ALGORITHM = "alg"
        const val TYPE = "typ"
        const val CONTENT_TYPE = "cty"
        const val KEY_ID = "kid"
    }
}
