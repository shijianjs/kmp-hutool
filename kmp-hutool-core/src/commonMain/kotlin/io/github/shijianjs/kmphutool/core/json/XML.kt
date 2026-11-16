package io.github.shijianjs.kmphutool.core.json

import io.github.shijianjs.kmphutool.core.json.xml.JSONXMLParser
import io.github.shijianjs.kmphutool.core.json.xml.JSONXMLSerializer
import io.github.shijianjs.kmphutool.core.json.xml.ParseConfig

/**
 * Static facade for XML ↔ JSON conversion.
 *
 * Port of `cn.hutool.json.XML`.
 *
 * @hutool cn.hutool.json.XML
 */
object XML {
    const val AMP: Char = '&'
    const val APOS: Char = '\''
    const val BANG: Char = '!'
    const val EQ: Char = '='
    const val GT: Char = '>'
    const val LT: Char = '<'
    const val QUEST: Char = '?'
    const val QUOT: Char = '"'
    const val SLASH: Char = '/'

    fun toJSONObject(string: String): JSONObject = toJSONObject(string, false)

    fun toJSONObject(string: String, keepStrings: Boolean): JSONObject =
        toJSONObject(JSONObject(), string, keepStrings)

    fun toJSONObject(string: String, parseConfig: ParseConfig): JSONObject =
        toJSONObject(JSONObject(), string, parseConfig)

    fun toJSONObject(jo: JSONObject, xmlStr: String, keepStrings: Boolean): JSONObject {
        JSONXMLParser.parseJSONObject(jo, xmlStr, keepStrings)
        return jo
    }

    fun toJSONObject(jo: JSONObject, xmlStr: String, parseConfig: ParseConfig): JSONObject {
        JSONXMLParser.parseJSONObject(jo, xmlStr, parseConfig)
        return jo
    }

    fun toXml(value: Any?): String? = toXml(value, null)

    fun toXml(value: Any?, tagName: String?): String? = toXml(value, tagName, "content")

    fun toXml(value: Any?, tagName: String?, vararg contentKeys: String): String? =
        JSONXMLSerializer.toXml(value, tagName, *contentKeys)
}
