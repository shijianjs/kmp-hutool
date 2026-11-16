package io.github.shijianjs.kmphutool.core.codec

import io.github.shijianjs.kmphutool.core.util.StrUtil

/**
 * Base58 codec (no version byte, no checksum).
 *
 * Port of `cn.hutool.core.codec.Base58Codec`. The alphabet excludes `0`, `O`, `I` and `l` so
 * hand-copied strings stay unambiguous. [Base58] adds convenience entry points.
 *
 * @hutool cn.hutool.core.codec.Base58Codec
 */
class Base58Codec(
    private val alphabet: String = DEFAULT_ALPHABET,
) {
    /** Encodes bytes as Base58, preserving leading zero bytes as leading `1`s. */
    fun encode(data: ByteArray): String {
        if (data.isEmpty()) return ""
        var zeroCount = 0
        while (zeroCount < data.size && data[zeroCount].toInt() == 0) zeroCount++

        val work = data.copyOf()
        val encoded = CharArray(data.size * 2)
        var outputStart = encoded.size
        var inputStart = zeroCount
        while (inputStart < work.size) {
            encoded[--outputStart] = alphabet[divmod(work, inputStart, 256, 58).toInt()]
            if (work[inputStart].toInt() == 0) inputStart++
        }
        val alphabetZero = alphabet[0]
        while (outputStart < encoded.size && encoded[outputStart] == alphabetZero) outputStart++
        while (zeroCount-- > 0) encoded[--outputStart] = alphabetZero
        return encoded.concatToString(outputStart, encoded.size)
    }

    /** Decodes Base58 text; throws [IllegalArgumentException] on characters outside the alphabet. */
    fun decode(encoded: CharSequence): ByteArray {
        if (encoded.isEmpty()) return ByteArray(0)
        val lookup = ByteArray('z'.code + 1) { -1 }
        alphabet.forEachIndexed { index, c -> lookup[c.code] = index.toByte() }

        val input58 = ByteArray(encoded.length)
        for (i in encoded.indices) {
            val c = encoded[i]
            val digit = if (c.code < 128) lookup[c.code].toInt() else -1
            require(digit >= 0) { StrUtil.format("Invalid char '{}' at [{}]", c, i) }
            input58[i] = digit.toByte()
        }

        var zeros = 0
        while (zeros < input58.size && input58[zeros].toInt() == 0) zeros++

        val decoded = ByteArray(encoded.length)
        var outputStart = decoded.size
        var inputStart = zeros
        while (inputStart < input58.size) {
            decoded[--outputStart] = divmod(input58, inputStart, 58, 256)
            if (input58[inputStart].toInt() == 0) inputStart++
        }
        while (outputStart < decoded.size && decoded[outputStart].toInt() == 0) outputStart++
        return decoded.copyOfRange(outputStart - zeros, decoded.size)
    }

    companion object {
        /** Bitcoin alphabet: no `0`, `O`, `I`, `l`. */
        const val DEFAULT_ALPHABET: String = "123456789ABCDEFGHJKLMNPQRSTUVWXYZabcdefghijkmnopqrstuvwxyz"

        /** Shared stateless instance. */
        val INSTANCE = Base58Codec()

        /** Long-division step shared by encode and decode. */
        private fun divmod(number: ByteArray, firstDigit: Int, base: Int, divisor: Int): Byte {
            var remainder = 0
            for (i in firstDigit until number.size) {
                val digit = number[i].toInt() and 0xFF
                val temp = remainder * base + digit
                number[i] = (temp / divisor).toByte()
                remainder = temp % divisor
            }
            return remainder.toByte()
        }
    }
}
