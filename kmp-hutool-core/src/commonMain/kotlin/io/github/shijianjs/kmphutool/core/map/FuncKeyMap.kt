package io.github.shijianjs.kmphutool.core.map

/**
 * A key-normalising map driven by a function.
 *
 * Port of `cn.hutool.core.map.FuncKeyMap`.
 *
 * @hutool cn.hutool.core.map.FuncKeyMap
 */
open class FuncKeyMap<K, V>(
    emptyMap: MutableMap<K, V> = linkedMapOf(),
    private val keyFunc: ((K) -> K)? = null,
) : CustomKeyMap<K, V>(emptyMap) {
    override fun customKey(key: K): K = keyFunc?.invoke(key) ?: key
}
