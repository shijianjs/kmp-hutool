package io.github.shijianjs.kmphutool.core

import io.github.shijianjs.kmphutool.core.map.multi.ListValueMap
import io.github.shijianjs.kmphutool.core.map.multi.RowKeyTable
import io.github.shijianjs.kmphutool.core.map.multi.SetValueMap
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class TableFamilyTest {
    @Test
    fun rowKeyTableStoresCells() {
        val table = RowKeyTable<String, String, Int>()
        table.put("r1", "c1", 1)
        table.put("r1", "c2", 2)
        table.put("r2", "c1", 3)

        assertEquals(3, table.size())
        assertEquals(2, table.getRow("r1")!!.size)
        assertEquals(1, table.get("r1", "c1"))
        assertTrue(table.containsRow("r2"))
        assertTrue(table.containsColumn("c2"))
        assertTrue(table.containsValue(3))
        assertEquals(setOf("c1", "c2"), table.columnKeySet())
        assertEquals(3, table.cellSet().size)
        assertEquals(listOf(1, 2, 3).sorted(), table.values().sorted())

        assertEquals(3, table.getColumn("c1")!!["r2"])
        table.getColumn("c1")!!["r3"] = 4
        assertEquals(4, table.get("r3", "c1"))

        assertEquals(2, table.remove("r1", "c2"))
        assertFalse(table.contains("r1", "c2"))
        assertTrue(table.columnMap().containsKey("c1"))
    }

    @Test
    fun multiValueMapsCollectValues() {
        val listMap = ListValueMap<String, Int>()
        listMap.putValue("a", 1)
        listMap.putValue("a", 2)
        assertEquals(listOf(1, 2), listMap["a"]!!.toList())
        assertEquals(2, listMap.get("a", 1))
        assertTrue(listMap.removeValue("a", 1))
        assertEquals(listOf(2), listMap["a"]!!.toList())

        val setMap = SetValueMap<String, Int>()
        setMap.putValue("a", 1)
        setMap.putValue("a", 1)
        assertEquals(setOf(1), setMap["a"]!!.toSet())
    }
}

