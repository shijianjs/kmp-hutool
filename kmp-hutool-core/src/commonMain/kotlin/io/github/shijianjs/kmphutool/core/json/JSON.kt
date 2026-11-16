package io.github.shijianjs.kmphutool.core.json

/**
 * Common supertype of [JSONObject] and [JSONArray].
 *
 * Port of `cn.hutool.json.JSON`. In this KMP port the value model is a plain Kotlin map/list, so
 * there is no reflective bean conversion; `toString` and [toJSONString] share the same writer.
 *
 * @hutool cn.hutool.json.JSON
 */
interface JSON : JSONString {
    fun toStringPretty(): String

    fun size(): Int

    fun isEmpty(): Boolean
}
