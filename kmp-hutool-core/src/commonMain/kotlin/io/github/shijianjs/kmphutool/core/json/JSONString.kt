package io.github.shijianjs.kmphutool.core.json

/**
 * Implemented by values that control their own JSON rendering.
 *
 * Port of `cn.hutool.json.JSONString`.
 *
 * @hutool cn.hutool.json.JSONString
 */
interface JSONString {
    fun toJSONString(): String
}
