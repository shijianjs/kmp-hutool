package io.github.shijianjs.kmphutool.core.lang.hash

/**
 * MurmurHash3: 32-bit, 64-bit and 128-bit variants over UTF-8 bytes.
 *
 * Port of `cn.hutool.core.lang.hash.MurmurHash`. The Java code leans on `ByteUtil` for little-endian
 * reads and on `Integer.rotateLeft`/`Long.rotateLeft`; both are reimplemented here so the object
 * works in `commonMain`.
 *
 * @hutool cn.hutool.core.lang.hash.MurmurHash
 */
object MurmurHash {
    private const val C1_32 = 0xcc9e2d51.toInt()
    private const val C2_32 = 0x1b873593
    private const val R1_32 = 15
    private const val R2_32 = 13
    private const val M_32 = 5
    private const val N_32 = -0x19ab949c // 0xe6546b64 as Int

    private val C1 = 0x87c37b91114253d5UL.toLong()
    private val C2 = 0x4cf5ad432745937fUL.toLong()
    private const val R1 = 31
    private const val R2 = 27
    private const val R3 = 33
    private const val M = 5L
    private const val N1 = 0x52dce729
    private const val N2 = 0x38495ab5

    private fun rotateLeft(value: Int, bits: Int): Int = (value shl bits) or (value ushr (32 - bits))
    private fun rotateLeft(value: Long, bits: Int): Long = (value shl bits) or (value ushr (64 - bits))

    private fun readIntLE(data: ByteArray, offset: Int): Int =
        (data[offset].toInt() and 0xff) or
            ((data[offset + 1].toInt() and 0xff) shl 8) or
            ((data[offset + 2].toInt() and 0xff) shl 16) or
            ((data[offset + 3].toInt() and 0xff) shl 24)

    private fun readLongLE(data: ByteArray, offset: Int): Long {
        var result = 0L
        for (i in 0 until 8) {
            result = result or ((data[offset + i].toLong() and 0xffL) shl (i * 8))
        }
        return result
    }

    /** Murmur3 32-bit over the UTF-8 bytes of [data]. */
    fun hash32(data: CharSequence): Int = hash32(data.toString().encodeToByteArray())

    /** Murmur3 32-bit with the default seed. */
    fun hash32(data: ByteArray): Int = hash32(data, data.size, 0)

    /** Murmur3 32-bit with an explicit [length] and [seed]. */
    fun hash32(data: ByteArray, length: Int, seed: Int): Int = hash32(data, 0, length, seed)

    /** Murmur3 32-bit over `data[offset until offset + length]`. */
    fun hash32(data: ByteArray, offset: Int, length: Int, seed: Int): Int {
        var hash = seed
        val nblocks = length shr 2
        for (i in 0 until nblocks) {
            val k = readIntLE(data, offset + (i shl 2))
            hash = mix32(k, hash)
        }

        val idx = offset + (nblocks shl 2)
        var k1 = 0
        when (offset + length - idx) {
            3 -> {
                k1 = k1 xor ((data[idx + 2].toInt() and 0xff) shl 16)
                k1 = k1 xor ((data[idx + 1].toInt() and 0xff) shl 8)
                k1 = k1 xor (data[idx].toInt() and 0xff)
                k1 *= C1_32
                k1 = rotateLeft(k1, R1_32)
                k1 *= C2_32
                hash = hash xor k1
            }
            2 -> {
                k1 = k1 xor ((data[idx + 1].toInt() and 0xff) shl 8)
                k1 = k1 xor (data[idx].toInt() and 0xff)
                k1 *= C1_32
                k1 = rotateLeft(k1, R1_32)
                k1 *= C2_32
                hash = hash xor k1
            }
            1 -> {
                k1 = k1 xor (data[idx].toInt() and 0xff)
                k1 *= C1_32
                k1 = rotateLeft(k1, R1_32)
                k1 *= C2_32
                hash = hash xor k1
            }
        }

        hash = hash xor length
        return fmix32(hash)
    }

    /** Murmur3 64-bit: the high half of the 128-bit variant, as upstream documents. */
    fun hash64(data: CharSequence): Long = hash64(data.toString().encodeToByteArray())

    /** Murmur3 64-bit with the default seed. */
    fun hash64(data: ByteArray): Long = hash64(data, data.size, 0)

    /** Murmur3 64-bit with an explicit [length] and [seed]. */
    fun hash64(data: ByteArray, length: Int, seed: Int): Long {
        var hash = seed.toLong()
        val nblocks = length shr 3
        for (i in 0 until nblocks) {
            val k = readLongLE(data, i shl 3)
            var mixed = k * C1
            mixed = rotateLeft(mixed, R1)
            mixed *= C2
            hash = hash xor mixed
            hash = rotateLeft(hash, R2) * M + N1
        }

        var k1 = 0L
        val tailStart = nblocks shl 3
        val tailLength = length - tailStart
        for (i in 0 until tailLength) {
            k1 = k1 xor ((data[tailStart + i].toLong() and 0xffL) shl (i * 8))
        }
        if (tailLength > 0) {
            k1 *= C1
            k1 = rotateLeft(k1, R1)
            k1 *= C2
            hash = hash xor k1
        }

        hash = hash xor length.toLong()
        return fmix64(hash)
    }

    /** Murmur3 128-bit over the UTF-8 bytes of [data]; the pair is the two halves of the hash. */
    fun hash128(data: CharSequence): LongArray = hash128(data.toString().encodeToByteArray())

    /** Murmur3 128-bit with the default seed. */
    fun hash128(data: ByteArray): LongArray = hash128(data, data.size, 0)

    /** Murmur3 128-bit with an explicit [length] and [seed]. */
    fun hash128(data: ByteArray, length: Int, seed: Int): LongArray = hash128(data, 0, length, seed)

    /** Murmur3 128-bit over `data[offset until offset + length]`. */
    fun hash128(data: ByteArray, offset: Int, length: Int, seed: Int): LongArray {
        val sanitizedSeed = seed.toLong() and 0xffffffffL
        var h1 = sanitizedSeed
        var h2 = sanitizedSeed
        val nblocks = length shr 4

        for (i in 0 until nblocks) {
            val i16 = offset + (i shl 4)
            var k1 = readLongLE(data, i16)
            var k2 = readLongLE(data, i16 + 8)

            k1 *= C1
            k1 = rotateLeft(k1, R1)
            k1 *= C2
            h1 = h1 xor k1
            h1 = rotateLeft(h1, R2)
            h1 += h2
            h1 = h1 * M + N1

            k2 *= C2
            k2 = rotateLeft(k2, R3)
            k2 *= C1
            h2 = h2 xor k2
            h2 = rotateLeft(h2, R1)
            h2 += h1
            h2 = h2 * M + N2
        }

        var k1 = 0L
        var k2 = 0L
        val tailStart = offset + (nblocks shl 4)
        val tailLength = offset + length - tailStart
        for (i in minOf(tailLength, 15) - 1 downTo 8) {
            k2 = k2 xor ((data[tailStart + i].toLong() and 0xffL) shl ((i - 8) * 8))
        }
        if (tailLength >= 9) {
            k2 *= C2
            k2 = rotateLeft(k2, R3)
            k2 *= C1
            h2 = h2 xor k2
        }
        for (i in minOf(tailLength, 8) - 1 downTo 0) {
            k1 = k1 xor ((data[tailStart + i].toLong() and 0xffL) shl (i * 8))
        }
        if (tailLength > 0) {
            k1 *= C1
            k1 = rotateLeft(k1, R1)
            k1 *= C2
            h1 = h1 xor k1
        }

        h1 = h1 xor length.toLong()
        h2 = h2 xor length.toLong()

        h1 += h2
        h2 += h1

        h1 = fmix64(h1)
        h2 = fmix64(h2)

        h1 += h2
        h2 += h1

        return longArrayOf(h1, h2)
    }

    private fun mix32(k: Int, hash: Int): Int {
        var mixed = k
        mixed *= C1_32
        mixed = rotateLeft(mixed, R1_32)
        mixed *= C2_32
        return rotateLeft(hash xor mixed, R2_32) * M_32 + N_32
    }

    private fun fmix32(hash: Int): Int {
        var h = hash
        h = h xor (h ushr 16)
        h *= 0x85ebca6b.toInt()
        h = h xor (h ushr 13)
        h *= 0xc2b2ae35.toInt()
        h = h xor (h ushr 16)
        return h
    }

    private fun fmix64(hash: Long): Long {
        var h = hash
        h = h xor (h ushr 33)
        h *= 0xff51afd7ed558ccdUL.toLong()
        h = h xor (h ushr 33)
        h *= 0xc4ceb9fe1a85ec53UL.toLong()
        h = h xor (h ushr 33)
        return h
    }
}
