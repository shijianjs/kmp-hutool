package io.github.shijianjs.kmphutool.core.codec

/**
 * Base85 (Ascii85) codec over byte arrays, with the standard alphabet and the ZeroMQ Z85 variant.
 *
 * Port of `cn.hutool.core.codec.Base85Codec`. `ByteArrayOutputStream` is not available in Kotlin
 * common, so the exact output size is computed up front and the result is filled in place; that also
 * removes the need for the upstream growth heuristic.
 *
 * @hutool cn.hutool.core.codec.Base85Codec
 */
class Base85Codec {
    /** Encodes [data] with the standard Ascii85 alphabet. */
    fun encode(data: ByteArray): ByteArray = encode(data, Z85 = false)

    /** Encodes [data], choosing the Z85 alphabet when [Z85] is true. */
    fun encode(data: ByteArray, Z85: Boolean): ByteArray {
        if (data.isEmpty()) return ByteArray(0)
        val alphabet = if (Z85) Z85_ALPHABET else STANDARD_ALPHABET
        val fullChunks = data.size / 4
        val remainder = data.size % 4
        val outputLength = fullChunks * 5 + if (remainder > 0) remainder + 1 else 0
        val out = ByteArray(outputLength)
        var outIndex = 0
        var tuple: Long = 0
        var count = 0
        for (byte in data) {
            tuple = (tuple shl 8) or (byte.toLong() and 0xFF)
            count++
            if (count == 4) {
                outIndex = writeEncodedTuple(out, outIndex, tuple, 5, alphabet)
                tuple = 0
                count = 0
            }
        }
        if (count > 0) {
            tuple = tuple shl (8 * (4 - count))
            outIndex = writeEncodedTuple(out, outIndex, tuple, count + 1, alphabet)
        }
        return out
    }

    /** Decodes standard Ascii85 text. */
    fun decode(encoded: ByteArray): ByteArray = decode(encoded, Z85 = false)

    /** Decodes [encoded], choosing the Z85 alphabet when [Z85] is true. */
    fun decode(encoded: ByteArray, Z85: Boolean): ByteArray {
        if (encoded.isEmpty()) return ByteArray(0)
        val lookup = if (Z85) Z85_LOOKUP else STANDARD_LOOKUP
        val outputLength = estimateDecodedLength(encoded.size)
        val out = ByteArray(outputLength)
        var outIndex = 0
        var tuple: Long = 0
        var count = 0
        for (byte in encoded) {
            val index = byte.toInt() and 0xFF
            val value = if (index < lookup.size) lookup[index] else -1
            if (value < 0) throw IllegalArgumentException("Invalid Base85 character: '${byte.toInt().toChar()}'")
            tuple = tuple * 85 + value
            count++
            if (count == 5) {
                outIndex = writeDecodedTuple(out, outIndex, tuple, 4)
                tuple = 0
                count = 0
            }
        }
        if (count > 0) {
            if (count == 1) throw IllegalArgumentException("Invalid Base85 encoding: single trailing character")
            for (i in count until 5) {
                tuple = tuple * 85 + 84
            }
            outIndex = writeDecodedTuple(out, outIndex, tuple, count - 1)
        }
        return if (outIndex == out.size) out else out.copyOf(outIndex)
    }

    private fun writeEncodedTuple(out: ByteArray, start: Int, tuple: Long, charCount: Int, alphabet: ByteArray): Int {
        var value = tuple
        var index = start
        val encoded = IntArray(5)
        for (i in 4 downTo 0) {
            encoded[i] = (value % 85).toInt()
            value /= 85
        }
        for (i in 0 until charCount) {
            out[index++] = alphabet[encoded[i]]
        }
        return index
    }

    private fun writeDecodedTuple(out: ByteArray, start: Int, tuple: Long, byteCount: Int): Int {
        var index = start
        // Partial blocks are padded on the right, so the valid bytes are the high ones.
        for (i in 3 downTo 4 - byteCount) {
            out[index++] = ((tuple shr (i * 8)) and 0xFF).toByte()
        }
        return index
    }

    companion object {
        private val STANDARD_ALPHABET = ByteArray(85) { ('!' + it).toByte() }

        private val Z85_ALPHABET = "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ.-:+=^!/*?&<>()[]{}@%$#".encodeToByteArray()

        private val STANDARD_LOOKUP = buildLookup(STANDARD_ALPHABET)
        private val Z85_LOOKUP = buildLookup(Z85_ALPHABET)

        /** A shared instance, matching upstream's `INSTANCE`. */
        val INSTANCE: Base85Codec = Base85Codec()

        private fun buildLookup(alphabet: ByteArray): IntArray {
            val table = IntArray(256) { -1 }
            for (index in alphabet.indices) {
                table[alphabet[index].toInt() and 0xFF] = index
            }
            return table
        }

        private fun estimateDecodedLength(encodedLength: Int): Int =
            (encodedLength / 5) * 4 + maxOf(0, encodedLength % 5 - 1)
    }
}
