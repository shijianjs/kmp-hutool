package io.github.shijianjs.kmphutool.core.map

/**
 * Base class for maps that normalise keys (and optionally values) on the way in and out.
 *
 * Port of `cn.hutool.core.map.TransMap`. Kotlin maps are typed, so [customKey] takes a `K` instead
 * of `Object`; the unchecked casts upstream needs disappear.
 *
 * @hutool cn.hutool.core.map.TransMap
 */
abstract class TransMap<K, V>(emptyMap: MutableMap<K, V> = linkedMapOf()) : MapWrapper<K, V>(emptyMap) {

    /** Maps an incoming key to the key actually stored. */
    protected abstract fun customKey(key: K): K

    /** Maps an incoming value to the value actually stored; identity by default. */
    protected open fun customValue(value: V): V = value

    override fun get(key: K): V? = rawMap[customKey(key)]

    override fun put(key: K, value: V): V? = rawMap.put(customKey(key), customValue(value))

    override fun putAll(from: Map<out K, V>) {
        from.forEach { (key, value) -> put(key, value) }
    }

    override fun containsKey(key: K): Boolean = rawMap.containsKey(customKey(key))

    override fun remove(key: K): V? = rawMap.remove(customKey(key))

    fun getOrDefault(key: K, defaultValue: V): V = rawMap[customKey(key)] ?: defaultValue
}
