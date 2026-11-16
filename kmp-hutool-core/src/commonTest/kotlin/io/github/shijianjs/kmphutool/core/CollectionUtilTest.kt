package io.github.shijianjs.kmphutool.core

import io.github.shijianjs.kmphutool.core.collection.CollUtil
import io.github.shijianjs.kmphutool.core.collection.IterUtil
import io.github.shijianjs.kmphutool.core.collection.ListUtil
import io.github.shijianjs.kmphutool.core.map.MapUtil
import io.github.shijianjs.kmphutool.core.util.ArrayUtil
import io.github.shijianjs.kmphutool.core.util.ObjectUtil
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertTrue

class CollectionUtilTest {
    @Test
    fun emptinessAndMembership() {
        assertTrue(CollUtil.isEmpty(null))
        assertTrue(CollUtil.isEmpty(emptyList<String>()))
        assertFalse(CollUtil.isEmpty(listOf(1)))
        assertTrue(CollUtil.contains(listOf("a", "b"), "b"))
        assertFalse(CollUtil.contains(listOf("a"), "c"))
        assertTrue(CollUtil.containsAll(listOf("a", "b"), "a", "b"))
        assertFalse(CollUtil.containsAll(listOf("a"), "a", "b"))
        assertTrue(CollUtil.containsAny(listOf("a"), "b", "a"))
        assertTrue(CollUtil.hasNull(listOf(null, 1)))
    }

    @Test
    fun mutationHelpers() {
        val list = mutableListOf("a")
        assertTrue(CollUtil.addAll(list, "b", null))
        assertEquals(2, list.size)
        assertTrue(CollUtil.addIfAbsent(list, "a").not())
        assertTrue(CollUtil.addIfAbsent(list, "c"))
        assertEquals(listOf("a", "b", "c"), list)
    }

    @Test
    fun reads() {
        assertEquals("b", CollUtil.get(listOf("a", "b"), 1))
        assertNull(CollUtil.get(listOf("a"), 5))
        assertEquals("fallback", CollUtil.get(listOf("a"), 5, "fallback"))
        assertEquals("a", CollUtil.getFirst(listOf("a", "b")))
        assertEquals("b", CollUtil.getLast(listOf("a", "b")))
    }

    @Test
    fun setOperations() {
        val a = listOf(1, 2, 3)
        val b = listOf(2, 3, 4)
        assertEquals(listOf(1, 2, 3, 4), CollUtil.union(a, b))
        assertEquals(listOf(1, 2, 3, 4), CollUtil.unionDistinct(a, b))
        assertEquals(listOf(2, 3), CollUtil.intersection(a, b))
        assertEquals(listOf(1), CollUtil.subtract(a, b))
        assertEquals(listOf(1, 4), CollUtil.disjunction(a, b))
    }

    @Test
    fun transforms() {
        assertEquals(listOf(2), CollUtil.filter(listOf(1, 2, 3)) { it % 2 == 0 })
        assertEquals(mapOf("a" to 2, "b" to 1), CollUtil.countMap(listOf("a", "a", "b")))
        assertEquals(mapOf(true to listOf(2), false to listOf(1, 3)), CollUtil.group(listOf(1, 2, 3)) { it % 2 == 0 })
        assertEquals(listOf(2, 3), CollUtil.sub(listOf(1, 2, 3, 4), 1, 3))
        assertEquals(listOf(3, 4), CollUtil.page(2, 2, listOf(1, 2, 3, 4)))
        assertEquals(listOf(listOf(1, 2), listOf(3, 4)), CollUtil.split(listOf(1, 2, 3, 4), 2))
        assertEquals("1,2,3", CollUtil.join(listOf(1, 2, 3), ","))
        assertEquals(3, CollUtil.max(listOf(1, 3, 2)))
        assertEquals(1, CollUtil.min(listOf(1, 3, 2)))
        assertEquals(listOf("1a", "2b"), CollUtil.zip(listOf(1, 2), listOf("a", "b")) { num, text -> "$num$text" })
        assertEquals(listOf(0, 2), CollUtil.indexOfAll(listOf(1, 2, 1)) { it == 1 })
        assertEquals(listOf(1, 2), CollUtil.reverseNew(listOf(2, 1)))
    }

    @Test
    fun removalHelpers() {
        val list = mutableListOf("a", null, "", " ", "b")
        CollUtil.removeNull(list)
        CollUtil.removeEmpty(list)
        CollUtil.removeBlank(list)
        assertEquals(listOf<String?>("a", "b"), list)
    }

    @Test
    fun listUtil() {
        assertEquals(listOf(listOf(1, 2), listOf(3, 4), listOf(5)), ListUtil.partition(listOf(1, 2, 3, 4, 5), 2))
        assertEquals(listOf(listOf(1, 2, 3), listOf(4, 5)), ListUtil.splitAvg(listOf(1, 2, 3, 4, 5), 2))
        assertEquals(listOf(3, 4), ListUtil.page(2, 2, listOf(1, 2, 3, 4)))
        assertEquals(listOf(2, 3), ListUtil.sub(listOf(1, 2, 3, 4), 1, 3))
        assertEquals(listOf(2, 1, 3), ListUtil.move(mutableListOf(1, 2, 3), 0, 1)!!)
        assertEquals(listOf(2, 1, 3), ListUtil.swapTo(mutableListOf(1, 2, 3), 2, 0)!!)
        assertEquals(listOf(1, 2, 3, 4), ListUtil.setOrAppend(mutableListOf(1, 2, 3), 3, 4))
    }

    @Test
    fun iterUtil() {
        assertEquals(listOf(1, 2), IterUtil.toList(listOf(1, 2) as Iterable<Int>))
        assertEquals("1,2", IterUtil.join(listOf(1, 2), ","))
        assertEquals(1, IterUtil.get(listOf(1, 2).iterator(), 0))
        assertEquals(2, IterUtil.size(listOf(1, 2)))
        assertTrue(IterUtil.isEmpty(listOf<Int>()))
        assertTrue(IterUtil.hasNull(listOf<Int?>(1, null)))
    }

    @Test
    fun mapUtil() {
        val map = MapUtil.ofPairs("a", 1, "b", "2")
        assertEquals(1, map["a"])
        assertEquals(1, MapUtil.getInt(map, "a"))
        assertEquals(2, MapUtil.getInt(map, "b"))
        assertEquals(5, MapUtil.getInt(map, "missing", 5))
        assertEquals("2", MapUtil.getStr(map, "b"))
        assertEquals("a=1&b=2", MapUtil.join(map, "&"))
        assertEquals(
            mutableMapOf("a" to 2),
            MapUtil.map(mapOf("a" to 1)) { it + 1 },
        )
        assertEquals(mutableMapOf(1 to "a", 2 to "b"), MapUtil.inverse(mapOf("a" to 1, "b" to 2)))
        assertEquals(listOf("a", "b"), MapUtil.keyList(map))
        assertEquals(1, MapUtil.getAny(map))
    }

    @Test
    fun arrayUtil() {
        assertEquals(listOf(2, 3), ArrayUtil.sub(arrayOf(1, 2, 3, 4), 1, 3).toList())
        assertEquals(listOf("a", "b"), ArrayUtil.append(arrayOf("a"), "b").toList())
        assertEquals(listOf("a", "b", "c"), ArrayUtil.addAll(arrayOf("a"), arrayOf("b", "c")).toList())
        assertEquals(listOf("a", "x", "b"), ArrayUtil.insert(arrayOf("a", "b"), 1, "x").toList())
        assertEquals(listOf("b"), ArrayUtil.remove(arrayOf("a", "b"), 0).toList())
        assertEquals(listOf("a", "b"), ArrayUtil.removeEle(arrayOf("a", "b", "c"), "c").toList())
        assertEquals(listOf("a"), ArrayUtil.distinct(arrayOf("a", "a")).toList())
        assertEquals(listOf(1, 2), ArrayUtil.removeNull(arrayOf(1, null, 2)).toList())
        assertTrue(ArrayUtil.contains(arrayOf("a"), "a"))
        assertEquals(1, ArrayUtil.indexOf(arrayOf("a", "b"), "b"))
    }

    @Test
    fun objectUtil() {
        assertTrue(ObjectUtil.isEmpty(null))
        assertTrue(ObjectUtil.isEmpty(""))
        assertTrue(ObjectUtil.isEmpty(emptyList<Int>()))
        assertTrue(ObjectUtil.isEmpty(Array(0) { 0 }))
        assertFalse(ObjectUtil.isEmpty(" "))
        assertEquals("d", ObjectUtil.defaultIfNull(null, "d"))
        assertEquals("v", ObjectUtil.defaultIfNull("v", "d"))
        assertEquals("v", ObjectUtil.defaultIfEmpty("", "v"))
        assertEquals(-1, ObjectUtil.compare(1, 2))
        assertEquals(1, ObjectUtil.compare(null, 1, nullGreater = true))
        assertTrue(ObjectUtil.isAllEmpty(null, ""))
        assertEquals(2, ObjectUtil.emptyCount(null, 1, ""))
    }
}
