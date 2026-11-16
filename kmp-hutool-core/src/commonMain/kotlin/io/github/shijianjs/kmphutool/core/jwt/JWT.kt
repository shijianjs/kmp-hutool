package io.github.shijianjs.kmphutool.core.jwt

import io.github.shijianjs.kmphutool.core.codec.Base64
import io.github.shijianjs.kmphutool.core.json.JSONObject
import io.github.shijianjs.kmphutool.core.jwt.signers.AlgorithmUtil
import io.github.shijianjs.kmphutool.core.jwt.signers.JWTSigner
import io.github.shijianjs.kmphutool.core.jwt.signers.NoneJWTSigner
import kotlin.time.Clock
import kotlin.time.ExperimentalTime
import kotlinx.datetime.Instant

/**
 * JSON Web Token builder and parser.
 *
 * Port of `cn.hutool.jwt.JWT`. Signing is delegated to a [JWTSigner], because HMAC/RSA engines are
 * platform-specific (the same boundary Hutool draws with `JWTSignerUtil`).
 *
 * @hutool cn.hutool.jwt.JWT
 */
class JWT() : RegisteredPayload<JWT> {
    private val header = JWTHeader()
    private val payload = JWTPayload()
    private var signer: JWTSigner? = null
    private var tokens: List<String>? = null

    constructor(token: String) : this() {
        parse(token)
    }

    fun parse(token: String): JWT {
        if (token.isBlank()) throw JWTException("Token String must be not blank!")
        val parts = token.split('.', limit = 3)
        if (parts.size != 3) throw JWTException("The token was expected 3 parts, but got ${parts.size}.")
        tokens = parts
        header.parse(parts[0])
        payload.parse(parts[1])
        return this
    }

    fun setSigner(signer: JWTSigner): JWT {
        this.signer = signer
        val algorithm = header.getClaim(JWTHeader.ALGORITHM) as? String
        if (algorithm.isNullOrBlank()) header.setAlgorithm(AlgorithmUtil.getId(signer.getAlgorithm()))
        return this
    }

    fun getSigner(): JWTSigner? = signer

    fun getHeaders(): JSONObject = header.getClaimsJson()

    fun getHeader(): JWTHeader = header

    fun getHeader(name: String): Any? = header.getClaim(name)

    fun getAlgorithm(): String? = header.getClaim(JWTHeader.ALGORITHM) as? String

    fun setHeader(name: String, value: Any?): JWT = apply { header.getClaimsJson().put(name, value) }

    fun addHeaders(headers: Map<String, *>): JWT = apply { header.addHeaders(headers) }

    fun getPayloads(): JSONObject = payload.getClaimsJson()

    fun getPayload(): JWTPayload = payload

    fun getPayload(name: String): Any? = payload.getClaim(name)

    override fun setPayload(name: String, value: Any?): JWT = apply { payload.setPayload(name, value) }

    fun addPayloads(payloads: Map<String, *>): JWT = apply { payload.addPayloads(payloads) }

    fun sign(): String = sign(true)

    fun sign(addTypeIfNot: Boolean): String {
        val signer = signer ?: throw JWTException("No Signer provided!")
        return sign(signer, addTypeIfNot)
    }

    fun sign(signer: JWTSigner): String = sign(signer, true)

    fun sign(signer: JWTSigner, addTypeIfNot: Boolean): String {
        if (addTypeIfNot && (header.getClaim(JWTHeader.TYPE) as? String).isNullOrBlank()) {
            header.setType("JWT")
        }
        if (getAlgorithm().isNullOrBlank()) header.setAlgorithm(AlgorithmUtil.getId(signer.getAlgorithm()))

        val headerBase64 = Base64.encodeUrlSafe(header.toString())
        val payloadBase64 = Base64.encodeUrlSafe(payload.toString())
        return "$headerBase64.$payloadBase64.${signer.sign(headerBase64, payloadBase64)}"
    }

    fun verify(): Boolean = verify(signer)

    fun verify(signer: JWTSigner?): Boolean {
        var actual = signer ?: NoneJWTSigner.NONE
        val algorithm = getAlgorithm()
        if (NoneJWTSigner.isNone(algorithm) && actual !is NoneJWTSigner) {
            throw JWTException("Alg is 'none' but use a real signer!")
        }
        if (actual is NoneJWTSigner && !NoneJWTSigner.isNone(algorithm)) {
            throw JWTException("Alg is not 'none' but use NoneJWTSigner!")
        }
        val parts = tokens ?: throw JWTException("No token to verify!")
        return actual.verify(parts[0], parts[1], parts[2])
    }

    @OptIn(ExperimentalTime::class)
    fun validate(leeway: Long): Boolean {
        if (!verify()) return false
        return try {
            JWTValidator.of(this).validateDate(Clock.System.now(), leeway)
            true
        } catch (_: IllegalArgumentException) {
            false
        }
    }

    fun validate(now: Instant, leeway: Long): Boolean {
        if (!verify()) return false
        return try {
            JWTValidator.of(this).validateDate(now, leeway)
            true
        } catch (_: IllegalArgumentException) {
            false
        }
    }

    companion object {
        fun create(): JWT = JWT()

        fun of(token: String): JWT = JWT(token)
    }
}
