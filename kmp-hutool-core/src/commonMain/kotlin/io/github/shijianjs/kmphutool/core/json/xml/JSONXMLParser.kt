package io.github.shijianjs.kmphutool.core.json.xml

import io.github.shijianjs.kmphutool.core.json.InternalJSONUtil
import io.github.shijianjs.kmphutool.core.json.JSONException
import io.github.shijianjs.kmphutool.core.json.JSONObject
import io.github.shijianjs.kmphutool.core.json.XML
import io.github.shijianjs.kmphutool.core.json.XMLTokener

/**
 * Converts XML text into a [JSONObject].
 *
 * Port of `cn.hutool.json.xml.JSONXMLParser`. Attributes and child elements both become keys; a
 * repeated key becomes a [io.github.shijianjs.kmphutool.core.json.JSONArray], and text goes under
 * `content`.
 *
 * @hutool cn.hutool.json.xml.JSONXMLParser
 */
object JSONXMLParser {
    fun parseJSONObject(jo: JSONObject, xmlStr: String, keepStrings: Boolean) {
        parseJSONObject(jo, xmlStr, ParseConfig.of().apply { this.keepStrings = keepStrings })
    }

    fun parseJSONObject(jo: JSONObject, xmlStr: String, parseConfig: ParseConfig) {
        val tokener = XMLTokener(xmlStr)
        while (tokener.more() && tokener.skipPast("<")) {
            parse(tokener, jo, null, parseConfig, 0)
        }
    }

    private fun parse(
        x: XMLTokener,
        context: JSONObject,
        name: String?,
        parseConfig: ParseConfig,
        depth: Int,
    ): Boolean {
        var token: Any = x.nextToken()
        when (token) {
            XML.BANG -> {
                val c = x.next()
                if (c == '-') {
                    if (x.next() == '-') {
                        x.skipPast("-->")
                        return false
                    }
                    x.back()
                } else if (c == '[') {
                    token = x.nextToken()
                    if (token == "CDATA" && x.next() == '[') {
                        val text = x.nextCDATA()
                        if (text.isNotEmpty()) context.accumulate("content", text)
                        return false
                    }
                    throw x.syntaxError("Expected 'CDATA['")
                }
                var nesting = 1
                do {
                    token = x.nextMeta() ?: throw x.syntaxError("Missing '>' after '<!'.")
                    if (token == XML.LT) nesting++ else if (token == XML.GT) nesting--
                } while (nesting > 0)
                return false
            }
            XML.QUEST -> {
                x.skipPast("?>")
                return false
            }
            XML.SLASH -> {
                token = x.nextToken()
                if (name == null) throw x.syntaxError("Mismatched close tag $token")
                if (token != name) throw x.syntaxError("Mismatched $name and $token")
                if (x.nextToken() != XML.GT) throw x.syntaxError("Misshaped close tag")
                return true
            }
            is Char -> throw x.syntaxError("Misshaped tag")
        }

        val tagName = token as? String ?: throw x.syntaxError("Misshaped tag")
        var current: Any? = null
        val jsonobject = JSONObject()
        while (true) {
            if (current == null) current = x.nextToken()
            when (val value = current) {
                is String -> {
                    val attributeName = value
                    current = x.nextToken()
                    if (current == XML.EQ) {
                        current = x.nextToken()
                        if (current !is String) throw x.syntaxError("Missing value")
                        jsonobject.accumulate(
                            attributeName,
                            if (parseConfig.keepStrings) current else InternalJSONUtil.stringToValue(current),
                        )
                        current = null
                    } else {
                        jsonobject.accumulate(attributeName, "")
                    }
                }
                XML.SLASH -> {
                    if (x.nextToken() != XML.GT) throw x.syntaxError("Misshaped tag")
                    if (jsonobject.size() > 0) context.accumulate(tagName, jsonobject)
                    else context.accumulate(tagName, "")
                    return false
                }
                XML.GT -> {
                    while (true) {
                        val content = x.nextContent()
                        if (content == null) {
                            throw x.syntaxError("Unclosed tag $tagName")
                        } else if (content is String) {
                            if (content.isNotEmpty()) {
                                jsonobject.accumulate(
                                    "content",
                                    if (parseConfig.keepStrings) content else InternalJSONUtil.stringToValue(content),
                                )
                            }
                        } else if (content == XML.LT) {
                            if (parseConfig.maxNestingDepth > -1 && depth >= parseConfig.maxNestingDepth) {
                                throw x.syntaxError("Maximum nesting depth of ${parseConfig.maxNestingDepth} reached")
                            }
                            if (parse(x, jsonobject, tagName, parseConfig, depth + 1)) {
                                when {
                                    jsonobject.isEmpty() -> context.accumulate(tagName, "")
                                    jsonobject.size() == 1 && jsonobject["content"] != null ->
                                        context.accumulate(tagName, jsonobject["content"])
                                    else -> context.accumulate(tagName, jsonobject)
                                }
                                return false
                            }
                        }
                    }
                }
                else -> throw x.syntaxError("Misshaped tag")
            }
        }
    }
}
