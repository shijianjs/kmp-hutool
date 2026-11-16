package io.github.shijianjs.kmphutool.core.cache

/**
 * First-in-first-out cache: expired entries are pruned first, then the oldest entry is evicted.
 *
 * Port of `cn.hutool.cache.impl.FIFOCache`; eviction order is the insertion order of the backing
 * [LinkedHashMap]. Not thread-safe, see [Cache] for the concurrency note.
 *
 * @hutool cn.hutool.cache.impl.FIFOCache
 */
class FIFOCache<K, V>(capacity: Int, timeout: Long = 0) : AbstractCache<K, V>(capacity, timeout) {
    override fun pruneCache(): Int {
        var count = 0
        var firstKey: K? = null

        val iterator = cacheMap.entries.iterator()
        while (iterator.hasNext()) {
            val entry = iterator.next()
            val co = entry.value
            if (isPruneExpiredActive() && co.isExpired()) {
                iterator.remove()
                onRemove(co.getKey(), co.getValue())
                count++
                continue
            }
            if (firstKey == null) firstKey = entry.key
        }

        if (isFull() && firstKey != null) {
            cacheMap.remove(firstKey)?.let {
                onRemove(it.getKey(), it.getValue())
                count++
            }
        }
        return count
    }
}
