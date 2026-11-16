package io.github.shijianjs.kmphutool.core.lang.hash

/**
 * Google CityHash (32/64/128-bit).
 *
 * Port of `cn.hutool.core.lang.hash.CityHash`, which follows the reference implementation and reads
 * 32/64-bit words little-endian. Kotlin common has no unsigned type in the public Hutool API, so all
 * arithmetic is done with [ULong] internally and converted back at the boundary.
 *
 * @hutool cn.hutool.core.lang.hash.CityHash
 */
object CityHash {
    private val k0 = 0xc3a5c85c97cb3127uL
    private val k1 = 0xb492b66fbe98f273uL
    private val k2 = 0x9ae16a3b2f90404fuL
    private val kMul = 0x9ddfea08eb382d69uL

    private const val C1 = 0xcc9e2d51.toInt()
    private const val C2 = 0x1b873593

    fun hash32(data: ByteArray): Int {
        val length = data.size
        if (length <= 24) {
            return when {
                length <= 12 -> if (length <= 4) hash32Len0to4(data) else hash32Len5to12(data)
                else -> hash32Len13to24(data)
            }
        }

        var h = length
        var g = C1 * length
        var f = g
        var a0 = rotate32(fetch32(data, length - 4) * C1, 17) * C2
        var a1 = rotate32(fetch32(data, length - 8) * C1, 17) * C2
        var a2 = rotate32(fetch32(data, length - 16) * C1, 17) * C2
        var a3 = rotate32(fetch32(data, length - 12) * C1, 17) * C2
        var a4 = rotate32(fetch32(data, length - 20) * C1, 17) * C2
        h = h xor a0
        h = rotate32(h, 19)
        h = h * 5 + 0xe6546b64.toInt()
        h = h xor a2
        h = rotate32(h, 19)
        h = h * 5 + 0xe6546b64.toInt()
        g = g xor a1
        g = rotate32(g, 19)
        g = g * 5 + 0xe6546b64.toInt()
        g = g xor a3
        g = rotate32(g, 19)
        g = g * 5 + 0xe6546b64.toInt()
        f += a4
        f = rotate32(f, 19)
        f = f * 5 + 0xe6546b64.toInt()

        var iterations = (length - 1) / 20
        var pos = 0
        do {
            a0 = rotate32(fetch32(data, pos) * C1, 17) * C2
            a1 = fetch32(data, pos + 4)
            a2 = rotate32(fetch32(data, pos + 8) * C1, 17) * C2
            a3 = rotate32(fetch32(data, pos + 12) * C1, 17) * C2
            a4 = fetch32(data, pos + 16)
            h = h xor a0
            h = rotate32(h, 18)
            h = h * 5 + 0xe6546b64.toInt()
            f += a1
            f = rotate32(f, 19)
            f *= C1
            g += a2
            g = rotate32(g, 18)
            g = g * 5 + 0xe6546b64.toInt()
            h = h xor (a3 + a1)
            h = rotate32(h, 19)
            h = h * 5 + 0xe6546b64.toInt()
            g = g xor a4
            g = reverseBytes32(g) * 5
            h += a4 * 5
            h = reverseBytes32(h)
            f += a0
            val swapValue = f
            f = g
            g = h
            h = swapValue
            pos += 20
        } while (--iterations != 0)

        g = rotate32(g, 11) * C1
        g = rotate32(g, 17) * C1
        f = rotate32(f, 11) * C1
        f = rotate32(f, 17) * C1
        h = rotate32(h + g, 19)
        h = h * 5 + 0xe6546b64.toInt()
        h = rotate32(h, 17) * C1
        h = rotate32(h + f, 19)
        h = h * 5 + 0xe6546b64.toInt()
        h = rotate32(h, 17) * C1
        return h
    }

    fun hash64(data: ByteArray): Long {
        val length = data.size
        if (length <= 32) {
            return if (length <= 16) hashLen0to16(data).toLong() else hashLen17to32(data).toLong()
        }
        if (length <= 64) return hashLen33to64(data).toLong()

        var x = fetch64(data, length - 40)
        var y = fetch64(data, length - 16) + fetch64(data, length - 56)
        var z = hashLen16(fetch64(data, length - 48) + length.toULong(), fetch64(data, length - 24))
        var v = weakHashLen32WithSeeds(data, length - 64, length.toULong(), z)
        var w = weakHashLen32WithSeeds(data, length - 32, y + k1, x)
        x = x * k1 + fetch64(data, 0)

        var remaining = (length - 1) and -64
        var pos = 0
        do {
            x = rotate64(x + y + v.low + fetch64(data, pos + 8), 37) * k1
            y = rotate64(y + v.high + fetch64(data, pos + 48), 42) * k1
            x = x xor w.high
            y += v.low + fetch64(data, pos + 40)
            z = rotate64(z + w.low, 33) * k1
            v = weakHashLen32WithSeeds(data, pos, v.high * k1, x + w.low)
            w = weakHashLen32WithSeeds(data, pos + 32, z + w.high, y + fetch64(data, pos + 16))
            val swapValue = x
            x = z
            z = swapValue
            pos += 64
            remaining -= 64
        } while (remaining != 0)

        return hashLen16(
            hashLen16(v.low, w.low) + shiftMix(y) * k1 + z,
            hashLen16(v.high, w.high) + x,
        ).toLong()
    }

    fun hash64(data: ByteArray, seed0: Long, seed1: Long): Long =
        hashLen16(hash64(data).toULong() - seed0.toULong(), seed1.toULong()).toLong()

    fun hash64(data: ByteArray, seed: Long): Long = hash64(data, k2.toLong(), seed)

    fun hash128(data: ByteArray): Number128 {
        val length = data.size
        return if (length >= 16) {
            hash128(data, 16, Number128(fetch64(data, 0).toLong(), (fetch64(data, 8) + k0).toLong()))
        } else {
            hash128(data, 0, Number128(k0.toLong(), k1.toLong()))
        }
    }

    fun hash128(data: ByteArray, seed: Number128): Number128 = hash128(data, 0, seed)

    private class U128(val low: ULong, val high: ULong)

    private fun hash128(byteArray: ByteArray, start: Int, seed: Number128): Number128 {
        val length = byteArray.size - start
        if (length < 128) {
            return cityMurmur(byteArray.copyOfRange(start, byteArray.size), seed)
        }

        var v = U128(0uL, 0uL)
        var w = U128(0uL, 0uL)
        var x = seed.lowValue.toULong()
        var y = seed.highValue.toULong()
        var z = length.toULong() * k1
        v = U128(rotate64(y xor k1, 49) * k1 + fetch64(byteArray, start), 0uL)
        v = U128(v.low, rotate64(v.low, 42) * k1 + fetch64(byteArray, start + 8))
        w = U128(rotate64(y + z, 35) * k1 + x, 0uL)
        w = U128(w.low, rotate64(x + fetch64(byteArray, start + 88), 53) * k1)

        var pos = start
        var remaining = length
        do {
            x = rotate64(x + y + v.low + fetch64(byteArray, pos + 8), 37) * k1
            y = rotate64(y + v.high + fetch64(byteArray, pos + 48), 42) * k1
            x = x xor w.high
            y += v.low + fetch64(byteArray, pos + 40)
            z = rotate64(z + w.low, 33) * k1
            v = weakHashLen32WithSeeds(byteArray, pos, v.high * k1, x + w.low)
            w = weakHashLen32WithSeeds(byteArray, pos + 32, z + w.high, y + fetch64(byteArray, pos + 16))
            val swapValue = x
            x = z
            z = swapValue
            pos += 64

            x = rotate64(x + y + v.low + fetch64(byteArray, pos + 8), 37) * k1
            y = rotate64(y + v.high + fetch64(byteArray, pos + 48), 42) * k1
            x = x xor w.high
            y += v.low + fetch64(byteArray, pos + 40)
            z = rotate64(z + w.low, 33) * k1
            v = weakHashLen32WithSeeds(byteArray, pos, v.high * k1, x + w.low)
            w = weakHashLen32WithSeeds(byteArray, pos + 32, z + w.high, y + fetch64(byteArray, pos + 16))
            val secondSwap = x
            x = z
            z = secondSwap
            pos += 64
            remaining -= 128
        } while (remaining >= 128)

        x += rotate64(v.low + z, 49) * k0
        y = y * k0 + rotate64(w.high, 37)
        z = z * k0 + rotate64(w.low, 27)
        w = U128(w.low * 9uL, w.high)
        v = U128(v.low * k0, v.high)

        var tailDone = 0
        while (tailDone < remaining) {
            tailDone += 32
            y = rotate64(x + y, 42) * k0 + v.high
            w = U128(w.low + fetch64(byteArray, pos + remaining - tailDone + 16), w.high)
            x = x * k0 + w.low
            z += w.high + fetch64(byteArray, pos + remaining - tailDone)
            w = U128(w.low, w.high + v.low)
            v = weakHashLen32WithSeeds(byteArray, pos + remaining - tailDone, v.low + z, v.high)
            v = U128(v.low * k0, v.high)
        }

        x = hashLen16(x, v.low)
        y = hashLen16(y + z, w.low)
        return Number128(
            (hashLen16(x + v.high, w.high) + y).toLong(),
            hashLen16(x + w.high, y + v.high).toLong(),
        )
    }

    private fun hash32Len0to4(byteArray: ByteArray): Int {
        var b = 0
        var c = 9
        for (value in byteArray) {
            b = b * C1 + value.toInt()
            c = c xor b
        }
        return fmix(mur(b, mur(byteArray.size, c)))
    }

    private fun hash32Len5to12(byteArray: ByteArray): Int {
        val length = byteArray.size
        var a = length
        var b = length * 5
        var c = 9
        val d = b
        a += fetch32(byteArray, 0)
        b += fetch32(byteArray, length - 4)
        c += fetch32(byteArray, (length ushr 1) and 4)
        return fmix(mur(c, mur(b, mur(a, d))))
    }

    private fun hash32Len13to24(byteArray: ByteArray): Int {
        val length = byteArray.size
        val a = fetch32(byteArray, (length ushr 1) - 4)
        val b = fetch32(byteArray, 4)
        val c = fetch32(byteArray, length - 8)
        val d = fetch32(byteArray, length ushr 1)
        val e = fetch32(byteArray, 0)
        val f = fetch32(byteArray, length - 4)
        val h = length
        return fmix(mur(f, mur(e, mur(d, mur(c, mur(b, mur(a, h)))))))
    }

    private fun hashLen0to16(byteArray: ByteArray): ULong {
        val length = byteArray.size
        if (length >= 8) {
            val mul = k2 + (length * 2).toULong()
            val a = fetch64(byteArray, 0) + k2
            val b = fetch64(byteArray, length - 8)
            val c = rotate64(b, 37) * mul + a
            val d = (rotate64(a, 25) + b) * mul
            return hashLen16(c, d, mul)
        }
        if (length >= 4) {
            val mul = k2 + (length * 2).toULong()
            val a = fetch32(byteArray, 0).toUInt().toULong()
            return hashLen16(length.toULong() + (a shl 3), fetch32(byteArray, length - 4).toUInt().toULong(), mul)
        }
        if (length > 0) {
            val a = (byteArray[0].toInt() and 0xff).toULong()
            val b = (byteArray[length ushr 1].toInt() and 0xff).toULong()
            val c = (byteArray[length - 1].toInt() and 0xff).toULong()
            val y = a + (b shl 8)
            val z = length.toULong() + (c shl 2)
            return shiftMix(y * k2 xor z * k0) * k2
        }
        return k2
    }

    private fun hashLen17to32(byteArray: ByteArray): ULong {
        val length = byteArray.size
        val mul = k2 + (length * 2).toULong()
        val a = fetch64(byteArray, 0) * k1
        val b = fetch64(byteArray, 8)
        val c = fetch64(byteArray, length - 8) * mul
        val d = fetch64(byteArray, length - 16) * k2
        return hashLen16(
            rotate64(a + b, 43) + rotate64(c, 30) + d,
            a + rotate64(b + k2, 18) + c,
            mul,
        )
    }

    private fun hashLen33to64(byteArray: ByteArray): ULong {
        val length = byteArray.size
        val mul = k2 + (length * 2).toULong()
        var a = fetch64(byteArray, 0) * k2
        val b = fetch64(byteArray, 8)
        val c = fetch64(byteArray, length - 24)
        val d = fetch64(byteArray, length - 32)
        val e = fetch64(byteArray, 16) * k2
        val f = fetch64(byteArray, 24) * 9uL
        val g = fetch64(byteArray, length - 8)
        val h = fetch64(byteArray, length - 16) * mul
        val u = rotate64(a + g, 43) + (rotate64(b, 30) + c) * 9uL
        val v = ((a + g) xor d) + f + 1uL
        val w = reverseBytes64((u + v) * mul) + h
        val x = rotate64(e + f, 42) + c
        val y = (reverseBytes64((v + w) * mul) + g) * mul
        val z = e + f + c
        a = reverseBytes64((x + z) * mul + y) + b
        val shifted = shiftMix((z + a) * mul + d + h) * mul
        return shifted + x
    }

    private fun fetch64(byteArray: ByteArray, start: Int): ULong {
        var result = 0uL
        for (i in 0 until 8) {
            result = result or ((byteArray[start + i].toInt() and 0xff).toULong() shl (8 * i))
        }
        return result
    }

    private fun fetch32(byteArray: ByteArray, start: Int): Int {
        var result = 0
        for (i in 0 until 4) {
            result = result or ((byteArray[start + i].toInt() and 0xff) shl (8 * i))
        }
        return result
    }

    private fun rotate64(value: ULong, shift: Int): ULong =
        if (shift == 0) value else (value shr shift) or (value shl (64 - shift))

    private fun rotate32(value: Int, shift: Int): Int =
        if (shift == 0) value else (value ushr shift) or (value shl (32 - shift))

    private fun hashLen16(u: ULong, v: ULong, mul: ULong): ULong {
        var a = (u xor v) * mul
        a = a xor (a shr 47)
        var b = (v xor a) * mul
        b = b xor (b shr 47)
        return b * mul
    }

    private fun hashLen16(u: ULong, v: ULong): ULong {
        var a = (u xor v) * kMul
        a = a xor (a shr 47)
        var b = (v xor a) * kMul
        b = b xor (b shr 47)
        return b * kMul
    }

    private fun shiftMix(value: ULong): ULong = value xor (value shr 47)

    private fun fmix(value: Int): Int {
        var h = value
        h = h xor (h ushr 16)
        h *= 0x85ebca6b.toInt()
        h = h xor (h ushr 13)
        h *= 0xc2b2ae35.toInt()
        h = h xor (h ushr 16)
        return h
    }

    private fun mur(a: Int, h: Int): Int {
        var first = a * C1
        first = rotate32(first, 17)
        first *= C2
        var result = h xor first
        result = rotate32(result, 19)
        return result * 5 + 0xe6546b64.toInt()
    }

    private fun weakHashLen32WithSeeds(
        w: ULong,
        x: ULong,
        y: ULong,
        z: ULong,
        a: ULong,
        b: ULong,
    ): U128 {
        var seedA = a + w
        var seedB = rotate64(b + seedA + z, 21)
        val c = seedA
        seedA += x
        seedA += y
        seedB += rotate64(seedA, 44)
        return U128(seedA + z, seedB + c)
    }

    private fun weakHashLen32WithSeeds(byteArray: ByteArray, start: Int, a: ULong, b: ULong): U128 =
        weakHashLen32WithSeeds(
            fetch64(byteArray, start),
            fetch64(byteArray, start + 8),
            fetch64(byteArray, start + 16),
            fetch64(byteArray, start + 24),
            a,
            b,
        )

    private fun cityMurmur(byteArray: ByteArray, seed: Number128): Number128 {
        val length = byteArray.size
        var a = seed.lowValue.toULong()
        var b = seed.highValue.toULong()
        var c: ULong
        var d: ULong
        var l = length - 16
        if (l <= 0) {
            a = shiftMix(a * k1) * k1
            c = b * k1 + hashLen0to16(byteArray)
            d = shiftMix(a + if (length >= 8) fetch64(byteArray, 0) else c)
        } else {
            c = hashLen16(fetch64(byteArray, length - 8) + k1, a)
            d = hashLen16(b + length.toULong(), c + fetch64(byteArray, length - 16))
            a += d
            var pos = 0
            do {
                a = a xor shiftMix(fetch64(byteArray, pos) * k1) * k1
                a *= k1
                b = b xor a
                c = c xor shiftMix(fetch64(byteArray, pos + 8) * k1) * k1
                c *= k1
                d = d xor c
                pos += 16
                l -= 16
            } while (l > 0)
        }
        a = hashLen16(a, c)
        b = hashLen16(d, b)
        return Number128((a xor b).toLong(), hashLen16(b, a).toLong())
    }

    private fun reverseBytes32(value: Int): Int {
        var result = 0
        for (i in 0 until 4) {
            result = (result shl 8) or ((value ushr (8 * i)) and 0xff)
        }
        return result
    }

    private fun reverseBytes64(value: ULong): ULong {
        var result = 0uL
        for (i in 0 until 8) {
            result = (result shl 8) or ((value shr (8 * i)) and 0xffuL)
        }
        return result
    }
}
