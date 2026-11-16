package io.github.shijianjs.kmphutool.core.json

/**
 * Base class for objects that carry their own JSON representation.
 *
 * Port of `cn.hutool.json.JSONSupport`. Upstream copies fields into the bean by reflection in
 * `parse(JSON)`; common Kotlin has no reflection, so this port stores the parsed JSON and subclasses
 * read it through [toJSON]. Use `kotlinx.serialization` for typed bean decoding.
 *
 * @hutool cn.hutool.json.JSONSupport
 */
open class JSONSupport : JSONString, JSONBeanParser<JSON> {
    protected var json: JSON? = null

    open fun parse(jsonString: String) {
        parse(JSONObject(jsonString))
    }

    override fun parse(value: JSON) {
        json = value
    }

    fun toJSON(): JSONObject = when (val value = json) {
        is JSONObject -> value
        is JSONArray -> JSONObject(mapOf("array" to value))
        null -> JSONObject()
        else -> JSONObject()
    }

    override fun toJSONString(): String = toJSON().toJSONString()

    fun toPrettyString(): String = toJSON().toStringPretty()

    override fun toString(): String = toJSONString()
}
