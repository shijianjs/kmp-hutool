package io.github.shijianjs.kmphutool.core.json.serialize

import io.github.shijianjs.kmphutool.core.json.JSON

/**
 * Custom serializer from a value into an existing JSON container.
 *
 * Port of `cn.hutool.json.serialize.JSONSerializer`.
 *
 * @hutool cn.hutool.json.serialize.JSONSerializer
 */
fun interface JSONSerializer<T : JSON, V> {
    fun serialize(json: T, bean: V)
}
