package io.github.shijianjs.kmphutool.core.codec

/**
 * Base62 codec, handy for short URLs.
 *
 * Port of `cn.hutool.core.codec.Base62Codec` (seruco/base62 lineage). Two alphabets are supported:
 * GMP (`0-9A-Za-z`) and the inverted variant with swapped letter case.
 *
 * @hutool cn.hutool.core.codec.Base62Codec
 */
class Base62Codec {
    /** Encodes bytes with the GMP alphabet. */
    fun encode(data: ByteArray): ByteArray = encode(data, false)

    /** Encodes with either the GMP or the inverted alphabet. */
    fun encode(data: ByteArray, useInverted: Boolean): ByteArray =
        translate(convert(data, STANDARD_BASE, TARGET_BASE), if (useInverted) INVERTED else GMP)

    /** Decodes Base62 bytes written with the GMP alphabet. */
    fun decode(encoded: ByteArray): ByteArray = decode(encoded, false)

    /** Decodes with either the GMP or the inverted alphabet. */
    fun decode(encoded: ByteArray, useInverted: Boolean): ByteArray =
        convert(translate(encoded, lookup(if (useInverted) INVERTED else GMP)), TARGET_BASE, STANDARD_BASE)

    companion object {
        private const val STANDARD_BASE = 256
        private const val TARGET_BASE = 62

        private val GMP = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz".encodeToByteArray()
        private val INVERTED = "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ".encodeToByteArray()

        /** Shared stateless instance. */
        val INSTANCE = Base62Codec()

        private fun lookup(alphabet: ByteArray): ByteArray {
            val table = ByteArray('z'.code + 1)
            alphabet.forEachIndexed { index, value -> table[value.toInt()] = index.toByte() }
            return table
        }

        private fun translate(indices: ByteArray, dictionary: ByteArray): ByteArray =
            ByteArray(indices.size) { dictionary[indices[it].toInt()] }

        /** Base conversion, one long-division pass per output digit. */
        private fun convert(message: ByteArray, sourceBase: Int, targetBase: Int): ByteArray {
            val out = ArrayList<Byte>(estimateOutputLength(message.size, sourceBase, targetBase))
            var source = message
            while (source.isNotEmpty()) {
                val quotient = ArrayList<Byte>(source.size)
                var remainder = 0
                for (b in source) {
                    val accumulator = (b.toInt() and 0xFF) + remainder * sourceBase
                    val digit = (accumulator - (accumulator % targetBase)) / targetBase
                    remainder = accumulator % targetBase
                    if (quotient.isNotEmpty() || digit > 0) quotient.add(digit.toByte())
                }
                out.add(remainder.toByte())
                source = quotient.toByteArray()
            }
            for (i in 0 until message.size - 1) {
                if (message[i].toInt() != 0) break
                out.add(0)
            }
            return out.toByteArray().reversedArray()
        }

        private fun estimateOutputLength(inputLength: Int, sourceBase: Int, targetBase: Int): Int =
            kotlin.math.ceil(
                (kotlin.math.ln(sourceBase.toDouble()) / kotlin.math.ln(targetBase.toDouble())) * inputLength,
            ).toInt()
    }
}
