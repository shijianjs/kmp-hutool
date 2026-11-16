package io.github.shijianjs.kmphutool.core.bloomfilter

/**
 * A Bloom filter: `contains == false` is certain, `contains == true` may be a false positive.
 *
 * Port of `cn.hutool.bloomfilter.BloomFilter` (upstream also extends `Serializable`, which is
 * absent in common code).
 *
 * @hutool cn.hutool.bloomfilter.BloomFilter
 */
interface BloomFilter {
    fun contains(str: String): Boolean

    /** Adds [str] and returns `true` only when it was not already present. */
    fun add(str: String): Boolean
}
