package io.github.shijianjs.kmphutool.core.json

/**
 * Parses a complete JSON text into [JSONObject], [JSONArray] or a scalar.
 *
 * Port of `cn.hutool.json.JSONTokener`. Hutool's character-level lexer is replaced by
 * `kotlinx.serialization-json`; the public entry point still returns the dynamic Hutool value types.
 *
 * @hutool cn.hutool.json.JSONTokener
 */
class JSONTokener(private val source: String) {
    fun nextValue(): Any? = JSONConvert.parse(source)

    fun more(): Boolean = source.isNotBlank()

    fun syntaxError(message: String): JSONException = JSONException(message, null as Throwable?)
}
