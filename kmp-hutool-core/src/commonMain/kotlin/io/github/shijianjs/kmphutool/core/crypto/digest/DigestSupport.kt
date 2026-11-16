package io.github.shijianjs.kmphutool.core.crypto.digest

import io.github.shijianjs.kmphutool.core.crypto.digest.otp.HmacSha1

/**
 * Small internal digest primitives for [io.github.shijianjs.kmphutool.core.crypto.SignUtil].
 *
 * Public digest classes that need streams/providers remain excluded; this only provides the three
 * algorithms used by parameter signing.
 */
internal object DigestSupport {
    private val MD5_SHIFTS = intArrayOf(
        7, 12, 17, 22, 7, 12, 17, 22, 7, 12, 17, 22, 7, 12, 17, 22,
        5, 9, 14, 20, 5, 9, 14, 20, 5, 9, 14, 20, 5, 9, 14, 20,
        4, 11, 16, 23, 4, 11, 16, 23, 4, 11, 16, 23, 4, 11, 16, 23,
        6, 10, 15, 21, 6, 10, 15, 21, 6, 10, 15, 21, 6, 10, 15, 21,
    )

    private val MD5_K = IntArray(64) { i ->
        (kotlin.math.sin(i + 1.0) * 4294967296.0).toLong().let {
            if (it < 0) (-it).toInt() else it.toInt()
        }
    }

    private val SHA256_K = intArrayOf(
        0x428a2f98, 0x71374491, 0xb5c0fbcf.toInt(), 0xe9b5dba5.toInt(),
        0x3956c25b, 0x59f111f1, 0x923f82a4.toInt(), 0xab1c5ed5.toInt(),
        0xd807aa98.toInt(), 0x12835b01, 0x243185be, 0x550c7dc3,
        0x72be5d74, 0x80deb1fe.toInt(), 0x9bdc06a7.toInt(), 0xc19bf174.toInt(),
        0xe49b69c1.toInt(), 0xefbe4786.toInt(), 0x0fc19dc6, 0x240ca1cc,
        0x2de92c6f, 0x4a7484aa, 0x5cb0a9dc, 0x76f988da,
        0x983e5152.toInt(), 0xa831c66d.toInt(), 0xb00327c8.toInt(), 0xbf597fc7.toInt(),
        0xc6e00bf3.toInt(), 0xd5a79147.toInt(), 0x06ca6351, 0x14292967,
        0x27b70a85, 0x2e1b2138, 0x4d2c6dfc, 0x53380d13,
        0x650a7354, 0x766a0abb, 0x81c2c92e.toInt(), 0x92722c85.toInt(),
        0xa2bfe8a1.toInt(), 0xa81a664b.toInt(), 0xc24b8b70.toInt(), 0xc76c51a3.toInt(),
        0xd192e819.toInt(), 0xd6990624.toInt(), 0xf40e3585.toInt(), 0x106aa070,
        0x19a4c116, 0x1e376c08, 0x2748774c, 0x34b0bcb5,
        0x391c0cb3, 0x4ed8aa4a, 0x5b9cca4f, 0x682e6ff3,
        0x748f82ee, 0x78a5636f, 0x84c87814.toInt(), 0x8cc70208.toInt(),
        0x90befffa.toInt(), 0xa4506ceb.toInt(), 0xbef9a3f7.toInt(), 0xc67178f2.toInt(),
    )

    fun md5(input: ByteArray): ByteArray {
        val padded = padLittleEndian(input)
        var h0 = 0x67452301
        var h1 = 0xefcdab89.toInt()
        var h2 = 0x98badcfe.toInt()
        var h3 = 0x10325476

        var offset = 0
        while (offset < padded.size) {
            val m = IntArray(16)
            for (i in 0 until 16) {
                val index = offset + i * 4
                m[i] = (padded[index].toInt() and 0xff) or
                    ((padded[index + 1].toInt() and 0xff) shl 8) or
                    ((padded[index + 2].toInt() and 0xff) shl 16) or
                    ((padded[index + 3].toInt() and 0xff) shl 24)
            }

            var a = h0
            var b = h1
            var c = h2
            var d = h3
            for (i in 0 until 64) {
                val f: Int
                val g: Int
                when {
                    i < 16 -> {
                        f = (b and c) or (b.inv() and d)
                        g = i
                    }
                    i < 32 -> {
                        f = (d and b) or (d.inv() and c)
                        g = (5 * i + 1) % 16
                    }
                    i < 48 -> {
                        f = b xor c xor d
                        g = (3 * i + 5) % 16
                    }
                    else -> {
                        f = c xor (b or d.inv())
                        g = (7 * i) % 16
                    }
                }
                val temp = d
                d = c
                c = b
                b += rotateLeft(a + f + MD5_K[i] + m[g], MD5_SHIFTS[i])
                a = temp
            }

            h0 += a
            h1 += b
            h2 += c
            h3 += d
            offset += 64
        }

        val result = IntArray(4) { when (it) { 0 -> h0; 1 -> h1; 2 -> h2; else -> h3 } }
        return ByteArray(16) { i -> (result[i / 4] ushr (8 * (i % 4))).toByte() }
    }

    fun sha1(input: ByteArray): ByteArray = HmacSha1.sha1(input)

    fun sha256(input: ByteArray): ByteArray {
        val padded = padBigEndian(input)
        var h0 = 0x6a09e667
        var h1 = 0xbb67ae85.toInt()
        var h2 = 0x3c6ef372
        var h3 = 0xa54ff53a.toInt()
        var h4 = 0x510e527f
        var h5 = 0x9b05688c.toInt()
        var h6 = 0x1f83d9ab
        var h7 = 0x5be0cd19

        var offset = 0
        while (offset < padded.size) {
            val w = IntArray(64)
            for (i in 0 until 16) {
                val index = offset + i * 4
                w[i] = (padded[index].toInt() shl 24) or
                    ((padded[index + 1].toInt() and 0xff) shl 16) or
                    ((padded[index + 2].toInt() and 0xff) shl 8) or
                    (padded[index + 3].toInt() and 0xff)
            }
            for (i in 16 until 64) {
                val s0 = rotateRight(w[i - 15], 7) xor rotateRight(w[i - 15], 18) xor (w[i - 15] ushr 3)
                val s1 = rotateRight(w[i - 2], 17) xor rotateRight(w[i - 2], 19) xor (w[i - 2] ushr 10)
                w[i] = w[i - 16] + s0 + w[i - 7] + s1
            }

            var a = h0
            var b = h1
            var c = h2
            var d = h3
            var e = h4
            var f = h5
            var g = h6
            var h = h7
            for (i in 0 until 64) {
                val s1 = rotateRight(e, 6) xor rotateRight(e, 11) xor rotateRight(e, 25)
                val ch = (e and f) xor (e.inv() and g)
                val temp1 = h + s1 + ch + SHA256_K[i] + w[i]
                val s0 = rotateRight(a, 2) xor rotateRight(a, 13) xor rotateRight(a, 22)
                val maj = (a and b) xor (a and c) xor (b and c)
                val temp2 = s0 + maj
                h = g
                g = f
                f = e
                e = d + temp1
                d = c
                c = b
                b = a
                a = temp1 + temp2
            }

            h0 += a
            h1 += b
            h2 += c
            h3 += d
            h4 += e
            h5 += f
            h6 += g
            h7 += h
            offset += 64
        }

        val hash = intArrayOf(h0, h1, h2, h3, h4, h5, h6, h7)
        return ByteArray(32) { i -> (hash[i / 4] ushr (24 - (i % 4) * 8)).toByte() }
    }

    private fun padLittleEndian(input: ByteArray): ByteArray {
        val length = (64 - (input.size + 9) % 64) % 64
        val padded = ByteArray(input.size + 1 + length + 8)
        input.copyInto(padded)
        padded[input.size] = 0x80.toByte()
        val bits = input.size.toLong() * 8
        for (i in 0 until 8) padded[padded.size - 8 + i] = (bits ushr (8 * i)).toByte()
        return padded
    }

    private fun padBigEndian(input: ByteArray): ByteArray {
        val length = (64 - (input.size + 9) % 64) % 64
        val padded = ByteArray(input.size + 1 + length + 8)
        input.copyInto(padded)
        padded[input.size] = 0x80.toByte()
        val bits = input.size.toLong() * 8
        for (i in 0 until 8) padded[padded.size - 1 - i] = (bits ushr (8 * i)).toByte()
        return padded
    }

    private fun rotateLeft(value: Int, bits: Int): Int = (value shl bits) or (value ushr (32 - bits))
    private fun rotateRight(value: Int, bits: Int): Int = (value ushr bits) or (value shl (32 - bits))
}
