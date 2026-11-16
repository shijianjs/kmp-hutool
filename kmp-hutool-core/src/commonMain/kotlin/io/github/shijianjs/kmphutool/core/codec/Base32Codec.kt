package io.github.shijianjs.kmphutool.core.codec

/**
 * RFC 4648 Base32 in both the standard and the "extended hex" alphabets.
 *
 * Port of `cn.hutool.core.codec.Base32Codec`. Upstream streams bits byte by byte; this port keeps a
 * small bit accumulator, which yields the same 8-character groups and `=` padding. Decoding
 * tolerates lowercase letters and skips characters outside the alphabet, as upstream does.
 *
 * @hutool cn.hutool.core.codec.Base32Codec
 */
class Base32Codec : Encoder<ByteArray>, Decoder<ByteArray> {

    override fun encode(data: ByteArray): String = encode(data, false)

    /** Encodes [data] using the hex alphabet when [useHex] is `true`. */
    fun encode(data: ByteArray, useHex: Boolean): String {
        val alphabet = if (useHex) HEX_ALPHABET else DEFAULT_ALPHABET
        if (data.isEmpty()) return ""

        val out = StringBuilder((data.size * 8 + 4) / 5 + 1)
        var buffer = 0
        var bits = 0
        for (b in data) {
            buffer = (buffer shl 8) or (b.toInt() and 0xff)
            bits += 8
            while (bits >= 5) {
                bits -= 5
                out.append(alphabet[(buffer shr bits) and 0x1f])
            }
        }
        if (bits > 0) {
            out.append(alphabet[(buffer shl (5 - bits)) and 0x1f])
        }
        while (out.length % 8 != 0) out.append('=')
        return out.toString()
    }

    override fun decode(data: String): ByteArray = decode(data, false)

    /** Decodes [encoded] with the hex alphabet when [useHex] is `true`. */
    fun decode(encoded: CharSequence, useHex: Boolean): ByteArray {
        val alphabet = if (useHex) HEX_ALPHABET else DEFAULT_ALPHABET
        val out = ArrayList<Byte>(encoded.length * 5 / 8 + 1)
        var buffer = 0
        var bits = 0
        for (c in encoded) {
            val value = alphabet.indexOf(c.uppercaseChar())
            if (value < 0) continue
            buffer = (buffer shl 5) or value
            bits += 5
            if (bits >= 8) {
                bits -= 8
                out.add(((buffer shr bits) and 0xff).toByte())
            }
        }
        return out.toByteArray()
    }

    companion object {
        val INSTANCE: Base32Codec = Base32Codec()

        private const val DEFAULT_ALPHABET = "ABCDEFGHIJKLMNOPQRSTUVWXYZ234567"
        private const val HEX_ALPHABET = "0123456789ABCDEFGHIJKLMNOPQRSTUV"
    }
}
