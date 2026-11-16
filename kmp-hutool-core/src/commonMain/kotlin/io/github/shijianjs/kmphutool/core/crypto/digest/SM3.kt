package io.github.shijianjs.kmphutool.core.crypto.digest

import io.github.shijianjs.kmphutool.core.util.HexUtil

/**
 * Pure Kotlin implementation of the Chinese SM3 hash (GB/T 32905-2016).
 *
 * Port of `cn.hutool.crypto.digest.SM3`. Hutool delegates to a JCE provider; common code has no
 * provider, so this class contains the compression function itself.
 *
 * @hutool cn.hutool.crypto.digest.SM3
 */
class SM3(
    private val salt: ByteArray? = null,
    private val saltPosition: Int = 0,
    private val digestCount: Int = 1,
) {
    fun digest(data: ByteArray): ByteArray {
        var value = data
        if (salt != null) {
            val position = saltPosition.coerceIn(0, value.size)
            value = value.copyOfRange(0, position) + salt + value.copyOfRange(position, value.size)
        }
        var result = value
        repeat(digestCount.coerceAtLeast(1)) {
            result = digestOnce(result)
        }
        return result
    }

    fun digestHex(data: ByteArray): String = HexUtil.encodeHexStr(digest(data))

    fun digestHex(data: CharSequence): String = digestHex(data.toString().encodeToByteArray())

    private fun digestOnce(input: ByteArray): ByteArray {
        val paddingLength = (64 - (input.size + 9) % 64) % 64
        val padded = ByteArray(input.size + 1 + paddingLength + 8)
        input.copyInto(padded)
        padded[input.size] = 0x80.toByte()
        val bitLength = input.size.toLong() * 8
        for (i in 0 until 8) {
            padded[padded.size - 1 - i] = (bitLength ushr (8 * i)).toByte()
        }

        val v = intArrayOf(
            0x7380166f, 0x4914b2b9, 0x172442d7, 0xda8a0600.toInt(),
            0xa96f30bc.toInt(), 0x163138aa, 0xe38dee4d.toInt(), 0xb0fb0e4e.toInt(),
        )

        var offset = 0
        while (offset < padded.size) {
            val w = IntArray(68)
            val w1 = IntArray(64)
            for (i in 0 until 16) {
                val index = offset + i * 4
                w[i] = ((padded[index].toInt() shl 24) or
                    ((padded[index + 1].toInt() and 0xff) shl 16) or
                    ((padded[index + 2].toInt() and 0xff) shl 8) or
                    (padded[index + 3].toInt() and 0xff))
            }
            for (i in 16 until 68) {
                w[i] = p1(w[i - 16] xor w[i - 9] xor rotl(w[i - 3], 15)) xor
                    rotl(w[i - 13], 7) xor w[i - 6]
            }
            for (i in 0 until 64) w1[i] = w[i] xor w[i + 4]

            var a = v[0]
            var b = v[1]
            var c = v[2]
            var d = v[3]
            var e = v[4]
            var f = v[5]
            var g = v[6]
            var h = v[7]

            for (j in 0 until 64) {
                val t = if (j < 16) 0x79cc4519 else 0x7a879d8a
                val ss1 = rotl(add(add(rotl(a, 12), e), rotl(t, j)), 7)
                val ss2 = ss1 xor rotl(a, 12)
                val ff = if (j < 16) a xor b xor c else (a and b) or (a and c) or (b and c)
                val gg = if (j < 16) e xor f xor g else (e and f) or (e.inv() and g)
                val tt1 = add(add(add(ff, d), ss2), w1[j])
                val tt2 = add(add(add(gg, h), ss1), w[j])
                d = c
                c = rotl(b, 9)
                b = a
                a = tt1
                h = g
                g = rotl(f, 19)
                f = e
                e = p0(tt2)
            }
            v[0] = v[0] xor a
            v[1] = v[1] xor b
            v[2] = v[2] xor c
            v[3] = v[3] xor d
            v[4] = v[4] xor e
            v[5] = v[5] xor f
            v[6] = v[6] xor g
            v[7] = v[7] xor h
            offset += 64
        }

        return ByteArray(32) { i ->
            (v[i / 4] ushr (24 - (i % 4) * 8)).toByte()
        }
    }

    private fun add(x: Int, y: Int): Int = x + y
    private fun rotl(x: Int, n: Int): Int = (x shl n) or (x ushr (32 - n))
    private fun p0(x: Int): Int = x xor rotl(x, 9) xor rotl(x, 17)
    private fun p1(x: Int): Int = x xor rotl(x, 15) xor rotl(x, 23)

    companion object {
        const val ALGORITHM_NAME = "SM3"

        fun create(): SM3 = SM3()
    }
}
