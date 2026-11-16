package io.github.shijianjs.kmphutool.core

import io.github.shijianjs.kmphutool.core.lang.hash.MurmurHash
import io.github.shijianjs.kmphutool.core.util.HashUtil
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class HashUtilTest {
    @Test
    fun classicHashesAreDeterministic() {
        val text = "kmp-hutool"
        val bkdr = HashUtil.bkdrHash(text)
        assertEquals(bkdr, HashUtil.bkdrHash(text))
        assertTrue(bkdr >= 0)
        assertEquals(0, HashUtil.tianlHash(""))
        assertEquals(96354, HashUtil.javaDefaultHash("abc"))
    }

    @Test
    fun murmurDelegates() {
        val bytes = "abc".encodeToByteArray()
        assertEquals(MurmurHash.hash32(bytes), HashUtil.murmur32(bytes))
        assertEquals(MurmurHash.hash64(bytes), HashUtil.murmur64(bytes))
        assertTrue(HashUtil.murmur128(bytes).contentEquals(MurmurHash.hash128(bytes)))
    }
}
