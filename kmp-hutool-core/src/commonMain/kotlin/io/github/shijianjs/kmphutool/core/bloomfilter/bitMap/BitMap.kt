package io.github.shijianjs.kmphutool.core.bloomfilter.bitMap

/**
 * Maps a non-negative bit index into a machine-word array.
 *
 * Port of `cn.hutool.bloomfilter.bitMap.BitMap`.
 *
 * @hutool cn.hutool.bloomfilter.bitMap.BitMap
 */
interface BitMap {
    fun add(i: Long)

    fun contains(i: Long): Boolean

    fun remove(i: Long)

    companion object {
        const val MACHINE32 = 32
        const val MACHINE64 = 64
    }
}
