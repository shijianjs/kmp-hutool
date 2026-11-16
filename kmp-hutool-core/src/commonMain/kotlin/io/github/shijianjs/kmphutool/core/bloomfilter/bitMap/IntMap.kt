package io.github.shijianjs.kmphutool.core.bloomfilter.bitMap

/**
 * A 32-bit-word bitmap.
 *
 * Port of `cn.hutool.bloomfilter.bitMap.IntMap`. The no-argument constructor of upstream preallocates
 * 93,750,000 ints (~375 MB); that size is not offered here, pass the capacity explicitly.
 *
 * @hutool cn.hutool.bloomfilter.bitMap.IntMap
 */
class IntMap(size: Int) : BitMap {
    private val ints = IntArray(size)

    override fun add(i: Long) {
        val r = (i / BitMap.MACHINE32).toInt()
        val c = (i and (BitMap.MACHINE32 - 1).toLong()).toInt()
        ints[r] = ints[r] or (1 shl c)
    }

    override fun contains(i: Long): Boolean {
        val r = (i / BitMap.MACHINE32).toInt()
        val c = (i and (BitMap.MACHINE32 - 1).toLong()).toInt()
        return ((ints[r] ushr c) and 1) == 1
    }

    override fun remove(i: Long) {
        val r = (i / BitMap.MACHINE32).toInt()
        val c = (i and (BitMap.MACHINE32 - 1).toLong()).toInt()
        ints[r] = ints[r] and (1 shl c).inv()
    }
}
