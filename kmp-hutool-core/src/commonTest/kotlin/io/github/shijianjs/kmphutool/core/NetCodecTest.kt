package io.github.shijianjs.kmphutool.core

import io.github.shijianjs.kmphutool.core.codec.PercentCodec
import io.github.shijianjs.kmphutool.core.net.FormUrlencoded
import io.github.shijianjs.kmphutool.core.net.MaskBit
import io.github.shijianjs.kmphutool.core.net.RFC3986
import io.github.shijianjs.kmphutool.core.net.URLDecoder
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class NetCodecTest {
    @Test
    fun percentEncodingRespectsSafeSets() {
        assertEquals("a%20b", PercentCodec.of(RFC3986.UNRESERVED).encode("a b"))
        assertEquals("a+b", FormUrlencoded.ALL.encode("a b"))
        assertEquals("%E4%BD%A0", PercentCodec.of(":").encode("你"))
        assertEquals("a%2Bb", RFC3986.UNRESERVED.encode("a+b"))
    }

    @Test
    fun maskBitMapsBothWays() {
        assertEquals("255.255.255.0", MaskBit.get(24))
        assertEquals(24, MaskBit.getMaskBit("255.255.255.0"))
        assertNull(MaskBit.getMaskBit("255.0.255.0"))
    }

    @Test
    fun decoderHandlesPlusAndPercent() {
        assertEquals("a b", URLDecoder.decode("a+b"))
        assertEquals("a+b", URLDecoder.decodeForPath("a%2Bb"))
        assertEquals("你好", URLDecoder.decode("%E4%BD%A0%E5%A5%BD"))
        // Malformed escapes pass through.
        assertEquals("%zz", URLDecoder.decode("%zz"))
    }
}

