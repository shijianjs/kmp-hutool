package io.github.shijianjs.kmphutool.core

import io.github.shijianjs.kmphutool.core.bloomfilter.BitMapBloomFilter
import io.github.shijianjs.kmphutool.core.bloomfilter.BloomFilterUtil
import io.github.shijianjs.kmphutool.core.bloomfilter.filter.DefaultFilter
import io.github.shijianjs.kmphutool.core.bloomfilter.filter.SDBMFilter
import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class BloomFilterTest {
    @Test
    fun addsAndQueriesAString() {
        val filter = BitMapBloomFilter(5)
        assertTrue(filter.add("kmp-hutool"))
        assertTrue(filter.contains("kmp-hutool"))
        assertFalse(filter.add("kmp-hutool"))
    }

    @Test
    fun individualFiltersAndFactoryWork() {
        val filter = DefaultFilter(1_000)
        assertTrue(filter.add("a"))
        val sdbm = SDBMFilter(1_000)
        assertTrue(sdbm.add("b"))
        assertTrue(BloomFilterUtil.createBitMap(5).add("c"))
    }
}
