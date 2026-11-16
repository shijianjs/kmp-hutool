package io.github.shijianjs.kmphutool.core

import io.github.shijianjs.kmphutool.core.codec.Morse
import io.github.shijianjs.kmphutool.core.net.Ipv4Util
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class Ipv4AndMorseTest {
    @Test
    fun convertsBetweenLongAndDotted() {
        assertEquals("192.168.1.1", Ipv4Util.longToIpv4(Ipv4Util.ipv4ToLong("192.168.1.1")))
        assertEquals(3232235777L, Ipv4Util.ipv4ToLong("192.168.1.1"))
        assertEquals(0L, Ipv4Util.ipv4ToLong("not-an-ip", 0L))
        assertFailsWith<IllegalArgumentException> { Ipv4Util.ipv4ToLong("256.0.0.1") }
    }

    @Test
    fun expandsMasksAndRanges() {
        assertEquals("192.168.1.0", Ipv4Util.getBeginIpStr("192.168.1.100", 24))
        assertEquals("192.168.1.255", Ipv4Util.getEndIpStr("192.168.1.100", 24))
        assertEquals(256, Ipv4Util.countByMaskBit(24, true))
        assertEquals(254, Ipv4Util.countByMaskBit(24, false))
        assertEquals(listOf("192.168.0.0", "192.168.0.1", "192.168.0.2", "192.168.0.3"), Ipv4Util.list("192.168.0.0/30", true))
        assertEquals(listOf("192.168.0.1", "192.168.0.2"), Ipv4Util.list("192.168.0.0/30", false))
        assertEquals(4, Ipv4Util.countByIpRange("192.168.0.0", "192.168.0.3"))
    }

    @Test
    fun detectsPrivateAddressesAndWildcards() {
        assertTrue(Ipv4Util.isInnerIP("10.1.1.1"))
        assertTrue(Ipv4Util.isInnerIP("127.0.0.1"))
        assertFalse(Ipv4Util.isInnerIP("192.169.1.1"))
        assertTrue(Ipv4Util.matches("192.168.*.1", "192.168.3.1"))
        assertFalse(Ipv4Util.matches("192.168.*.1", "192.168.3.2"))
        assertEquals(24, Ipv4Util.getMaskBitByMask("255.255.255.0"))
        assertTrue(Ipv4Util.isMaskValid("255.255.0.0"))
        assertFalse(Ipv4Util.isMaskValid("255.0.255.0"))
    }

    @Test
    fun morseRoundTrips() {
        val morse = Morse()
        assertEquals(".../---/.../", morse.encode("SOS"))
        assertEquals("SOS", morse.decode(".../---/.../"))
        assertEquals("HELLO WORLD", morse.decode(morse.encode("hello world")))
        assertEquals("中", morse.decode(morse.encode("中")))
        assertFailsWith<IllegalArgumentException> { morse.decode("abc") }
        assertEquals(".-./", Morse('.', '-', '/').encode("R"))
    }
}


