package io.github.shijianjs.kmphutool.core.util

/**
 * Escaping and unescaping for HTML/XML text and for arbitrary characters.
 *
 * Port of `cn.hutool.core.util.EscapeUtil`. The `Filter`-taking overload of the Java class is not
 * ported: Kotlin passes a lambda instead, so [escapeHtml4] keeps a single implementation.
 *
 * @hutool cn.hutool.core.util.EscapeUtil
 */
object EscapeUtil {
    private const val NBSP = "\u00A0"

    /** Escapes `&`, `<`, `>`, `"` and `'` as HTML 4 entities, plus `\u00A0` as `&nbsp;`. */
    fun escapeHtml4(html: CharSequence?): String? {
        if (html == null) return null
        val result = StringBuilder(html.length + 16)
        for (ch in html) {
            when (ch) {
                '&' -> result.append("&amp;")
                '<' -> result.append("&lt;")
                '>' -> result.append("&gt;")
                '"' -> result.append("&quot;")
                '\'' -> result.append("&#39;")
                '\u00A0' -> result.append("&nbsp;")
                '\u0000' -> result.append("&#0;")
                else -> result.append(ch)
            }
        }
        return result.toString()
    }

    /** Reverses [escapeHtml4], including numeric entities such as `&#39;`. */
    fun unescapeHtml4(html: CharSequence?): String? {
        if (html == null) return null
        val result = StringBuilder(html.length)
        var index = 0
        while (index < html.length) {
            val ch = html[index]
            if (ch == '&') {
                val semi = html.indexOf(';', index + 1)
                // A bare `&` with no entity behind it is kept as is.
                if (semi > index && semi - index <= 12) {
                    val entity = html.subSequence(index + 1, semi).toString()
                    val decoded = decodeEntity(entity)
                    if (decoded != null) {
                        result.append(decoded)
                        index = semi + 1
                        continue
                    }
                }
            }
            result.append(ch)
            index++
        }
        return result.toString()
    }

    /** Escapes `&`, `<`, `>`, `"` and `'`, the five characters XML reserves. */
    fun escapeXml(xml: CharSequence?): String? = escapeHtml4(xml)

    /** Reverses [escapeXml]. */
    fun unescapeXml(xml: CharSequence?): String? = unescapeHtml4(xml)

    /**
     * Escapes text the way JavaScript's `escape()` does: letters and digits stay, and the
     * punctuation `* @ - _ + . /` stays; every other character becomes `%XX` (below 256) or
     * `%uXXXX`.
     */
    fun escape(content: CharSequence?): String? =
        escape(content) { ch -> !CharUtil.isLetterOrNumber(ch) && !JS_UNESCAPED.contains(ch) }

    /** Like [escape], but only letters and digits stay unescaped. */
    fun escapeAll(content: CharSequence?): String? = escape(content) { true }

    /**
     * Percent-encodes the characters [filter] accepts and copies the rest.
     *
     * @hutool cn.hutool.core.util.EscapeUtil#escape(java.lang.CharSequence, cn.hutool.core.text.StrMatcher?)
     */
    fun escape(content: CharSequence?, filter: (Char) -> Boolean): String? {
        if (content == null) return null
        val result = StringBuilder(content.length * 6)
        for (ch in content) {
            if (!filter(ch) || CharUtil.isLetterOrNumber(ch)) {
                result.append(ch)
            } else if (ch.code < 256) {
                result.append('%')
                if (ch.code < 16) result.append('0')
                result.append(ch.code.toString(16))
            } else {
                result.append("%u")
                if (ch.code <= 0xFFF) result.append('0')
                result.append(ch.code.toString(16))
            }
        }
        return result.toString()
    }

    /** Decodes `%XX` and `%uXXXX`; incomplete sequences are kept as they are. */
    fun unescape(content: String?): String? {
        if (content == null || content.isEmpty()) return content
        val result = StringBuilder(content.length)
        var index = 0
        while (index < content.length) {
            val ch = content[index]
            if (ch == '%' && index + 1 < content.length) {
                if (content[index + 1] == 'u') {
                    if (index + 6 <= content.length) {
                        val value = content.substring(index + 2, index + 6).toIntOrNull(16)
                        if (value != null) {
                            result.append(value.toChar())
                            index += 6
                            continue
                        }
                    }
                } else if (index + 3 <= content.length) {
                    val value = content.substring(index + 1, index + 3).toIntOrNull(16)
                    if (value != null) {
                        result.append(value.toChar())
                        index += 3
                        continue
                    }
                }
            }
            result.append(ch)
            index++
        }
        return result.toString()
    }

    /** [unescape], but text that is not valid escape input is returned unchanged. */
    fun safeUnescape(content: String?): String? {
        if (content == null) return null
        if (!content.contains('%')) return content
        return unescape(content)
    }

    private const val JS_UNESCAPED = "*@-_+./"

    private fun decodeEntity(entity: String): String? {
        val named = when (entity.lowercase()) {
            "amp" -> "&"
            "lt" -> "<"
            "gt" -> ">"
            "quot" -> "\""
            "apos" -> "'"
            "nbsp" -> NBSP
            else -> null
        }
        if (named != null) return named
        return if (entity.startsWith("#")) {
            val number = if (entity.startsWith("#x") || entity.startsWith("#X")) {
                entity.substring(2).toIntOrNull(16)
            } else {
                entity.substring(1).toIntOrNull()
            }
            number?.toChar()?.toString()
        } else {
            null
        }
    }
}
