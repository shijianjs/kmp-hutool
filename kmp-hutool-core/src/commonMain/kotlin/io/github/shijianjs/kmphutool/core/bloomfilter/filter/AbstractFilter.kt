package io.github.shijianjs.kmphutool.core.bloomfilter.filter

import io.github.shijianjs.kmphutool.core.bloomfilter.BloomFilter
import io.github.shijianjs.kmphutool.core.bloomfilter.bitMap.BitMap
import io.github.shijianjs.kmphutool.core.bloomfilter.bitMap.IntMap
import io.github.shijianjs.kmphutool.core.bloomfilter.bitMap.LongMap
import kotlin.math.abs

/**
 * Base Bloom filter that maps one hash into a [BitMap].
 *
 * Port of `cn.hutool.bloomfilter.filter.AbstractFilter`.
 *
 * @hutool cn.hutool.bloomfilter.filter.AbstractFilter
 */
abstract class AbstractFilter protected constructor(maxValue: Long, machineNum: Int) : BloomFilter {
    protected var size: Long = 0
        private set
    private lateinit var bm: BitMap

    init {
        init(maxValue, machineNum)
    }

    protected fun init(maxValue: Long, machineNum: Int) {
        require(maxValue in 1..Int.MAX_VALUE.toLong()) { "maxValue must be between 1 and ${Int.MAX_VALUE}" }
        size = maxValue
        val capacity = ((size + machineNum - 1) / machineNum).toInt()
        bm = when (machineNum) {
            BitMap.MACHINE32 -> IntMap(capacity)
            BitMap.MACHINE64 -> LongMap(capacity)
            else -> throw IllegalArgumentException("Error Machine number!")
        }
    }

    override fun contains(str: String): Boolean = bm.contains(abs(hash(str)))

    override fun add(str: String): Boolean {
        val hash = abs(hash(str))
        if (bm.contains(hash)) return false
        bm.add(hash)
        return true
    }

    /** The hash of [str] before it is folded into the bitmap. */
    abstract fun hash(str: String): Long

    companion object {
        val DEFAULT_MACHINE_NUM: Int = BitMap.MACHINE32
    }
}
