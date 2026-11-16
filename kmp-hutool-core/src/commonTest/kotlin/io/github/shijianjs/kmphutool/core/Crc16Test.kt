package io.github.shijianjs.kmphutool.core

import io.github.shijianjs.kmphutool.core.io.checksum.CRC16
import io.github.shijianjs.kmphutool.core.io.checksum.crc16.CRC16CCITTFalse
import io.github.shijianjs.kmphutool.core.io.checksum.crc16.CRC16DNP
import io.github.shijianjs.kmphutool.core.io.checksum.crc16.CRC16IBM
import io.github.shijianjs.kmphutool.core.io.checksum.crc16.CRC16Maxim
import io.github.shijianjs.kmphutool.core.io.checksum.crc16.CRC16Modbus
import io.github.shijianjs.kmphutool.core.io.checksum.crc16.CRC16USB
import io.github.shijianjs.kmphutool.core.io.checksum.crc16.CRC16X25
import io.github.shijianjs.kmphutool.core.io.checksum.crc16.CRC16XModem
import kotlin.test.Test
import kotlin.test.assertEquals

class Crc16Test {
    private val check = "123456789".encodeToByteArray()

    private fun crc(checksum: io.github.shijianjs.kmphutool.core.io.checksum.crc16.CRC16Checksum): Long {
        checksum.update(check)
        return checksum.getValue()
    }

    @Test
    fun matchesKnownCheckValues() {
        assertEquals(0xBB3DL, crc(CRC16IBM()))
        assertEquals(0x4B37L, crc(CRC16Modbus()))
        assertEquals(0x31C3L, crc(CRC16XModem()))
        assertEquals(0x29B1L, crc(CRC16CCITTFalse()))
        assertEquals(0x906EL, crc(CRC16X25()))
        assertEquals(0xB4C8L, crc(CRC16USB()))
        assertEquals(0x44C2L, crc(CRC16Maxim()))
        assertEquals(0xEA82L, crc(CRC16DNP()))
    }

    @Test
    fun facadeHexLooksLikeCrc16() {
        val crc16 = CRC16(CRC16Modbus())
        crc16.update(check, 0, check.size)
        assertEquals("4b37", crc16.getHexValue())
    }
}
