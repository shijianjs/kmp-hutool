package io.github.shijianjs.kmphutool.core.map

/**
 * Wraps a [MutableMap] so a subclass can customise key or value handling while keeping the original
 * map behind [getRaw].
 *
 * Port of `cn.hutool.core.map.MapWrapper`. Kotlin's interface delegation provides the full
 * `MutableMap` surface, including the default methods upstream re-declares (`getOrDefault`,
 * `putIfAbsent`, `compute*`, `merge`).
 *
 * @hutool cn.hutool.core.map.MapWrapper
 */
open class MapWrapper<K, V>(protected val rawMap: MutableMap<K, V>) : MutableMap<K, V> by rawMap {
    /** The wrapped map itself, not a copy. */
    fun getRaw(): MutableMap<K, V> = rawMap

    override fun equals(other: Any?): Boolean =
        this === other || (other is MapWrapper<*, *> && rawMap == other.rawMap)

    override fun hashCode(): Int = rawMap.hashCode()

    override fun toString(): String = rawMap.toString()
}
