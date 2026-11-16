package io.github.shijianjs.kmphutool.core.map

import io.github.shijianjs.kmphutool.core.builder.Builder

/**
 * Chainable map construction.
 *
 * Port of `cn.hutool.core.map.MapBuilder`. `put` overloads cover the conditional forms; `join` is
 * implemented here because the Kotlin `MapUtil` port does not expose `join` separately.
 *
 * @hutool cn.hutool.core.map.MapBuilder
 */
class MapBuilder<K, V>(private val map: MutableMap<K, V>) : Builder<MutableMap<K, V>> {

    fun put(k: K, v: V): MapBuilder<K, V> = apply { map[k] = v }

    fun put(condition: Boolean, k: K, v: V): MapBuilder<K, V> = apply { if (condition) map[k] = v }

    fun put(condition: Boolean, k: K, supplier: () -> V): MapBuilder<K, V> =
        apply { if (condition) map[k] = supplier() }

    fun putAll(map: Map<out K, V>): MapBuilder<K, V> = apply { this.map.putAll(map) }

    fun clear(): MapBuilder<K, V> = apply { map.clear() }

    fun map(): MutableMap<K, V> = map

    override fun build(): MutableMap<K, V> = map()

    fun join(separator: String, keyValueSeparator: String): String =
        join(separator, keyValueSeparator, false)

    fun joinIgnoreNull(separator: String, keyValueSeparator: String): String =
        join(separator, keyValueSeparator, true)

    fun join(separator: String, keyValueSeparator: String, isIgnoreNull: Boolean): String {
        val out = StringBuilder()
        var first = true
        for ((key, value) in map) {
            if (isIgnoreNull && (key == null || value == null)) continue
            if (!first) out.append(separator)
            out.append(key).append(keyValueSeparator).append(value)
            first = false
        }
        return out.toString()
    }

    companion object {
        /** A builder over an empty hash map. */
        fun <K, V> create(): MapBuilder<K, V> = create(false)

        /** A builder over an empty map, insertion ordered when [isLinked] is `true`. */
        fun <K, V> create(isLinked: Boolean): MapBuilder<K, V> =
            create(if (isLinked) linkedMapOf() else HashMap())

        /** A builder over [map] itself, not a copy. */
        fun <K, V> create(map: MutableMap<K, V>): MapBuilder<K, V> = MapBuilder(map)
    }
}
