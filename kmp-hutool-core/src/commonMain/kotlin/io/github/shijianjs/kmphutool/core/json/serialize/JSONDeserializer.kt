package io.github.shijianjs.kmphutool.core.json.serialize

import io.github.shijianjs.kmphutool.core.json.JSON

/**
 * Custom deserializer from a JSON container to a Kotlin value.
 *
 * Port of `cn.hutool.json.serialize.JSONDeserializer`.
 *
 * @hutool cn.hutool.json.serialize.JSONDeserializer
 */
fun interface JSONDeserializer<T> {
    fun deserialize(json: JSON): T
}
