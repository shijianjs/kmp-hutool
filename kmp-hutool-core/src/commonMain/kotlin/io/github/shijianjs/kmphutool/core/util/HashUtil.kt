package io.github.shijianjs.kmphutool.core.util

import io.github.shijianjs.kmphutool.core.lang.hash.MurmurHash

/**
 * Non-cryptographic string/byte hashes.
 *
 * Port of `cn.hutool.core.util.HashUtil` minus the JVM-only `identityHashCode` and the CityHash /
 * MetroHash families (their classes are not ported yet). Murmur calls delegate to [MurmurHash].
 *
 * @hutool cn.hutool.core.util.HashUtil
 */
object HashUtil {
    /** Additive hash. */
    fun additiveHash(key: CharSequence, prime: Int): Int {
        var hash = key.length
        for (i in 0 until key.length) hash += key[i].code
        return hash % prime
    }

    /** Rotating hash. */
    fun rotatingHash(key: CharSequence, prime: Int): Int {
        var hash = key.length
        for (i in 0 until key.length) {
            hash = (hash shl 4) xor (hash ushr 28) xor key[i].code
        }
        return hash % prime
    }

    /** One-at-a-time hash. */
    fun oneByOneHash(key: CharSequence): Int {
        var hash = 0
        for (i in 0 until key.length) {
            hash += key[i].code
            hash += hash shl 10
            hash = hash xor (hash ushr 6)
        }
        hash += hash shl 3
        hash = hash xor (hash ushr 11)
        hash += hash shl 15
        return hash
    }

    /** Bernstein hash. */
    fun bernstein(key: CharSequence): Int {
        var hash = 0
        for (i in 0 until key.length) hash = 33 * hash + key[i].code
        return hash
    }

    /** Universal hashing. */
    fun universal(key: CharArray, mask: Int, tab: IntArray): Int {
        var hash = key.size
        val len = key.size
        var i = 0
        while (i < len shl 3) {
            val k = key[i shr 3].code
            if ((k and 0x01) == 0) hash = hash xor tab[i]
            if ((k and 0x02) == 0) hash = hash xor tab[i + 1]
            if ((k and 0x04) == 0) hash = hash xor tab[i + 2]
            if ((k and 0x08) == 0) hash = hash xor tab[i + 3]
            if ((k and 0x10) == 0) hash = hash xor tab[i + 4]
            if ((k and 0x20) == 0) hash = hash xor tab[i + 5]
            if ((k and 0x40) == 0) hash = hash xor tab[i + 6]
            if ((k and 0x80) == 0) hash = hash xor tab[i + 7]
            i += 8
        }
        return hash and mask
    }

    /** Zobrist hashing. */
    fun zobrist(key: CharArray, mask: Int, tab: Array<IntArray>): Int {
        var hash = key.size
        for (i in key.indices) hash = hash xor tab[i][key[i].code]
        return hash and mask
    }

    /** 32-bit FNV-1. */
    fun fnvHash(data: ByteArray): Int {
        val p = 16777619
        var hash = 2166136261L.toInt()
        for (b in data) hash = (hash xor b.toInt()) * p
        hash += hash shl 13
        hash = hash xor (hash ushr 7)
        hash += hash shl 3
        hash = hash xor (hash ushr 17)
        hash += hash shl 5
        return kotlin.math.abs(hash)
    }

    /** 32-bit FNV-1 over chars. */
    fun fnvHash(data: CharSequence): Int {
        val p = 16777619
        var hash = 2166136261L.toInt()
        for (i in 0 until data.length) hash = (hash xor data[i].code) * p
        hash += hash shl 13
        hash = hash xor (hash ushr 7)
        hash += hash shl 3
        hash = hash xor (hash ushr 17)
        hash += hash shl 5
        return kotlin.math.abs(hash)
    }

    /** Thomas Wang's integer hash. */
    fun intHash(key0: Int): Int {
        var key = key0
        key += (key shl 15).inv()
        key = key xor (key ushr 10)
        key += key shl 3
        key = key xor (key ushr 6)
        key += (key shl 11).inv()
        key = key xor (key ushr 16)
        return key
    }

    /** RS hash. */
    fun rsHash(str: CharSequence): Int {
        var b = 378551
        var a = 63689
        var hash = 0
        for (i in 0 until str.length) {
            hash = hash * a + str[i].code
            a *= b
        }
        return hash and 0x7FFFFFFF
    }

    /** JS hash. */
    fun jsHash(str: CharSequence): Int {
        var hash = 1315423911
        for (i in 0 until str.length) hash = hash xor ((hash shl 5) + str[i].code + (hash ushr 2))
        return kotlin.math.abs(hash) and 0x7FFFFFFF
    }

    /** PJW hash. */
    fun pjwHash(str: CharSequence): Int {
        val bitsInUnsignedInt = 32
        val threeQuarters = bitsInUnsignedInt * 3 / 4
        val oneEighth = bitsInUnsignedInt / 8
        val highBits = 0xFFFFFFFF.toInt() shl (bitsInUnsignedInt - oneEighth)
        var hash = 0
        for (i in 0 until str.length) {
            hash = (hash shl oneEighth) + str[i].code
            val test = hash and highBits
            if (test != 0) hash = (hash xor (test shr threeQuarters)) and highBits.inv()
        }
        return hash and 0x7FFFFFFF
    }

    /** ELF hash. */
    fun elfHash(str: CharSequence): Int {
        var hash = 0
        for (i in 0 until str.length) {
            hash = (hash shl 4) + str[i].code
            val x = hash and 0xF0000000.toInt()
            if (x != 0) {
                hash = hash xor (x shr 24)
                hash = hash and x.inv()
            }
        }
        return hash and 0x7FFFFFFF
    }

    /** BKDR hash. */
    fun bkdrHash(str: CharSequence): Int {
        val seed = 131
        var hash = 0
        for (i in 0 until str.length) hash = hash * seed + str[i].code
        return hash and 0x7FFFFFFF
    }

    /** SDBM hash. */
    fun sdbmHash(str: CharSequence): Int {
        var hash = 0
        for (i in 0 until str.length) hash = str[i].code + (hash shl 6) + (hash shl 16) - hash
        return hash and 0x7FFFFFFF
    }

    /** DJB hash. */
    fun djbHash(str: CharSequence): Int {
        var hash = 5381
        for (i in 0 until str.length) hash = (hash shl 5) + hash + str[i].code
        return hash and 0x7FFFFFFF
    }

    /** DEK hash. */
    fun dekHash(str: CharSequence): Int {
        var hash = str.length
        for (i in 0 until str.length) hash = (hash shl 5) xor (hash ushr 27) xor str[i].code
        return hash and 0x7FFFFFFF
    }

    /** AP hash. */
    fun apHash(str: CharSequence): Int {
        var hash = 0
        for (i in 0 until str.length) {
            hash = hash xor if (i and 1 == 0) {
                (hash shl 7) xor str[i].code xor (hash ushr 3)
            } else {
                ((hash shl 11) xor str[i].code xor (hash ushr 5)).inv()
            }
        }
        return hash
    }

    /** TianL hash. */
    fun tianlHash(str: CharSequence): Long {
        val length = str.length
        if (length == 0) return 0
        var hash = if (length <= 256) 16777216L * (length - 1) else 4278190080L
        if (length <= 96) {
            for (i in 1..length) {
                var c = str[i - 1]
                if (c in 'A'..'Z') c = (c.code + 32).toChar()
                hash += (3L * i * c.code * c.code + 5L * i * c.code + 7L * i + 11 * c.code) % 16777216
            }
        } else {
            for (i in 1..96) {
                var c = str[i + length - 96 - 1]
                if (c in 'A'..'Z') c = (c.code + 32).toChar()
                hash += (3L * i * c.code * c.code + 5L * i * c.code + 7L * i + 11 * c.code) % 16777216
            }
        }
        if (hash < 0) hash *= -1
        return hash
    }

    /** The Java `String.hashCode` algorithm (31 * h + char). */
    fun javaDefaultHash(str: CharSequence): Int {
        var h = 0
        for (i in 0 until str.length) h = 31 * h + str[i].code
        return h
    }

    /** Mixes [javaDefaultHash] and [fnvHash] into a 64-bit value. */
    fun mixHash(str: CharSequence): Long {
        var hash = javaDefaultHash(str).toLong()
        hash = hash shl 32
        hash = hash or fnvHash(str).toLong()
        return hash
    }

    fun murmur32(data: ByteArray): Int = MurmurHash.hash32(data)

    fun murmur64(data: ByteArray): Long = MurmurHash.hash64(data)

    fun murmur128(data: ByteArray): LongArray = MurmurHash.hash128(data)

    /** HF hash. */
    fun hfHash(data: CharSequence): Long {
        var hash = 0L
        for (i in 0 until data.length) hash += data[i].code.toLong() * 3 * i
        if (hash < 0) hash = -hash
        return hash
    }

    /** HFIP hash. */
    fun hfIpHash(data: CharSequence): Long {
        var hash = 0L
        for (i in 0 until data.length) hash += data[i % 4].code xor data[i].code
        return hash
    }
}
