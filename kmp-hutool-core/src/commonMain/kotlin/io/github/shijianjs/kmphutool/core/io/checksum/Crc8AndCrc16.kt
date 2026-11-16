package io.github.shijianjs.kmphutool.core.io.checksum

import io.github.shijianjs.kmphutool.core.io.checksum.crc16.CRC16Checksum
import io.github.shijianjs.kmphutool.core.io.checksum.crc16.CRC16IBM

/**
 * CRC-16 facade, IBM algorithm by default.
 *
 * Port of `cn.hutool.core.io.checksum.CRC16`; the `java.util.zip.Checksum` interface is replaced by
 * the same method names on [CRC16Checksum].
 *
 * @hutool cn.hutool.core.io.checksum.CRC16
 */
class CRC16(private val crc16: CRC16Checksum = CRC16IBM()) {
    /** The CRC value as hexadecimal. */
    fun getHexValue(): String = crc16.getHexValue()

    /** The CRC value as hexadecimal, zero-padded to four digits when [isPadding]. */
    fun getHexValue(isPadding: Boolean): String = crc16.getHexValue(isPadding)

    /** The current CRC value. */
    fun getValue(): Long = crc16.getValue()

    /** Resets the running value. */
    fun reset() = crc16.reset()

    /** Updates with `[off, off + len)` bytes of [bytes]. */
    fun update(bytes: ByteArray, off: Int, len: Int) = crc16.update(bytes, off, len)

    /** Updates with one byte. */
    fun update(b: Int) = crc16.update(b)
}

/**
 * CRC-8 with a caller-supplied polynomial and initial value.
 *
 * Port of `cn.hutool.core.io.checksum.CRC8`; the `java.util.zip.Checksum` interface is replaced by
 * the same method names on this class.
 *
 * @hutool cn.hutool.core.io.checksum.CRC8
 */
class CRC8(polynomial: Int, init: Short) {
    private val init: Short = init
    private val crcTable = ShortArray(256)
    private var value: Short = init

    init {
        for (dividend in 0 until 256) {
            var remainder = dividend
            repeat(8) {
                remainder = if ((remainder and 0x01) != 0) (remainder ushr 1) xor polynomial else remainder ushr 1
            }
            crcTable[dividend] = remainder.toShort()
        }
    }

    /** Updates with `[offset, offset + len)` bytes of [buffer]. */
    fun update(buffer: ByteArray, offset: Int, len: Int) {
        for (i in 0 until len) {
            val data = buffer[offset + i].toInt() xor value.toInt()
            value = (crcTable[data and 0xff].toInt() xor (value.toInt() shl 8)).toShort()
        }
    }

    /** Updates with the whole [buffer]. */
    fun update(buffer: ByteArray) = update(buffer, 0, buffer.size)

    /** Updates with one byte. */
    fun update(b: Int) = update(byteArrayOf(b.toByte()), 0, 1)

    /** The current CRC value, 0..255. */
    fun getValue(): Long = (value.toInt() and 0xff).toLong()

    /** Resets the running value to the initial value. */
    fun reset() {
        value = init
    }
}
