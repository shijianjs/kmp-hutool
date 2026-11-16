package io.github.shijianjs.kmphutool.core.bloomfilter.bitMap

/**
 * A 64-bit-word bitmap.
 *
 * Port of `cn.hutool.bloomfilter.bitMap.LongMap`. The upstream no-argument constructor preallocates
 * 93,750,000 longs; that size is not offered here, pass the capacity explicitly.
 *
 * @hutool cn.hutool.bloomfilter.bitMap.LongMap
 */
class LongMap(size: Int) : BitMap {
    private val longs = LongArray(size)

    override fun add(i: Long) {
        val r = (i / BitMap.MACHINE64).toInt()
        val c = i and 63L
        longs[r] = longs[r] or (1L shl c.toInt())
    }

    override fun contains(i: Long): Boolean {
        val r = (i / BitMap.MACHINE64).toInt()
        val c = i and 63L
        return ((longs[r] ushr c.toInt()) and 1L) == 1L
    }

    override fun remove(i: Long) {
        val r = (i / BitMap.MACHINE64).toInt()
        val c = i and 63L
        longs[r] = longs[r] and (1L shl c.toInt()).inv()
    }
}
