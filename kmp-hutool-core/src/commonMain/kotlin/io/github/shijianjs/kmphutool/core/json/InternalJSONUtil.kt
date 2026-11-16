package io.github.shijianjs.kmphutool.core.json

/**
 * XML content coercion helper.
 *
 * Port of `cn.hutool.json.InternalJSONUtil` (the `stringToValue` part). `BigDecimal`/`BigInteger`
 * branches are not portable, so integral values become [Long] and decimal values [Double].
 *
 * @hutool cn.hutool.json.InternalJSONUtil
 */
object InternalJSONUtil {
    private val numberPattern = Regex("""-?(0|[1-9]\d*)(\.\d+)?([eE][+-]?\d+)?""")

    fun stringToValue(string: String): Any {
        val value = string.trim()
        if (value.isEmpty()) return value
        when (value.lowercase()) {
            "true" -> return true
            "false" -> return false
            "null" -> return JSONNull
        }
        if (numberPattern.matches(value)) {
            value.toLongOrNull()?.let { return it }
            value.toDoubleOrNull()?.let { return it }
        }
        return value
    }
}
