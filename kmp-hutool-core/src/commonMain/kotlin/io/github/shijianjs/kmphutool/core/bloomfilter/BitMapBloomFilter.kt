package io.github.shijianjs.kmphutool.core.bloomfilter

import io.github.shijianjs.kmphutool.core.bloomfilter.filter.DefaultFilter
import io.github.shijianjs.kmphutool.core.bloomfilter.filter.ELFFilter
import io.github.shijianjs.kmphutool.core.bloomfilter.filter.JSFilter
import io.github.shijianjs.kmphutool.core.bloomfilter.filter.PJWFilter
import io.github.shijianjs.kmphutool.core.bloomfilter.filter.SDBMFilter

/**
 * A Bloom filter composed from several hash filters.
 *
 * Port of `cn.hutool.bloomfilter.BitMapBloomFilter`.
 *
 * @hutool cn.hutool.bloomfilter.BitMapBloomFilter
 */
class BitMapBloomFilter : BloomFilter {
    private var filters: Array<BloomFilter>

    constructor(m: Int) {
        val size = (m.toDouble() / 5 * 1024 * 1024 * 8).toLong()
        filters = arrayOf(
            DefaultFilter(size),
            ELFFilter(size),
            JSFilter(size),
            PJWFilter(size),
            SDBMFilter(size),
        )
    }

    constructor(m: Int, vararg filters: BloomFilter) : this(m) {
        this.filters = arrayOf(*filters)
    }

    override fun add(str: String): Boolean {
        var added = false
        for (filter in filters) added = filter.add(str) or added
        return added
    }

    override fun contains(str: String): Boolean {
        for (filter in filters) if (!filter.contains(str)) return false
        return true
    }
}
