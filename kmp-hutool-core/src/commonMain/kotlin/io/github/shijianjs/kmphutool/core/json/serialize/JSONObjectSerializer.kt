package io.github.shijianjs.kmphutool.core.json.serialize

import io.github.shijianjs.kmphutool.core.json.JSONObject

/**
 * Serializer that fills a [JSONObject].
 *
 * Port of `cn.hutool.json.serialize.JSONObjectSerializer`.
 *
 * @hutool cn.hutool.json.serialize.JSONObjectSerializer
 */
fun interface JSONObjectSerializer<V> : JSONSerializer<JSONObject, V>
