package io.github.shijianjs.kmphutool.core.json

import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonNull
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.booleanOrNull
import kotlinx.serialization.json.doubleOrNull
import kotlinx.serialization.json.longOrNull


/**
 * Bridges Hutool's dynamic JSON values to `kotlinx.serialization` elements.
 *
 * This is an internal implementation detail: the public API stays `JSONObject` / `JSONArray`, and the
 * parser/serializer do the conversion so consumers do not need the serialization compiler plugin.
 */
internal object JSONConvert {
    val parser = Json {
        isLenient = true

        allowTrailingComma = true
    }

    private val writer = Json {
        prettyPrint = false
        encodeDefaults = true
    }

    private val prettyWriter = Json {
        prettyPrint = true
        prettyPrintIndent = "  "
        encodeDefaults = true
    }

    fun parse(source: String): Any? =
        fromElement(parser.parseToJsonElement(source))

    fun stringify(value: Any?, pretty: Boolean = false): String {
        val element = toElement(value)
        return (if (pretty) prettyWriter else writer)
            .encodeToString(JsonElement.serializer(), element)
    }

    fun toElement(value: Any?): JsonElement = when (value) {
        null, JSONNull -> JsonNull
        is JsonElement -> value
        is JSONObject -> JsonObject(value.entries.associate { it.key to toElement(it.value) })
        is JSONArray -> JsonArray(value.map { toElement(it) })
        is Map<*, *> -> JsonObject(
            value.entries.associate { (it.key?.toString() ?: "null") to toElement(it.value) },
        )
        is Iterable<*> -> JsonArray(value.map { toElement(it) })
        is Boolean -> JsonPrimitive(value)
        is Number -> JsonPrimitive(value)
        is String, is Char -> JsonPrimitive(value.toString())
        else -> JsonPrimitive(value.toString())
    }

    fun fromElement(element: JsonElement): Any? = when (element) {
        is JsonNull -> JSONNull
        is JsonObject -> JSONObject(element.entries.associate { it.key to fromElement(it.value) })
        is JsonArray -> JSONArray(element.map { fromElement(it) })
        is JsonPrimitive -> primitive(element)
    }

    private fun primitive(value: JsonPrimitive): Any {
        if (value.isString) return value.content
        value.booleanOrNull?.let { return it }
        value.longOrNull?.let { return it }
        value.doubleOrNull?.let { return it }
        return value.content
    }
}
