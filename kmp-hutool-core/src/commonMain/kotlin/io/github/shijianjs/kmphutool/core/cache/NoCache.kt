package io.github.shijianjs.kmphutool.core.cache

/**
 * A cache that stores nothing; useful to switch caching off without changing call sites.
 *
 * Port of `cn.hutool.cache.impl.NoCache`.
 *
 * @hutool cn.hutool.cache.impl.NoCache
 */
class NoCache<K, V> : Cache<K, V> {
    override fun capacity(): Int = 0

    override fun timeout(): Long = 0

    override fun put(key: K, value: V, timeout: Long) = Unit

    override fun get(key: K, isUpdateLastAccess: Boolean): V? = null

    override fun cacheObjIterator(): Iterator<CacheObj<K, V>> = emptyList<CacheObj<K, V>>().iterator()

    override fun prune(): Int = 0

    override fun isFull(): Boolean = false

    override fun remove(key: K) = Unit

    override fun clear() = Unit

    override fun size(): Int = 0

    override fun isEmpty(): Boolean = true

    override fun containsKey(key: K): Boolean = false

    override fun iterator(): Iterator<V> = emptyList<V>().iterator()
}
