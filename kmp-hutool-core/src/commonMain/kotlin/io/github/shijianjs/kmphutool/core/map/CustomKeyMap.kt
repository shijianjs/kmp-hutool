package io.github.shijianjs.kmphutool.core.map

/**
 * A [TransMap] whose subclasses only rewrite keys.
 *
 * Port of `cn.hutool.core.map.CustomKeyMap`.
 *
 * @hutool cn.hutool.core.map.CustomKeyMap
 */
abstract class CustomKeyMap<K, V>(emptyMap: MutableMap<K, V> = linkedMapOf()) : TransMap<K, V>(emptyMap) {
    override fun customValue(value: V): V = value
}
