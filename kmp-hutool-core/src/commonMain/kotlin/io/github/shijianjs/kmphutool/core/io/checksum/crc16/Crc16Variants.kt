package io.github.shijianjs.kmphutool.core.io.checksum.crc16

/**
 * CRC16_ANSI: init `0xffff`, reflected polynomial `0xa001`.
 *
 * @hutool cn.hutool.core.io.checksum.crc16.CRC16Ansi
 */
class CRC16Ansi : CRC16Checksum() {
    override fun reset() {
        wCRCin = 0xffff
    }

    override fun update(b: Int) {
        var hi = wCRCin ushr 8
        hi = hi xor b
        wCRCin = hi
        repeat(8) {
            val flag = wCRCin and 0x0001
            wCRCin = wCRCin ushr 1
            if (flag == 1) wCRCin = wCRCin xor 0xa001
        }
    }
}

/**
 * CRC16_CCITT: init `0x0000`, reflected polynomial `0x8408`.
 *
 * @hutool cn.hutool.core.io.checksum.crc16.CRC16CCITT
 */
class CRC16CCITT : CRC16Checksum() {
    override fun update(b: Int) {
        wCRCin = wCRCin xor (b and 0x00ff)
        repeat(8) {
            wCRCin = if ((wCRCin and 0x0001) != 0) (wCRCin ushr 1) xor 0x8408 else wCRCin ushr 1
        }
    }
}

/**
 * CRC16_CCITT_FALSE: init `0xffff`, polynomial `0x1021`, MSB first.
 *
 * @hutool cn.hutool.core.io.checksum.crc16.CRC16CCITTFalse
 */
class CRC16CCITTFalse : CRC16Checksum() {
    override fun reset() {
        wCRCin = 0xffff
    }

    override fun update(bytes: ByteArray, off: Int, len: Int) {
        super.update(bytes, off, len)
        wCRCin = wCRCin and 0xffff
    }

    override fun update(b: Int) {
        repeat(8) { i ->
            val bit = (b ushr (7 - i)) and 1 == 1
            val c15 = (wCRCin ushr 15) and 1 == 1
            wCRCin = wCRCin shl 1
            if (c15 xor bit) wCRCin = wCRCin xor 0x1021
        }
        wCRCin = wCRCin and 0xffff
    }
}

/**
 * CRC16_DNP: init `0x0000`, reflected polynomial `0xa6bc`, result XOR `0xffff`.
 *
 * @hutool cn.hutool.core.io.checksum.crc16.CRC16DNP
 */
class CRC16DNP : CRC16Checksum() {
    override fun getValue(): Long = ((wCRCin xor 0xffff) and 0xFFFF).toLong()

    override fun update(b: Int) {
        wCRCin = wCRCin xor (b and 0x00ff)
        repeat(8) {
            wCRCin = if ((wCRCin and 0x0001) != 0) (wCRCin ushr 1) xor 0xa6bc else wCRCin ushr 1
        }
    }
}

/**
 * CRC16_IBM: init `0x0000`, reflected polynomial `0xa001`.
 *
 * @hutool cn.hutool.core.io.checksum.crc16.CRC16IBM
 */
class CRC16IBM : CRC16Checksum()

/**
 * CRC16_MAXIM: init `0x0000`, reflected polynomial `0xa001`, result XOR `0xffff`.
 *
 * @hutool cn.hutool.core.io.checksum.crc16.CRC16Maxim
 */
class CRC16Maxim : CRC16Checksum() {
    override fun getValue(): Long = ((wCRCin xor 0xffff) and 0xFFFF).toLong()

    override fun update(b: Int) {
        wCRCin = wCRCin xor (b and 0x00ff)
        repeat(8) {
            wCRCin = if ((wCRCin and 0x0001) != 0) (wCRCin ushr 1) xor 0xa001 else wCRCin ushr 1
        }
    }
}

/**
 * CRC16_MODBUS: init `0xffff`, reflected polynomial `0xa001`.
 *
 * @hutool cn.hutool.core.io.checksum.crc16.CRC16Modbus
 */
class CRC16Modbus : CRC16Checksum() {
    override fun reset() {
        wCRCin = 0xffff
    }
}

/**
 * CRC16_USB: init `0xffff`, reflected polynomial `0xa001`, result XOR `0xffff`.
 *
 * @hutool cn.hutool.core.io.checksum.crc16.CRC16USB
 */
class CRC16USB : CRC16Checksum() {
    override fun reset() {
        wCRCin = 0xFFFF
    }

    override fun getValue(): Long = ((wCRCin xor 0xffff) and 0xFFFF).toLong()

    override fun update(b: Int) {
        wCRCin = wCRCin xor (b and 0x00ff)
        repeat(8) {
            wCRCin = if ((wCRCin and 0x0001) != 0) (wCRCin ushr 1) xor 0xa001 else wCRCin ushr 1
        }
    }
}

/**
 * CRC16_X25: init `0xffff`, reflected polynomial `0x8408`, result XOR `0xffff`.
 *
 * @hutool cn.hutool.core.io.checksum.crc16.CRC16X25
 */
class CRC16X25 : CRC16Checksum() {
    override fun reset() {
        wCRCin = 0xffff
    }

    override fun getValue(): Long = ((wCRCin xor 0xffff) and 0xFFFF).toLong()

    override fun update(b: Int) {
        wCRCin = wCRCin xor (b and 0x00ff)
        repeat(8) {
            wCRCin = if ((wCRCin and 0x0001) != 0) (wCRCin ushr 1) xor 0x8408 else wCRCin ushr 1
        }
    }
}

/**
 * CRC16_XMODEM: init `0x0000`, polynomial `0x1021`, MSB first.
 *
 * @hutool cn.hutool.core.io.checksum.crc16.CRC16XModem
 */
class CRC16XModem : CRC16Checksum() {
    override fun update(bytes: ByteArray, off: Int, len: Int) {
        super.update(bytes, off, len)
        wCRCin = wCRCin and 0xffff
    }

    override fun update(b: Int) {
        repeat(8) { i ->
            val bit = (b ushr (7 - i)) and 1 == 1
            val c15 = (wCRCin ushr 15) and 1 == 1
            wCRCin = wCRCin shl 1
            if (c15 xor bit) wCRCin = wCRCin xor 0x1021
        }
        wCRCin = wCRCin and 0xffff
    }
}
