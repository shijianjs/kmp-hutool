package io.github.shijianjs.kmphutool.core.map.multi

import io.github.shijianjs.kmphutool.core.map.MapWrapper

/**
 * Base for maps whose value is a collection, so one key can hold several values.
 *
 * Port of `cn.hutool.core.map.multi.AbsCollValueMap`. [createCollection] decides the concrete
 * collection type in each subclass.
 *
 * @hutool cn.hutool.core.map.multi.AbsCollValueMap
 */
abstract class AbsCollValueMap<K, V, C : MutableCollection<V>>(
    rawMap: MutableMap<K, C>,
) : MapWrapper<K, C>(rawMap) {

    /** Adds every value of [m], creating the per-key collections as needed. */
    fun putAllValues(m: Map<K, out Collection<V>>?) {
        if (m == null) return
        for ((key, values) in m) for (value in values) putValue(key, value)
    }

    /** Adds [value] under [key], creating its collection on first use. */
    fun putValue(key: K, value: V) {
        val collection = get(key)
        if (collection == null) {
            val created = createCollection()
            put(key, created)
            created.add(value)
        } else {
            collection.add(value)
        }
    }

    /** The value at [index] under [key], or `null` when out of range. */
    fun get(key: K, index: Int): V? {
        val collection = get(key) ?: return null
        if (index < 0) return null
        var i = 0
        for (value in collection) {
            if (i == index) return value
            i++
        }
        return null
    }

    /** Removes one value under [key]. */
    fun removeValue(key: K, value: V): Boolean = get(key)?.remove(value) ?: false

    /** Removes several values under [key]. */
    fun removeValues(key: K, values: Collection<V>): Boolean = get(key)?.removeAll(values) ?: false

    /** Creates the collection that backs one key. */
    protected abstract fun createCollection(): C
}
