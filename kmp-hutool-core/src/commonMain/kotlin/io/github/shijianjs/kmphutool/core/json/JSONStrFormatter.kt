package io.github.shijianjs.kmphutool.core.json

/**
 * Indents a compact JSON string with four spaces per level without parsing it.
 *
 * Port of `cn.hutool.json.JSONStrFormatter`.
 *
 * @hutool cn.hutool.json.JSONStrFormatter
 */
object JSONStrFormatter {
    private const val SPACE = "    "
    private const val NEW_LINE = '\n'

    /** Returns [json] with line breaks and indentation applied. */
    fun format(json: String): String {
        val result = StringBuilder()
        var wrapChar: Char? = null
        var escapeMode = false
        var number = 0
        val length = json.length

        for (i in 0 until length) {
            val key = json[i]

            if (key == '"' || key == '\'') {
                if (wrapChar == null) {
                    wrapChar = key
                } else if (wrapChar == key) {
                    if (escapeMode) escapeMode = false
                    wrapChar = null
                }
                if (i > 1 && json[i - 1] == ':') result.append(' ')
                result.append(key)
                continue
            }

            if (key == '\\') {
                if (wrapChar != null) {
                    escapeMode = !escapeMode
                    result.append(key)
                    continue
                }
                result.append(key)
            }

            if (wrapChar != null) {
                result.append(key)
                continue
            }

            if (key == '[' || key == '{') {
                if (i > 1 && json[i - 1] == ':') {
                    result.append(NEW_LINE)
                    result.append(indent(number))
                }
                result.append(key)
                result.append(NEW_LINE)
                number++
                result.append(indent(number))
                continue
            }

            if (key == ']' || key == '}') {
                result.append(NEW_LINE)
                number--
                result.append(indent(number))
                result.append(key)
                continue
            }

            if (key == ',') {
                result.append(key)
                result.append(NEW_LINE)
                result.append(indent(number))
                continue
            }

            if (i > 1 && json[i - 1] == ':') result.append(' ')
            result.append(key)
        }

        return result.toString()
    }

    private fun indent(number: Int): String = SPACE.repeat(number)
}
