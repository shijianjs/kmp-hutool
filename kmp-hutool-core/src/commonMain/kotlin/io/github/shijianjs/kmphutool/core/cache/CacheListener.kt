package io.github.shijianjs.kmphutool.core.cache

/**
 * Receives cache eviction events.
 *
 * Port of `cn.hutool.cache.CacheListener`, the portable part of the cache module.
 *
 * @hutool cn.hutool.cache.CacheListener
 */
fun interface CacheListener<K, V> {
    fun onRemove(key: K, cachedObject: V)
}
