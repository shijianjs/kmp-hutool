package io.github.shijianjs.kmphutool.core

import io.github.shijianjs.kmphutool.core.codec.PunyCode
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class PunyCodeTest {
    @Test
    fun leavesAsciiUntouched() {
        assertEquals("example.com", PunyCode.encodeDomain("example.com"))
        assertEquals("example.com", PunyCode.decodeDomain("example.com"))
        assertEquals("abc", PunyCode.encode("abc"))
    }

    @Test
    fun roundTripsInternationalLabels() {
        val encoded = PunyCode.encodeDomain("中国.公司")
        assertTrue(encoded.startsWith("xn--"))
        assertTrue(encoded.contains(".xn--"))
        assertEquals("中国.公司", PunyCode.decodeDomain(encoded))
        assertEquals("中国", PunyCode.decode(PunyCode.encode("中国", withPrefix = true)))
    }
}

