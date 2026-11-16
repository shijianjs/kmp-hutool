package io.github.shijianjs.kmphutool.core.json

/**
 * XML-aware tokenizer used by [XML] and [JSONXMLParser].
 *
 * Port of `cn.hutool.json.XMLTokener`. It keeps the upstream token model: markup characters come
 * back as [Char], names and quoted values as [String], and content text as trimmed strings.
 *
 * @hutool cn.hutool.json.XMLTokener
 */
class XMLTokener(source: CharSequence) : JSONTokener(source) {
    fun nextCDATA(): String {
        val sb = StringBuilder()
        while (true) {
            val c = next()
            if (c == '\u0000') throw syntaxError("Unclosed CDATA")
            sb.append(c)
            val i = sb.length - 3
            if (i >= 0 && sb[i] == ']' && sb[i + 1] == ']' && sb[i + 2] == '>') {
                sb.setLength(i)
                return sb.toString()
            }
        }
    }

    fun nextContent(): Any? {
        var c: Char
        do {
            c = next()
        } while (c.isWhitespace())

        if (c == '\u0000') return null
        if (c == '<') return XML.LT

        val sb = StringBuilder()
        while (true) {
            if (c == '<' || c == '\u0000') {
                back()
                return sb.toString().trim()
            }
            if (c == '&') sb.append(nextEntity(c)) else sb.append(c)
            c = next()
        }
    }

    fun nextEntity(ampersand: Char): Any {
        val sb = StringBuilder()
        while (true) {
            val c = next()
            when {
                c.isLetterOrDigit() || c == '#' -> sb.append(c.lowercaseChar())
                c == ';' -> return unescapeEntity(sb.toString())
                else -> throw syntaxError("Missing ';' in XML entity: &$sb")
            }
        }
    }

    fun nextMeta(): Any? {
        val c = nextClean()
        when (c) {
            '\u0000' -> throw syntaxError("Misshaped meta tag")
            '<' -> return XML.LT
            '>' -> return XML.GT
            '/' -> return XML.SLASH
            '=' -> return XML.EQ
            '!' -> return XML.BANG
            '?' -> return XML.QUEST
            '"', '\'' -> {
                while (true) {
                    val ch = next()
                    if (ch == '\u0000') throw syntaxError("Unterminated string")
                    if (ch == c) return true
                }
            }
            else -> while (true) {
                val ch = next()
                when {
                    ch == '\u0000' -> return true
                    ch.isWhitespace() -> return true
                    ch in "<>/=!?\"'" -> {
                        back()
                        return true
                    }
                }
            }
        }
    }

    fun nextToken(): Any {
        val c = nextClean()
        when (c) {
            '\u0000' -> throw syntaxError("Misshaped element")
            '<' -> throw syntaxError("Misplaced '<'")
            '>' -> return XML.GT
            '/' -> return XML.SLASH
            '=' -> return XML.EQ
            '!' -> return XML.BANG
            '?' -> return XML.QUEST
            '"', '\'' -> {
                val sb = StringBuilder()
                while (true) {
                    val ch = next()
                    if (ch == '\u0000') throw syntaxError("Unterminated string")
                    if (ch == c) return sb.toString()
                    if (ch == '&') sb.append(nextEntity(ch)) else sb.append(ch)
                }
            }
            else -> {
                val sb = StringBuilder()
                var ch = c
                while (true) {
                    sb.append(ch)
                    ch = next()
                    when {
                        ch == '\u0000' -> return sb.toString()
                        ch.isWhitespace() -> return sb.toString()
                        ch in ">/=!?[]" -> {
                            back()
                            return sb.toString()
                        }
                        ch in "<\"'" -> throw syntaxError("Bad character in a name")
                    }
                }
            }
        }
    }

    companion object {
        val entity: Map<String, Char> = mapOf(
            "amp" to '&',
            "apos" to '\'',
            "gt" to '>',
            "lt" to '<',
            "quot" to '"',
        )

        fun unescapeEntity(e: String): String {
            if (e.isEmpty()) return ""
            if (e[0] == '#') {
                val code = if (e.length > 1 && (e[1] == 'x' || e[1] == 'X')) {
                    e.substring(2).toIntOrNull(16)
                } else {
                    e.substring(1).toIntOrNull()
                }
                return code?.toChar()?.toString() ?: "&$e;"
            }
            return entity[e]?.toString() ?: "&$e;"
        }
    }
}
