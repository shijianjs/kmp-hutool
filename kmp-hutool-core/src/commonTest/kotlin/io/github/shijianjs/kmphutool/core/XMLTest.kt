package io.github.shijianjs.kmphutool.core

import io.github.shijianjs.kmphutool.core.json.JSONArray
import io.github.shijianjs.kmphutool.core.json.JSONObject
import io.github.shijianjs.kmphutool.core.json.XML
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class XMLTest {
    @Test
    fun parsesAttributesAndRepeatedElements() {
        val json = XML.toJSONObject(
            """<root id="1"><item>text</item><item>2</item><empty/></root>""",
        )
        val root = json.getJSONObject("root")!!
        assertEquals(1L, root.getLong("id"))
        assertEquals("", root.getStr("empty"))
        val items = root["item"] as JSONArray
        assertEquals("text", items.getStr(0))
        assertEquals(2L, items.getLong(1))
    }

    @Test
    fun keepsStringsWhenRequested() {
        val json = XML.toJSONObject("""<root id="1"><n>true</n></root>""", keepStrings = true)
        assertEquals("1", json.getJSONObject("root")?.getStr("id"))
    }

    @Test
    fun writesObjectAsXml() {
        val json = JSONObject(mapOf("root" to JSONObject(mapOf("name" to "Ada", "content" to "hi"))))
        val xml = XML.toXml(json)
        assertTrue(xml!!.contains("<root>"))
        assertTrue(xml.contains("<name>Ada</name>"))
        assertTrue(xml.contains("hi"))
    }
}
