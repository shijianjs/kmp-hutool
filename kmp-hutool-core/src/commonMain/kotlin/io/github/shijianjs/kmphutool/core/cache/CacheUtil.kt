package io.github.shijianjs.kmphutool.core.cache

/**
 * Factories for the cache implementations.
 *
 * Port of `cn.hutool.cache.CacheUtil`. File, timed and weak caches from upstream need
 * `java.io`/timers/`WeakReference` and are intentionally absent; the in-memory policies are here.
 *
 * @hutool cn.hutool.cache.CacheUtil
 */
object CacheUtil {
    fun <K, V> newFIFOCache(capacity: Int, timeout: Long = 0): FIFOCache<K, V> =
        FIFOCache(capacity, timeout)

    fun <K, V> newLFUCache(capacity: Int, timeout: Long = 0): LFUCache<K, V> =
        LFUCache(capacity, timeout)

    fun <K, V> newLRUCache(capacity: Int, timeout: Long = 0): LRUCache<K, V> =
        LRUCache(capacity, timeout)

    fun <K, V> newNoCache(): NoCache<K, V> = NoCache()
}
