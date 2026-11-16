package io.github.shijianjs.kmphutool.core

import io.github.shijianjs.kmphutool.core.lang.hash.Hash128
import io.github.shijianjs.kmphutool.core.lang.hash.Hash32
import io.github.shijianjs.kmphutool.core.lang.hash.Hash64
import io.github.shijianjs.kmphutool.core.lang.hash.Number128
import kotlin.test.Test
import kotlin.test.assertEquals

class HashTypesTest {
    @Test
    fun hashInterfacesBridgeToCommonType() {
        val h32 = Hash32<String> { it.length }
        assertEquals(3, h32.hash("abc"))
        val h64 = Hash64<String> { it.length.toLong() }
        assertEquals(3L, h64.hash("abc"))
        val h128 = Hash128<String> { Number128(it.length.toLong(), 1L) }
        assertEquals(Number128(3L, 1L), h128.hash("abc"))
    }

    @Test
    fun number128ExposesBothHalves() {
        val n = Number128(5L, 7L)
        assertEquals(longArrayOf(5L, 7L).toList(), n.getLongArray().toList())
        assertEquals(5L, n.longValue())
        n.highValue = 9L
        assertEquals(9L, n.highValue)
    }
}
