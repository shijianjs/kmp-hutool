package io.github.shijianjs.kmphutool.core.cache

import kotlin.time.ExperimentalTime

/**
 * One cached entry: value, last-access time and access counter.
 *
 * Port of `cn.hutool.cache.impl.CacheObj`, using [kotlin.time.Clock] instead of
 * `System.currentTimeMillis` and a plain [Long] instead of `AtomicLong`.
 *
 * @hutool cn.hutool.cache.impl.CacheObj
 */
open class CacheObj<K, V> internal constructor(
    private val key: K,
    private val obj: V,
    val ttl: Long,
) {
    var lastAccess: Long = nowMillis()
        private set
    var accessCount: Long = 0
        private set

    fun getKey(): K = key

    fun getValue(): V = obj

    @OptIn(ExperimentalTime::class)
    fun isExpired(): Boolean = ttl > 0 && nowMillis() - lastAccess > ttl

    fun get(isUpdateLastAccess: Boolean): V {
        if (isUpdateLastAccess) lastAccess = nowMillis()
        accessCount++
        return obj
    }

    internal fun touch(isUpdateLastAccess: Boolean): V = get(isUpdateLastAccess)

    /** Adjusts the access count when an LFU cache normalises its counters. */
    internal fun addAccessCount(delta: Long): Long {
        accessCount += delta
        return accessCount
    }

    override fun toString(): String =
        "CacheObj [key=$key, obj=$obj, lastAccess=$lastAccess, accessCount=$accessCount, ttl=$ttl]"

    @OptIn(ExperimentalTime::class)
    private fun nowMillis(): Long = kotlin.time.Clock.System.now().toEpochMilliseconds()
}
