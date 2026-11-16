package io.github.shijianjs.kmphutool.core.bloomfilter

/**
 * Factory helpers for Bloom filters.
 *
 * Port of `cn.hutool.bloomfilter.BloomFilterUtil`. `createBitSet` is omitted because
 * `java.util.BitSet` has no common counterpart; use [createBitMap].
 *
 * @hutool cn.hutool.bloomfilter.BloomFilterUtil
 */
object BloomFilterUtil {
    /** Creates a bitmap-backed Bloom filter sized by [m]. */
    fun createBitMap(m: Int): BitMapBloomFilter = BitMapBloomFilter(m)
}
