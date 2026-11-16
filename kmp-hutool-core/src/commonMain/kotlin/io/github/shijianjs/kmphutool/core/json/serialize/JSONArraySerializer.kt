package io.github.shijianjs.kmphutool.core.json.serialize

import io.github.shijianjs.kmphutool.core.json.JSONArray

/**
 * Serializer that fills a [JSONArray].
 *
 * Port of `cn.hutool.json.serialize.JSONArraySerializer`.
 *
 * @hutool cn.hutool.json.serialize.JSONArraySerializer
 */
fun interface JSONArraySerializer<V> : JSONSerializer<JSONArray, V>
