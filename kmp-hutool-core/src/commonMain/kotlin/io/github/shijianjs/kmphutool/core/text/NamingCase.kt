package io.github.shijianjs.kmphutool.core.text

import io.github.shijianjs.kmphutool.core.util.StrUtil

/**
 * Naming-convention conversions: camelCase, snake_case, kebab-case.
 *
 * Port of `cn.hutool.core.text.NamingCase`. The Java class switches on `Character.isUpperCase` and
 * neighbour characters; Kotlin's `Char` predicates do the same job.
 *
 * @hutool cn.hutool.core.text.NamingCase
 */
object NamingCase {
    /** `HelloWorld` → `hello_world`. */
    fun toUnderlineCase(str: CharSequence?): String? = toSymbolCase(str, '_')

    /** `HelloWorld` → `hello-world`. */
    fun toKebabCase(str: CharSequence?): String? = toSymbolCase(str, '-')

    /**
     * Rewrites camelCase into words joined by [symbol], lowercasing ordinary capitals and keeping
     * runs of capitals as acronyms (`aBC` → `a_BC`, `ABcc` → `A_bcc`).
     */
    fun toSymbolCase(str: CharSequence?, symbol: Char): String? {
        if (str == null) return null

        val result = StringBuilder(str.length)
        for (i in str.indices) {
            var c = str[i]
            if (c.isUpperCase()) {
                val prev = if (i > 0) str[i - 1] else null
                val next = if (i < str.length - 1) str[i + 1] else null

                if (prev != null) {
                    if (symbol == prev) {
                        if (next == null || next.isLowerCase()) c = c.lowercaseChar()
                    } else if (prev.isLowerCase()) {
                        result.append(symbol)
                        if (next == null || next.isLowerCase() || next.isDigit()) c = c.lowercaseChar()
                    } else if (next != null && next.isLowerCase()) {
                        result.append(symbol)
                        c = c.lowercaseChar()
                    }
                } else if (next == null || next.isLowerCase()) {
                    c = c.lowercaseChar()
                }
            }
            result.append(c)
        }
        return result.toString()
    }

    /** `hello_world` → `HelloWorld`. */
    fun toPascalCase(name: CharSequence?): String? = StrUtil.upperFirst(toCamelCase(name))

    /** `hello_world` → `helloWorld`. */
    fun toCamelCase(name: CharSequence?): String? = toCamelCase(name, '_')

    /** Splits [name] on [symbol] and camel-cases the following words. */
    fun toCamelCase(name: CharSequence?, symbol: Char): String? = toCamelCase(name, symbol, true)

    /**
     * Splits [name] on [symbol] and camel-cases the following words, lowercasing everything else
     * when [otherCharToLower] is `true`.
     */
    fun toCamelCase(name: CharSequence?, symbol: Char, otherCharToLower: Boolean): String? {
        if (name == null) return null
        val text = name.toString()
        if (!text.contains(symbol)) return text

        val result = StringBuilder(text.length)
        var upperCase = false
        for (c in text) {
            if (c == symbol) {
                upperCase = true
            } else if (upperCase) {
                result.append(c.uppercaseChar())
                upperCase = false
            } else {
                result.append(if (otherCharToLower) c.lowercaseChar() else c)
            }
        }
        return result.toString()
    }
}
