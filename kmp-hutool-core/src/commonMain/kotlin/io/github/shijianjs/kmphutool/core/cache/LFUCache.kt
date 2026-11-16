package io.github.shijianjs.kmphutool.core.cache

/**
 * Least-frequently-used cache: expired entries go first, then the entry with the smallest access
 * count is evicted and the remaining counters are normalised.
 *
 * Port of `cn.hutool.cache.impl.LFUCache`. Not thread-safe, see [Cache].
 *
 * @hutool cn.hutool.cache.impl.LFUCache
 */
class LFUCache<K, V>(capacity: Int, timeout: Long = 0) : AbstractCache<K, V>(
    if (capacity == Int.MAX_VALUE) capacity - 1 else capacity,
    timeout,
) {
    override fun pruneCache(): Int {
        var count = 0
        var least: CacheObj<K, V>? = null

        val iterator = cacheMap.entries.iterator()
        while (iterator.hasNext()) {
            val co = iterator.next().value
            if (co.isExpired()) {
                iterator.remove()
                onRemove(co.getKey(), co.getValue())
                count++
                continue
            }
            if (least == null || co.accessCount < least.accessCount) least = co
        }

        val leastObj = least
        if (isFull() && leastObj != null) {
            val minAccessCount = leastObj.accessCount
            val second = cacheMap.entries.iterator()
            while (second.hasNext()) {
                val co = second.next().value
                if (co.addAccessCount(-minAccessCount) <= 0) {
                    second.remove()
                    onRemove(co.getKey(), co.getValue())
                    count++
                }
            }
        }
        return count
    }
}
