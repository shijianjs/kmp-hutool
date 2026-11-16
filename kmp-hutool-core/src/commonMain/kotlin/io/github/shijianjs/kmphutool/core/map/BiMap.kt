package io.github.shijianjs.kmphutool.core.map

/**
 * A map with a maintained inverse view, so a value can be looked up by its key.
 *
 * Port of `cn.hutool.core.map.BiMap`. Duplicate values are allowed: the last `put` wins in the
 * inverse map, exactly as upstream documents. The inverse is built lazily and dropped whenever a
 * mutating operation could have invalidated it.
 *
 * @hutool cn.hutool.core.map.BiMap
 */
class BiMap<K, V>(rawMap: MutableMap<K, V> = linkedMapOf()) : MapWrapper<K, V>(rawMap) {
    private var inverse: MutableMap<V, K>? = null

    override fun put(key: K, value: V): V? {
        val oldValue = rawMap.put(key, value)
        inverse?.let { inv ->
            if (oldValue != null) inv.remove(oldValue)
            inv[value] = key
        }
        return oldValue
    }

    override fun putAll(from: Map<out K, V>) {
        rawMap.putAll(from)
        inverse?.let { inv -> from.forEach { (k, v) -> inv[v] = k } }
    }

    override fun remove(key: K): V? {
        val value = rawMap.remove(key)
        if (value != null) inverse?.remove(value)
        return value
    }

    override fun clear() {
        rawMap.clear()
        inverse = null
    }

    /** The inverse map, built on first use; mutations of this map invalidate it. */
    fun getInverse(): MutableMap<V, K> {
        inverse?.let { return it }
        val built = linkedMapOf<V, K>()
        rawMap.forEach { (k, v) -> built[v] = k }
        inverse = built
        return built
    }

    /** The key currently mapped to [value], if any. */
    fun getKey(value: V): K? = getInverse()[value]

    /**
     * Puts [value] only when [key] is absent; `commonMain` has no `putIfAbsent`, so this is the
     * explicit method upstream callers expect.
     */
    fun putIfAbsent(key: K, value: V): V? {
        if (rawMap.containsKey(key)) return rawMap[key]
        rawMap[key] = value
        inverse?.put(value, key)
        return null
    }

    private fun resetInverseMap() {
        inverse = null
    }
}
