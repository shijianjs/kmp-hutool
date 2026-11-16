package io.github.shijianjs.kmphutool.core.map

/**
 * A fixed-capacity, access-ordered map: reading a key refreshes it, and inserting past [capacity]
 * evicts the least recently used entry.
 *
 * Kotlin rewrite of `cn.hutool.core.map.FixedLinkedHashMap`. `commonMain` has no access-order
 * `LinkedHashMap`, so ordering is maintained explicitly: every hit or write removes and re-appends the
 * key, and eviction takes the first key of the iteration order. Not thread-safe, same as upstream.
 *
 * @hutool cn.hutool.core.map.FixedLinkedHashMap
 */
class FixedLinkedHashMap<K, V>(
    capacity: Int,
) : MutableMap<K, V> {
    private val map = LinkedHashMap<K, V>(capacity + 1)
    private var capacity = capacity

    /** Called with the evicted entry, before it disappears from the map. */
    var removeListener: ((MutableMap.MutableEntry<K, V>) -> Unit)? = null

    /** The current capacity; lowering it does not evict immediately. */
    fun getCapacity(): Int = capacity

    fun setCapacity(capacity: Int) {
        this.capacity = capacity
    }

    override val size: Int
        get() = map.size

    override val keys: MutableSet<K>
        get() = map.keys

    override val values: MutableCollection<V>
        get() = map.values

    override val entries: MutableSet<MutableMap.MutableEntry<K, V>>
        get() = map.entries

    override fun containsKey(key: K): Boolean = map.containsKey(key)

    override fun containsValue(value: V): Boolean = map.containsValue(value)

    override fun get(key: K): V? {
        val value = map.remove(key) ?: return null
        // Re-insert to mark this key as the most recently used.
        map[key] = value
        return value
    }

    override fun isEmpty(): Boolean = map.isEmpty()

    override fun clear() = map.clear()

    override fun put(key: K, value: V): V? {
        val previous = map.remove(key)
        map[key] = value
        evictIfNeeded()
        return previous
    }

    override fun putAll(from: Map<out K, V>) {
        from.forEach { (key, value) -> put(key, value) }
    }

    override fun remove(key: K): V? = map.remove(key)

    override fun toString(): String = map.toString()

    private fun evictIfNeeded() {
        if (map.size <= capacity) return
        val eldestKey = map.keys.first()
        val eldestValue = map.getValue(eldestKey)
        removeListener?.invoke(
            object : MutableMap.MutableEntry<K, V> {
                override val key: K = eldestKey
                override val value: V = eldestValue
                override fun setValue(newValue: V): V = throw UnsupportedOperationException("read-only entry view")
            },
        )
        map.remove(eldestKey)
    }
}
