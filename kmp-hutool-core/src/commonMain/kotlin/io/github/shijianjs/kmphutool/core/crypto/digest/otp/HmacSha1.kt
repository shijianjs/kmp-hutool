package io.github.shijianjs.kmphutool.core.crypto.digest.otp

/**
 * Internal HMAC-SHA1 used by [HOTP]/[TOTP].
 *
 * Hutool's `HMac` needs a JCE provider; common code does not, so the HOTP implementation carries the
 * one primitive it needs. This is not a public Hutool port and has no coverage tag.
 */
internal object HmacSha1 {
    private const val BLOCK_SIZE = 64

    fun digest(key: ByteArray, data: ByteArray): ByteArray {
        val normalized = if (key.size > BLOCK_SIZE) sha1(key) else key
        val ipad = ByteArray(BLOCK_SIZE) { 0x36 }
        val opad = ByteArray(BLOCK_SIZE) { 0x5c }
        for (i in normalized.indices) {
            ipad[i] = (ipad[i].toInt() xor normalized[i].toInt()).toByte()
            opad[i] = (opad[i].toInt() xor normalized[i].toInt()).toByte()
        }
        return sha1(opad + sha1(ipad + data))
    }

    fun sha1(input: ByteArray): ByteArray {
        val paddingLength = (64 - (input.size + 9) % 64) % 64
        val padded = ByteArray(input.size + 1 + paddingLength + 8)
        input.copyInto(padded)
        padded[input.size] = 0x80.toByte()
        val bitLength = input.size.toLong() * 8
        for (i in 0 until 8) {
            padded[padded.size - 1 - i] = (bitLength ushr (8 * i)).toByte()
        }

        var h0 = 0x67452301
        var h1 = 0xEFCDAB89.toInt()
        var h2 = 0x98BADCFE.toInt()
        var h3 = 0x10325476
        var h4 = 0xC3D2E1F0.toInt()

        var offset = 0
        while (offset < padded.size) {
            val w = IntArray(80)
            for (i in 0 until 16) {
                val index = offset + i * 4
                w[i] = (padded[index].toInt() shl 24) or
                    ((padded[index + 1].toInt() and 0xff) shl 16) or
                    ((padded[index + 2].toInt() and 0xff) shl 8) or
                    (padded[index + 3].toInt() and 0xff)
            }
            for (i in 16 until 80) w[i] = rotl(w[i - 3] xor w[i - 8] xor w[i - 14] xor w[i - 16], 1)

            var a = h0
            var b = h1
            var c = h2
            var d = h3
            var e = h4

            for (i in 0 until 80) {
                val f: Int
                val k: Int
                when {
                    i < 20 -> {
                        f = (b and c) or (b.inv() and d)
                        k = 0x5A827999
                    }
                    i < 40 -> {
                        f = b xor c xor d
                        k = 0x6ED9EBA1
                    }
                    i < 60 -> {
                        f = (b and c) or (b and d) or (c and d)
                        k = 0x8F1BBCDC.toInt()
                    }
                    else -> {
                        f = b xor c xor d
                        k = 0xCA62C1D6.toInt()
                    }
                }
                val temp = rotl(a, 5) + f + e + k + w[i]
                e = d
                d = c
                c = rotl(b, 30)
                b = a
                a = temp
            }

            h0 += a
            h1 += b
            h2 += c
            h3 += d
            h4 += e
            offset += 64
        }

        return intArrayOf(h0, h1, h2, h3, h4).let { hash ->
            ByteArray(20) { i -> (hash[i / 4] ushr (24 - (i % 4) * 8)).toByte() }
        }
    }

    private fun rotl(value: Int, bits: Int): Int = (value shl bits) or (value ushr (32 - bits))
}
