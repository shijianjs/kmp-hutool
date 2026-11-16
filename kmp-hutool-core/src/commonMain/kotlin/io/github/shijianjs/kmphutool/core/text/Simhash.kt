package io.github.shijianjs.kmphutool.core.text

import io.github.shijianjs.kmphutool.core.lang.hash.MurmurHash

/**
 * Simhash, a locality-sensitive hash used to deduplicate large text collections: texts that share
 * most of their words end up with close 64-bit hashes.
 *
 * Port of `cn.hutool.core.text.Simhash`. The bit-field construction avoids upstream's
 * `BigInteger`-from-binary-string step: bit `i` is set when the weighted vote is positive. Upstream
 * guards `storage` with a `StampedLock`; Kotlin targets here are single-threaded or rely on
 * collections that are already safe, so the locking is dropped and documented.
 *
 * @hutool cn.hutool.core.text.Simhash
 */
class Simhash(
    private val fracCount: Int = 4,
    private val hammingThresh: Int = 3,
) {
    private val bitNum = 64
    private val fracBitNum = bitNum / fracCount

    /** One bucket map per segment, so a lookup only compares hashes sharing a segment. */
    private val storage: List<MutableMap<String, MutableList<Long>>> =
        List(fracCount) { linkedMapOf() }

    /** The 64-bit simhash of the token list. */
    fun hash(segList: Collection<CharSequence>): Long {
        val weight = IntArray(bitNum)
        for (seg in segList) {
            val wordHash = MurmurHash.hash64(seg)
            for (i in 0 until bitNum) {
                if ((wordHash ushr i) and 1L == 1L) weight[i] += 1 else weight[i] -= 1
            }
        }

        var result = 0L
        for (i in 0 until bitNum) {
            if (weight[i] > 0) result = result or (1L shl i)
        }
        return result
    }

    /** `true` when a stored hash is within [hammingThresh] of this text's hash. */
    fun equals(segList: Collection<CharSequence>): Boolean {
        val simhash = hash(segList)
        val fracs = splitSimhash(simhash)
        for (i in 0 until fracCount) {
            val candidates = storage[i][fracs[i]] ?: continue
            for (stored in candidates) {
                if (hamming(simhash, stored) < hammingThresh) return true
            }
        }
        return false
    }

    /** Stores [simhash] under every segment bucket, as upstream indexes it. */
    fun store(simhash: Long) {
        val fracs = splitSimhash(simhash)
        for (i in 0 until fracCount) {
            storage[i].getOrPut(fracs[i]) { mutableListOf() }.add(simhash)
        }
    }

    /** Hamming distance over all 64 bits. */
    fun hamming(a: Long, b: Long): Int {
        var distance = 0
        for (i in 0 until bitNum) {
            if ((a ushr i and 1L) != (b ushr i and 1L)) distance++
        }
        return distance
    }

    /** Cuts the hash into [fracCount] equal bit strings, low bits first, like upstream. */
    fun splitSimhash(simhash: Long): List<String> {
        val parts = ArrayList<String>(fracCount)
        val sb = StringBuilder()
        for (i in 0 until bitNum) {
            sb.append(if ((simhash ushr i and 1L) == 1L) '1' else '0')
            if ((i + 1) % fracBitNum == 0) {
                parts.add(sb.toString())
                sb.setLength(0)
            }
        }
        return parts
    }
}
