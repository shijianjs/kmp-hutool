package io.github.shijianjs.kmphutool.core.text

import io.github.shijianjs.kmphutool.core.util.StrUtil

/**
 * `{}` style placeholder formatting, the same template syntax Hutool uses in `StrUtil.format`.
 *
 * Port of `cn.hutool.core.text.StrFormatter`.
 *
 * @hutool cn.hutool.core.text.StrFormatter
 */
object StrFormatter {
    /** The default placeholder: `{}`. */
    const val EMPTY_JSON = "{}"

    /** The default escape character. */
    const val DEFAULT_ESCAPE = '\\'

    /**
     * Formats [template] by replacing every `{}` with the next argument of [args].
     *
     * A `\` before the braces escapes the placeholder, `{1}`-style index placeholders are not
     * supported (Hutool keeps them for [formatWith]); `null` arguments print as `null`.
     *
     * @param template template with zero or more `{}` placeholders
     * @param args values, consumed left to right
     * @return the formatted string; [template] itself when there is no placeholder
     */
    fun format(template: CharSequence, vararg args: Any?): String = formatWith(template, EMPTY_JSON, *args)

    /**
     * Formats [template] with a custom placeholder, for example `{1}` or `$name`.
     *
     * @param template template containing [placeHolder] markers
     * @param placeHolder the marker to replace
     * @param args values, consumed left to right
     */
    fun formatWith(template: CharSequence, placeHolder: String, vararg args: Any?): String {
        if (template.isEmpty() || args.isEmpty() || placeHolder.isEmpty()) {
            return template.toString()
        }
        val placeHolderLen = placeHolder.length
        var argIndex = 0
        val result = StringBuilder(template.length + 16)
        var index = 0
        while (index < template.length) {
            if (template.startsWithAt(placeHolder, index)) {
                // An escaped placeholder (`\{}`) is emitted without the escape character.
                if (index > 0 && template[index - 1] == DEFAULT_ESCAPE) {
                    result.setLength(result.length - 1)
                    result.append(placeHolder)
                    index += placeHolderLen
                    continue
                }
                if (argIndex < args.size) {
                    result.append(args[argIndex++].toStringOrEmpty())
                    index += placeHolderLen
                    continue
                }
            }
            result.append(template[index])
            index++
        }
        return result.toString()
    }

    /**
     * Formats a template whose placeholders are keys of [map]: `{name}` becomes `map["name"]`.
     *
     * @param template template containing `{key}` placeholders
     * @param map the values, keyed by the text inside the braces
     * @param ignoreNull when `true`, a key mapping to `null` keeps its placeholder in the output
     */
    fun format(template: CharSequence, map: Map<*, *>, ignoreNull: Boolean = true): String {
        if (template.isEmpty() || map.isEmpty()) return template.toString()
        val result = StringBuilder(template.length + 16)
        var index = 0
        var insidePlaceholder = false
        var keyStart = 0
        while (index < template.length) {
            val ch = template[index]
            when {
                ch == StrPool.DELIM_START && !insidePlaceholder -> {
                    if (index > 0 && template[index - 1] == DEFAULT_ESCAPE) {
                        result.setLength(result.length - 1)
                        result.append(ch)
                    } else {
                        insidePlaceholder = true
                        keyStart = index + 1
                    }
                }

                ch == StrPool.DELIM_END && insidePlaceholder -> {
                    insidePlaceholder = false
                    val key = template.substring(keyStart, index)
                    val value = map[key]
                    if (!map.containsKey(key) || (value == null && ignoreNull)) {
                        result.append(StrPool.DELIM_START).append(key).append(StrPool.DELIM_END)
                    } else {
                        result.append(value.toStringOrEmpty())
                    }
                }

                insidePlaceholder -> Unit

                else -> result.append(ch)
            }
            index++
        }
        return result.toString()
    }

    private fun CharSequence.startsWithAt(value: String, index: Int): Boolean {
        if (index + value.length > length) return false
        for (offset in value.indices) {
            if (this[index + offset] != value[offset]) return false
        }
        return true
    }

    private fun Any?.toStringOrEmpty(): String = StrUtil.toStringOrEmpty(this)
}
