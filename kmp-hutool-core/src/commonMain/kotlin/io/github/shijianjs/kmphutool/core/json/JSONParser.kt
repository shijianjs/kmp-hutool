package io.github.shijianjs.kmphutool.core.json

/**
 * Thin parser facade over [JSONTokener].
 *
 * Port of `cn.hutool.json.JSONParser`. The filter overloads of upstream `parseTo` are not ported,
 * because they expose Hutool's mutable pair types; use [parse] and transform the result instead.
 *
 * @hutool cn.hutool.json.JSONParser
 */
class JSONParser(private val tokener: JSONTokener) {
    fun parse(): Any? = tokener.nextValue()

    companion object {
        fun of(tokener: JSONTokener): JSONParser = JSONParser(tokener)
    }
}
