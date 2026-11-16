package io.github.shijianjs.kmphutool.core.cache

/**
 * Base class for size- and timeout-bounded caches.
 *
 * Port of `cn.hutool.cache.impl.AbstractCache`. The JVM implementation uses concurrent maps and
 * per-key locks; common code has no portable lock, so this class is deliberately single-threaded.
 * Subclasses only choose the eviction policy by overriding [pruneCache].
 *
 * @hutool cn.hutool.cache.impl.AbstractCache
 */
abstract class AbstractCache<K, V>(
    private val maxCapacity: Int,
    private val defaultTimeout: Long,
) : Cache<K, V> {
    protected val cacheMap: MutableMap<K, CacheObj<K, V>> = LinkedHashMap()
    protected var listener: CacheListener<K, V>? = null
    protected var existCustomTimeout: Boolean = false

    private var hitCount: Long = 0
    private var missCount: Long = 0

    override fun capacity(): Int = maxCapacity

    override fun timeout(): Long = defaultTimeout

    override fun put(key: K, value: V, timeout: Long) {
        val newObj = CacheObj(key, value, timeout)
        if (timeout != 0L) existCustomTimeout = true
        val existing = cacheMap.containsKey(key)
        if (!existing && isFull()) pruneCache()
        val old = cacheMap.put(key, newObj)
        if (old != null) onRemove(old.getKey(), old.getValue())
    }

    override fun get(key: K, isUpdateLastAccess: Boolean): V? {
        val co = cacheMap[key]
        if (co == null) {
            missCount++
            return null
        }
        if (co.isExpired()) {
            cacheMap.remove(key)
            onRemove(co.getKey(), co.getValue())
            missCount++
            return null
        }
        hitCount++
        val value = co.get(isUpdateLastAccess)
        onAccess(key)
        return value
    }

    /** Hook for policies that reorder entries on access, such as LRU. */
    protected open fun onAccess(key: K) = Unit

    override fun iterator(): Iterator<V> = cacheMap.values.map { it.getValue() }.iterator()

    override fun cacheObjIterator(): Iterator<CacheObj<K, V>> = cacheMap.values.iterator()

    override fun prune(): Int = pruneCache()

    protected abstract fun pruneCache(): Int

    override fun isFull(): Boolean = maxCapacity > 0 && cacheMap.size >= maxCapacity

    override fun remove(key: K) {
        cacheMap.remove(key)?.let { onRemove(it.getKey(), it.getValue()) }
    }

    override fun clear() {
        val removed = cacheMap.values.toList()
        cacheMap.clear()
        removed.forEach { onRemove(it.getKey(), it.getValue()) }
    }

    override fun size(): Int = cacheMap.size

    override fun isEmpty(): Boolean = cacheMap.isEmpty()

    override fun containsKey(key: K): Boolean {
        val co = cacheMap[key] ?: return false
        if (co.isExpired()) {
            cacheMap.remove(key)
            onRemove(co.getKey(), co.getValue())
            return false
        }
        return true
    }

    override fun setListener(listener: CacheListener<K, V>?): Cache<K, V> {
        this.listener = listener
        return this
    }

    protected fun isPruneExpiredActive(): Boolean = defaultTimeout != 0L || existCustomTimeout

    protected fun onRemove(key: K, value: V) {
        listener?.onRemove(key, value)
    }

    fun getHitCount(): Long = hitCount

    fun getMissCount(): Long = missCount
}
