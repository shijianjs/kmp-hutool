package io.github.shijianjs.kmphutool.core.map

/**
 * A [TransMap] where both the stored key and the stored value come from functions.
 *
 * Port of `cn.hutool.core.map.FuncMap`.
 *
 * @hutool cn.hutool.core.map.FuncMap
 */
class FuncMap<K, V>(
    emptyMap: MutableMap<K, V> = linkedMapOf(),
    private val keyFunc: ((K) -> K)? = null,
    private val valueFunc: ((V) -> V)? = null,
) : TransMap<K, V>(emptyMap) {
    override fun customKey(key: K): K = keyFunc?.invoke(key) ?: key
    override fun customValue(value: V): V = valueFunc?.invoke(value) ?: value
}
