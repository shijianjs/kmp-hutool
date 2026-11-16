package io.github.shijianjs.kmphutool.core.cache

/**
 * A size- and time-bounded cache.
 *
 * Port of `cn.hutool.cache.Cache`, Kotlinised: callers pass a `() -> V` supplier instead of
 * Hutool's `Func0`, and implementations are not thread-safe because common code has no portable
 * lock. Wrap the cache in your own coroutine mutex when it is shared between coroutines.
 *
 * @hutool cn.hutool.cache.Cache
 */
interface Cache<K, V> : Iterable<V> {
    /** Maximum number of entries, `0` means unlimited. */
    fun capacity(): Int

    /** Default entry lifetime in milliseconds, `0` means no expiry. */
    fun timeout(): Long

    fun put(key: K, value: V, timeout: Long = this.timeout())

    fun get(key: K, isUpdateLastAccess: Boolean = true): V?

    /** Returns the cached value, or computes, stores and returns one when absent. */
    fun get(key: K, supplier: () -> V): V {
        return get(key, true, supplier)
    }

    fun get(key: K, isUpdateLastAccess: Boolean, supplier: () -> V): V {
        get(key, isUpdateLastAccess)?.let { return it }
        val value = supplier()
        put(key, value)
        return value
    }

    fun cacheObjIterator(): Iterator<CacheObj<K, V>>

    fun prune(): Int

    fun isFull(): Boolean

    fun remove(key: K)

    fun clear()

    fun size(): Int

    fun isEmpty(): Boolean

    fun containsKey(key: K): Boolean

    fun setListener(listener: CacheListener<K, V>?): Cache<K, V> = this
}
