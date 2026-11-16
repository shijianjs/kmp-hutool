package io.github.shijianjs.kmphutool.core

import io.github.shijianjs.kmphutool.core.lang.hash.MurmurHash
import io.github.shijianjs.kmphutool.core.map.AbsEntry
import io.github.shijianjs.kmphutool.core.map.BiMap
import io.github.shijianjs.kmphutool.core.map.TolerantMap
import io.github.shijianjs.kmphutool.core.text.Simhash
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class MapHashTest {
    @Test
    fun tolerantMapFallsBack() {
        val map = TolerantMap(linkedMapOf("a" to 1), 0)
        assertEquals(1, map["a"])
        assertEquals(0, map["missing"])
        map["b"] = 2
        assertEquals(2, map["b"])
        assertEquals(2, map.getRaw().size)
    }

    @Test
    fun biMapKeepsInverse() {
        val map = BiMap<String, Int>()
        map["one"] = 1
        map["two"] = 2
        assertEquals("one", map.getKey(1))
        map["uno"] = 1
        assertEquals("uno", map.getKey(1))
        map.remove("uno")
        assertTrue(map.getInverse().isEmpty() || map.getInverse().containsKey(2))
        map.clear()
        assertTrue(map.isEmpty())
    }

    @Test
    fun entryContract() {
        val entry = object : AbsEntry<String, Int>() {
            override val key: String = "k"
            override val value: Int = 2
        }
        assertEquals("k=2", entry.toString())
        assertEquals("k".hashCode() xor 2.hashCode(), entry.hashCode())
        assertTrue(entry == mapOf("k" to 2).entries.first())
        assertFailsWith<UnsupportedOperationException> { entry.setValue(3) }
    }

    @Test
    fun murmurHashMatchesKnownVectors() {
        // ASCII vectors cross-checked against the MurmurHash3 reference implementation.
        assertEquals(613153351, MurmurHash.hash32("hello"))
        assertEquals(-1277324294, MurmurHash.hash32("abc"))
        assertEquals(0, MurmurHash.hash32(""))
        // Vectors from Hutool's own MurmurHashTest, so the port keeps upstream results.
        assertEquals(-1898877446, MurmurHash.hash32("你"))
        assertEquals(337357348, MurmurHash.hash32("你好"))
        assertEquals(-1349759534971957051L, MurmurHash.hash64("你"))
        assertEquals(-7563732748897304996L, MurmurHash.hash64("你好"))
        assertEquals(2, MurmurHash.hash128("hello").size)
        // MurmurHash3 x64 128-bit reference halves for "hello" (seed 0).
        val halves = MurmurHash.hash128("hello")
        assertEquals(-3758069500696749310L, halves[0])
        assertEquals(6565844092913065241L, halves[1])
    }

    @Test
    fun simhashDetectsNearDuplicates() {
        val simhash = Simhash()
        val base = listOf("hutool", "kotlin", "multiplatform", "text", "hash")
        val near = listOf("hutool", "kotlin", "multiplatform", "text", "hash", "extra")
        val other = listOf("banana", "orange", "apple", "pear", "grape")

        val baseHash = simhash.hash(base)
        simhash.store(baseHash)
        assertTrue(simhash.equals(base))
        assertTrue(simhash.hamming(baseHash, simhash.hash(near)) < 20)
        assertFalse(simhash.equals(other))
    }
}
