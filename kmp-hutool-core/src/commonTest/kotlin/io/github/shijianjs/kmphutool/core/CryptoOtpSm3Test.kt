package io.github.shijianjs.kmphutool.core

import io.github.shijianjs.kmphutool.core.crypto.digest.SM3
import io.github.shijianjs.kmphutool.core.crypto.digest.otp.HOTP
import io.github.shijianjs.kmphutool.core.crypto.digest.otp.TOTP
import kotlinx.datetime.Instant
import kotlin.test.Test
import kotlin.test.assertEquals

class CryptoOtpSm3Test {
    @Test
    fun sm3MatchesKnownVector() {
        assertEquals(
            "66c7f0f462eeedd9d1f2d46bdc10e4e24167c4875cf2f7a2297da02b8f4ba8e0",
            SM3().digestHex("abc").lowercase(),
        )
        assertEquals(
            "debe9ff92275b8a138604889c18e5a4d6fdb70e5387e5765293dcba39c0c5732",
            SM3().digestHex("abcdabcdabcdabcdabcdabcdabcdabcdabcdabcdabcdabcdabcdabcdabcdabcd").lowercase(),
        )
    }

    @Test
    fun hotpMatchesRfc4226() {
        val key = "12345678901234567890".encodeToByteArray()
        val hotp = HOTP(key = key)
        assertEquals(755224, hotp.generate(0))
        assertEquals(287082, hotp.generate(1))
        assertEquals(359152, hotp.generate(2))
    }

    @Test
    fun totpMatchesRfc6238() {
        val key = "12345678901234567890".encodeToByteArray()
        val totp = TOTP(passwordLength = 8, key = key)
        assertEquals(94287082, totp.generate(Instant.parse("1970-01-01T00:00:59Z")))
        assertEquals(7081804, totp.generate(Instant.parse("2005-03-18T01:58:29Z")))
    }
}
