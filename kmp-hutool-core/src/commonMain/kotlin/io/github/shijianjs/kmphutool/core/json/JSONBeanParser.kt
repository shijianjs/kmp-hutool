package io.github.shijianjs.kmphutool.core.json

/**
 * Lets a bean fill itself from a parsed JSON value.
 *
 * Port of `cn.hutool.json.JSONBeanParser`.
 *
 * @hutool cn.hutool.json.JSONBeanParser
 */
fun interface JSONBeanParser<T> {
    fun parse(value: T)
}
