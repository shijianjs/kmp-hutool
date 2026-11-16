package io.github.shijianjs.kmphutool.core.json

/**
 * Character cursor shared by JSON and XML parsing.
 *
 * Port of `cn.hutool.json.JSONTokener`. The JSON value itself is parsed by
 * `kotlinx.serialization-json`; the cursor methods below exist for XML markup scanning and for code
 * that walks the source character by character.
 *
 * @hutool cn.hutool.json.JSONTokener
 */
open class JSONTokener(private val source: CharSequence) {
    private var index = 0
    private var previous: Char = '\u0000'

    fun next(): Char {
        if (index >= source.length) {
            previous = '\u0000'
            return previous
        }
        previous = source[index]
        index++
        return previous
    }

    fun back() {
        if (index > 0) index--
    }

    fun getPrevious(): Char = previous

    fun more(): Boolean {
        var i = index
        while (i < source.length && source[i].isWhitespace()) i++
        index = i
        return index < source.length
    }

    fun nextClean(): Char {
        while (true) {
            val c = next()
            if (c == '\u0000' || !c.isWhitespace()) return c
        }
    }

    fun nextStringValue(): String {
        val c = nextClean()
        if (c == '"' || c == '\'') {
            val sb = StringBuilder()
            while (true) {
                val ch = next()
                if (ch == '\u0000' || ch == c) return sb.toString()
                sb.append(ch)
            }
        }
        val sb = StringBuilder()
        var ch = c
        while (ch != ':' && ch != ',' && ch != '}' && ch != ']' && ch != '\u0000') {
            sb.append(ch)
            ch = next()
        }
        if (ch != '\u0000') back()
        return sb.toString().trim()
    }

    fun nextValue(): Any? {
        if (!more()) throw JSONException("Missing value")
        val value = JSONConvert.parse(source.substring(index))
        index = source.length
        return value
    }

    fun skipPast(to: String): Boolean {
        val position = source.toString().indexOf(to, index)
        if (position < 0) {
            index = source.length
            return false
        }
        index = position + to.length
        return true
    }

    fun syntaxError(message: String): JSONException =
        JSONException("$message at character ${index - 1}")
}
