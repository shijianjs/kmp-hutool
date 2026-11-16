package io.github.shijianjs.kmphutool.core

import io.github.shijianjs.kmphutool.core.setting.GroupedMap
import io.github.shijianjs.kmphutool.core.setting.GroupedSet
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class GroupedSettingTest {
    @Test
    fun groupedMapReadsByGroup() {
        val map = GroupedMap()
        map.put("a", "k", "v1")
        map.put(" a ", "k2", "v2")
        assertEquals("v1", map.get("a", "k"))
        assertEquals(2, map.size)
        assertTrue(map.isEmpty("b"))
        map.clear("a")
        assertTrue(map.isEmpty())
    }


    @Test
    fun settingExceptionFormatsTemplate() {
        val error = io.github.shijianjs.kmphutool.core.setting.SettingRuntimeException("bad {}", "file")
        assertEquals("bad file", error.message)
    }

    @Test
    fun groupedSetParsesSections() {
        val set = GroupedSet()
        set.load("[g1]\naaa\n\\#bbb\n# comment\n[g2]\nccc\n[g1]\nddd")
        assertEquals(setOf("aaa", "#bbb", "ddd"), set.getValues("g1"))
        assertEquals(setOf("ccc"), set.getValues("g2"))
        assertTrue(set.contains("g1", "aaa", "ddd"))
        assertFalse(set.contains("missing", "aaa"))
    }
}
