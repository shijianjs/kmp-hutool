package io.github.shijianjs.kmphutool.core.io.checksum.crc16

/**
 * Shared CRC-16 state and helpers.
 *
 * Port of `cn.hutool.core.io.checksum.crc16.CRC16Checksum`. Java implemented `java.util.zip.Checksum`;
 * Kotlin common code has no such interface, so the same four methods live directly on this class.
 *
 * @hutool cn.hutool.core.io.checksum.crc16.CRC16Checksum
 */
abstract class CRC16Checksum {
    /** Running CRC value; algorithms may keep it masked or unmasked. */
    protected var wCRCin: Int = 0

    init {
        reset()
    }

    /** The current CRC value. */
    open fun getValue(): Long = wCRCin.toLong()

    /** The CRC value as hexadecimal, without padding. */
    fun getHexValue(): String = getHexValue(false)

    /** The CRC value as hexadecimal, left-padded with zeros to four digits when [isPadding]. */
    fun getHexValue(isPadding: Boolean): String {
        val hex = getValue().toString(16)
        return if (isPadding) hex.padStart(4, '0') else hex
    }

    /** Resets the running value to zero. */
    open fun reset() {
        wCRCin = 0x0000
    }

    /** Updates the CRC with a whole buffer. */
    fun update(bytes: ByteArray) {
        update(bytes, 0, bytes.size)
    }

    /** Updates the CRC with `[off, off + len)` bytes. */
    open fun update(bytes: ByteArray, off: Int, len: Int) {
        for (i in off until off + len) update(bytes[i].toInt())
    }

    /** Updates the CRC with one byte. */
    open fun update(b: Int) {
        wCRCin = wCRCin xor (b and 0x00ff)
        repeat(8) {
            wCRCin = if ((wCRCin and 0x0001) != 0) (wCRCin ushr 1) xor 0xa001 else wCRCin ushr 1
        }
    }
}
