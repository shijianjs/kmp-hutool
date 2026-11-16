package io.github.shijianjs.kmphutool.core.cache

/**
 * Least-recently-used cache: each hit moves the entry to the most-recent end.
 *
 * Port of `cn.hutool.cache.impl.LRUCache`. Access order is maintained explicitly because Kotlin's
 * common `LinkedHashMap` only guarantees insertion order. Not thread-safe, see [Cache].
 *
 * @hutool cn.hutool.cache.impl.LRUCache
 */
class LRUCache<K, V>(capacity: Int, timeout: Long = 0) : AbstractCache<K, V>(
    if (capacity == Int.MAX_VALUE) capacity - 1 else capacity,
    timeout,
) {
    override fun onAccess(key: K) {
        val co = cacheMap.remove(key) ?: return
        cacheMap[key] = co
    }

    override fun pruneCache(): Int {
        var count = 0
        val iterator = cacheMap.entries.iterator()
        while (iterator.hasNext()) {
            val co = iterator.next().value
            if (isPruneExpiredActive() && co.isExpired()) {
                iterator.remove()
                onRemove(co.getKey(), co.getValue())
                count++
            }
        }

        if (isFull()) {
            val eldest = cacheMap.keys.firstOrNull()
            if (eldest != null) {
                cacheMap.remove(eldest)?.let {
                    onRemove(it.getKey(), it.getValue())
                    count++
                }
            }
        }
        return count
    }
}
