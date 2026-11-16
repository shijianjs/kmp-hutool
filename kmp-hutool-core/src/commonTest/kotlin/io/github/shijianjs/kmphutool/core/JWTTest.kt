package io.github.shijianjs.kmphutool.core

import io.github.shijianjs.kmphutool.core.jwt.JWT
import io.github.shijianjs.kmphutool.core.jwt.JWTHeader
import io.github.shijianjs.kmphutool.core.jwt.JWTPayload
import io.github.shijianjs.kmphutool.core.jwt.JWTUtil
import io.github.shijianjs.kmphutool.core.jwt.JWTValidator
import io.github.shijianjs.kmphutool.core.jwt.RegisteredPayload
import io.github.shijianjs.kmphutool.core.jwt.signers.JWTSigner
import kotlinx.datetime.Instant
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class JWTTest {
    private val signer = object : JWTSigner {
        override fun sign(headerBase64: String, payloadBase64: String): String = "sig"
        override fun verify(headerBase64: String, payloadBase64: String, signBase64: String): Boolean =
            signBase64 == "sig"
        override fun getAlgorithm(): String = "HmacSHA256"
    }

    @Test
    fun createsParsesAndVerifiesToken() {
        val token = JWT.create()
            .setHeader("cty", "json")
            .setPayload("name", "Ada")
            .setSigner(signer)
            .sign()
        assertEquals(3, token.split('.').size)

        val parsed = JWT.of(token)
        assertEquals("Ada", parsed.getPayload("name"))
        assertEquals("HS256", parsed.getAlgorithm())
        assertTrue(parsed.verify(signer))
        assertFalse(parsed.verify(object : JWTSigner {
            override fun sign(headerBase64: String, payloadBase64: String): String = "other"
            override fun verify(headerBase64: String, payloadBase64: String, signBase64: String): Boolean = false
            override fun getAlgorithm(): String = "HmacSHA256"
        }))
    }

    @Test
    fun signsWithHeaderAndPayloadMaps() {
        val token = JWTUtil.createToken(
            mapOf(JWTHeader.TYPE to "JWT", JWTHeader.KEY_ID to "k1"),
            mapOf("sub" to "u1"),
            signer,
        )
        val parsed = JWTUtil.parseToken(token)
        assertEquals("u1", parsed.getPayload(RegisteredPayload.SUBJECT))
        assertTrue(JWTUtil.verify(token, signer))
    }

    @Test
    fun validatesTimeWindow() {
        val now = Instant.parse("2026-11-17T00:00:00Z")
        val jwt = JWT.create()
            .setPayload(RegisteredPayload.EXPIRES_AT, now.epochSeconds + 60)
            .setSigner(signer)
        val token = jwt.sign()

        JWTValidator.of(token).validateDate(now)
        val expired = JWT.create()
            .setPayload(RegisteredPayload.EXPIRES_AT, now.epochSeconds - 1)
            .setSigner(signer)
            .sign()
        assertFalse(runCatching { JWTValidator.of(expired).validateDate(now) }.isSuccess)
    }
}
