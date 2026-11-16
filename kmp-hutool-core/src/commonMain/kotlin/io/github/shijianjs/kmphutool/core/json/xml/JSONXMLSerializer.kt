package io.github.shijianjs.kmphutool.core.json.xml

import io.github.shijianjs.kmphutool.core.json.JSONArray
import io.github.shijianjs.kmphutool.core.json.JSONObject
import io.github.shijianjs.kmphutool.core.util.EscapeUtil

/**
 * Converts JSON values into XML text.
 *
 * Port of `cn.hutool.json.xml.JSONXMLSerializer`. A JSON object becomes nested tags, a JSON array
 * repeats the current tag (or `<array>` when no tag is given), and keys listed in `contentKeys` are
 * written as escaped element text.
 *
 * @hutool cn.hutool.json.xml.JSONXMLSerializer
 */
object JSONXMLSerializer {
    fun toXml(value: Any?): String? = toXml(value, null)

    fun toXml(value: Any?, tagName: String?): String? = toXml(value, tagName, *arrayOf("content"))

    fun toXml(value: Any?, tagName: String?, vararg contentKeys: String): String? {
        if (value == null) return null
        val sb = StringBuilder()

        if (value is JSONObject) {
            appendTag(sb, tagName, false)
            for ((key, item) in value.entries) {
                when {
                    key in contentKeys -> {
                        if (item is JSONArray) {
                            item.forEachIndexed { index, element ->
                                if (index > 0) sb.append('\n')
                                sb.append(EscapeUtil.escapeXml(element?.toString() ?: "null"))
                            }
                        } else {
                            sb.append(EscapeUtil.escapeXml(item?.toString() ?: "null"))
                        }
                    }
                    item == null || item.toString().isEmpty() -> sb.append(wrapWithTag(null, key))
                    item is JSONArray -> item.forEach { element ->
                        if (element is JSONArray) {
                            sb.append(wrapWithTag(toXml(element, null, *contentKeys), key))
                        } else {
                            sb.append(toXml(element, key, *contentKeys) ?: "")
                        }
                    }
                    else -> sb.append(toXml(item, key, *contentKeys) ?: "")
                }
            }
            appendTag(sb, tagName, true)
            return sb.toString()
        }

        if (value is JSONArray) {
            value.forEach { element ->
                sb.append(toXml(element, tagName ?: "array", *contentKeys) ?: "")
            }
            return sb.toString()
        }

        return wrapWithTag(EscapeUtil.escapeXml(value.toString()), tagName)
    }

    private fun appendTag(sb: StringBuilder, tagName: String?, end: Boolean) {
        if (!tagName.isNullOrBlank()) {
            sb.append('<')
            if (end) sb.append('/')
            sb.append(tagName).append('>')
        }
    }

    private fun wrapWithTag(content: String?, tagName: String?): String = when {
        tagName.isNullOrBlank() -> "\"${content ?: ""}\""
        content.isNullOrEmpty() -> "<$tagName/>"
        else -> "<$tagName>$content</$tagName>"
    }
}
