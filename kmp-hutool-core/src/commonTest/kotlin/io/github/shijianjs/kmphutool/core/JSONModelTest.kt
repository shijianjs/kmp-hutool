package io.github.shijianjs.kmphutool.core

import io.github.shijianjs.kmphutool.core.json.JSONArray
import io.github.shijianjs.kmphutool.core.json.JSONObject
import io.github.shijianjs.kmphutool.core.json.JSONObjectIter
import io.github.shijianjs.kmphutool.core.json.JSONParser
import io.github.shijianjs.kmphutool.core.json.JSONTokener
import io.github.shijianjs.kmphutool.core.json.serialize.JSONArraySerializer
import io.github.shijianjs.kmphutool.core.json.serialize.JSONObjectSerializer
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class JSONModelTest {
    @Test
    fun parsesObjectAndBuildsTypedValues() {
        val json = JSONObject("""{"name":"Ada","age":36,"active":true,"tags":["kotlin","kmp"]}""")
        assertEquals("Ada", json.getStr("name"))
        assertEquals(36, json.getInt("age"))
        assertTrue(json.getBool("active") ?: false)
        assertEquals("kmp", json.getJSONArray("tags")?.getStr(1))
        assertFalse(json.isEmpty())
        assertEquals(4, json.size())
    }

    @Test
    fun parsesArrayAndNestedObjects() {
        val array = JSONArray("""[{"id":1},{"id":2}]""")
        assertEquals(2, array.size())
        assertEquals(1, array.getJSONObject(0)?.getInt("id"))
        assertEquals(2, array.getJSONObject(1)?.getInt("id"))

        val iterable = JSONObjectIter(array.iterator())
        assertEquals(listOf(1, 2), iterable.map { it.getInt("id") })
    }

    @Test
    fun writesCompactAndPrettyJson() {
        val json = JSONObject(mapOf("a" to 1, "b" to JSONArray(listOf("x", null))))
        assertEquals("""{"a":1,"b":["x",null]}""", json.toJSONString())
        assertTrue(json.toStringPretty().contains("\n  \"a\": 1"))
        assertEquals(json.toJSONString(), json.toString())
    }

    @Test
    fun parserAndSerializersShareTheModel() {
        val parsed = JSONParser.of(JSONTokener("""{"v":1.5}""")).parse() as JSONObject
        assertEquals(1.5, parsed.getDouble("v"))

        val objectSerializer = JSONObjectSerializer<Map<String, Int>> { target, value ->
            value.forEach { (key, item) -> target.put(key, item) }
        }
        val target = JSONObject()
        objectSerializer.serialize(target, mapOf("x" to 7))
        assertEquals(7, target.getInt("x"))

        val arraySerializer = JSONArraySerializer<List<String>> { target, value -> target.addAll(value) }
        assertEquals("b", arraySerializer.let { serializer ->
            val target = JSONArray()
            serializer.serialize(target, listOf("a", "b"))
            target.getStr(1)
        })
    }
}
