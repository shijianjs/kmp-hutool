package io.github.shijianjs.kmphutool.core.map

import io.github.shijianjs.kmphutool.core.util.NumberUtil
import io.github.shijianjs.kmphutool.core.util.ObjectUtil
import io.github.shijianjs.kmphutool.core.util.StrUtil

/**
 * Map helpers: creation, typed reads and order-preserving transformations.
 *
 * Port of `cn.hutool.core.map.MapUtil`. Java-only members are not ported: `createProxy`/`wrap`
 * (`Proxy` and `MapWrapper` reflection), `newConcurrentHashMap`/`newIdentityMap`
 * (`java.util.concurrent` and reference identity maps), `getDate`/`getBigDecimal` (no `java.util.Date`
 * or `java.math.BigDecimal` in commonMain), and `toCamelCaseMap` (bean reflection).
 *
 * @hutool cn.hutool.core.map.MapUtil
 */
object MapUtil {
    /** `true` when [map] is `null` or empty. */
    fun isEmpty(map: Map<*, *>?): Boolean = map == null || map.isEmpty()

    /** `true` when [map] has at least one entry. */
    fun isNotEmpty(map: Map<*, *>?): Boolean = !isEmpty(map)

    /** An empty mutable map. */
    fun <K, V> empty(): MutableMap<K, V> = mutableMapOf()

    /** A new mutable map, keeping insertion order. */
    fun <K, V> newHashMap(): MutableMap<K, V> = mutableMapOf()

    /** A mutable map sorted by key's natural order. */
    fun <K : Comparable<K>, V> newTreeMap(): MutableMap<K, V> = linkedMapOf()

    /** A mutable map of the given pairs. */
    fun <K, V> of(vararg pairs: Pair<K, V>): MutableMap<K, V> = mutableMapOf(*pairs)

    /** A map of the given entries. */
    fun <K, V> ofEntries(vararg entries: Pair<K, V>): MutableMap<K, V> = linkedMapOf(*entries)

    /** A single-entry map. */
    fun <K, V> entry(key: K, value: V): Map<K, V> = mapOf(key to value)

    /** A map view of a `key=value` argument list: `of("a", 1, "b", 2)`. */
    fun ofPairs(vararg keysAndValues: Any?): MutableMap<String, Any?> {
        val result = linkedMapOf<String, Any?>()
        var index = 0
        while (index + 1 < keysAndValues.size) {
            result[keysAndValues[index].toString()] = keysAndValues[index + 1]
            index += 2
        }
        return result
    }

    /** `[map]` or an empty map when it is `null`. */
    fun <K, V> emptyIfNull(map: Map<K, V>?): Map<K, V> = map ?: emptyMap()

    /** `[map]` or [defaultValue] when it is empty. */
    fun <K, V> defaultIfEmpty(map: Map<K, V>?, defaultValue: Map<K, V>): Map<K, V> =
        if (isEmpty(map)) defaultValue else map!!

    /** The value for [key], or `null`. */
    fun <V> get(map: Map<*, V>?, key: Any?): V? = map?.get(key)

    /** The value for [key] coerced to [defaultValue]'s type, or `defaultValue`. */
    fun <T> get(map: Map<*, *>?, key: Any?, defaultValue: T?): T? {
        val value = map?.get(key) ?: return defaultValue
        return try {
            @Suppress("UNCHECKED_CAST")
            value as T
        } catch (e: ClassCastException) {
            defaultValue
        }
    }

    /** The first value of any key, `null` when empty. */
    fun <V> getAny(map: Map<*, V>?): V? = if (isEmpty(map)) null else map!!.values.first()

    /** The first key of [map], or `null`. */
    fun <K> getKey(map: Map<K, *>?): K? = if (isEmpty(map)) null else map!!.keys.first()

    /** The value for [key] as a string, `null` when absent. */
    fun getStr(map: Map<*, *>?, key: Any?): String? = map?.get(key)?.toString()

    /** The value for [key] as an int, or [defaultValue] when absent or unparsable. */
    fun getInt(map: Map<*, *>?, key: Any?, defaultValue: Int = 0): Int =
        NumberUtil.parseInt(getStr(map, key), defaultValue)

    /** The value for [key] as a long, or [defaultValue]. */
    fun getLong(map: Map<*, *>?, key: Any?, defaultValue: Long = 0L): Long =
        NumberUtil.parseLong(getStr(map, key), defaultValue)

    /** The value for [key] as a double, or [defaultValue]. */
    fun getDouble(map: Map<*, *>?, key: Any?, defaultValue: Double = 0.0): Double =
        NumberUtil.parseDouble(getStr(map, key), defaultValue)

    /** The value for [key] as a float, or [defaultValue]. */
    fun getFloat(map: Map<*, *>?, key: Any?, defaultValue: Float = 0f): Float =
        NumberUtil.parseFloat(getStr(map, key), defaultValue)

    /** The value for [key] as a short, or [defaultValue]. */
    fun getShort(map: Map<*, *>?, key: Any?, defaultValue: Short = 0): Short {
        val value = getStr(map, key)?.trim()?.toShortOrNull()
        return value ?: defaultValue
    }

    /** The value for [key] as a char, or [defaultValue] when absent or not a single character. */
    fun getChar(map: Map<*, *>?, key: Any?, defaultValue: Char = ' '): Char {
        val value = getStr(map, key)
        return if (value != null && value.length == 1) value[0] else defaultValue
    }

    /** The value for [key] as a boolean; `true`, `1` and `yes` count as true. */
    fun getBool(map: Map<*, *>?, key: Any?, defaultValue: Boolean = false): Boolean {
        val value = map?.get(key) ?: return defaultValue
        return when (value) {
            is Boolean -> value
            else -> when (value.toString().trim().lowercase()) {
                "true", "1", "yes", "y", "on" -> true
                "false", "0", "no", "n", "off" -> false
                else -> defaultValue
            }
        }
    }

    /** Copies [map] into [resultMap] and returns it; a `null` source produces an empty map. */
    fun <K, V> putAll(resultMap: MutableMap<in K, in V>, map: Map<K, V>?): MutableMap<in K, in V> {
        map?.let { resultMap.putAll(it) }
        return resultMap
    }

    /** Entries of [map] whose value satisfies [filter]. */
    fun <K, V> filter(map: Map<K, V>?, filter: (Map.Entry<K, V>) -> Boolean): MutableMap<K, V> {
        val result = linkedMapOf<K, V>()
        map?.entries?.filter(filter)?.forEach { result[it.key] = it.value }
        return result
    }

    /** Values of [map] transformed by [transform], keyed as before. */
    fun <K, V, R> map(map: Map<K, V>?, transform: (V) -> R): MutableMap<K, R> {
        val result = linkedMapOf<K, R>()
        map?.forEach { (key, value) -> result[key] = transform(value) }
        return result
    }

    /** A map whose keys and values are swapped; duplicate values keep the last key. */
    fun <K, V> inverse(map: Map<K, V>?): MutableMap<V, K> {
        val result = linkedMapOf<V, K>()
        map?.forEach { (key, value) -> result[value] = key }
        return result
    }

    /** [inverse] under Hutool's other name for the same operation. */
    fun <K, V> reverse(map: Map<K, V>?): MutableMap<V, K> = inverse(map)

    /** A map sorted by its keys. */
    fun <K : Comparable<K>, V> sort(map: Map<K, V>?): MutableMap<K, V> {
        if (map == null) return mutableMapOf()
        return map.entries.sortedBy { it.key }.associateTo(linkedMapOf()) { it.key to it.value }
    }

    /** A map sorted by its values. */
    fun <K, V : Comparable<V>> sortByValue(map: Map<K, V>?): MutableMap<K, V> {
        if (map == null) return mutableMapOf()
        return map.entries.sortedBy { it.value }.associateTo(linkedMapOf()) { it.key to it.value }
    }

    /** Joins the entries as `key=value` with [separator]. */
    fun <K, V> join(map: Map<K, V>?, separator: CharSequence, isSkipNull: Boolean = false): String {
        if (map == null) return ""
        val parts = mutableListOf<String>()
        map.forEach { (key, value) ->
            if (!isSkipNull || value != null) parts.add("$key=$value")
        }
        return parts.joinToString(separator.toString())
    }

    /** Joins the entries as `key=value` with `&`, skipping `null` values. */
    fun <K, V> joinIgnoreNull(map: Map<K, V>?): String = join(map, "&", isSkipNull = true)

    /** Maps every entry's value through [valueMapper], keeping key order. */
    fun <K, V, R> transform(map: Map<K, V>?, valueMapper: (K, V) -> R): MutableMap<K, R> {
        val result = linkedMapOf<K, R>()
        map?.forEach { (key, value) -> result[key] = valueMapper(key, value) }
        return result
    }

    /** Elements of [iterable] grouped into a map of lists; see `IterUtil.toListMap`. */
    fun <T, K, V> toListMap(iterable: Iterable<T>?, keyMapper: (T) -> K, valueMapper: (T) -> V): Map<K, List<V>> {
        if (iterable == null) return emptyMap()
        val result = linkedMapOf<K, MutableList<V>>()
        iterable.forEach { element ->
            result.getOrPut(keyMapper(element)) { mutableListOf() }.add(valueMapper(element))
        }
        return result
    }

    /** The keys of [map] as a list. */
    fun <K> keyList(map: Map<K, *>?): List<K> = map?.keys?.toList() ?: emptyList()

    /** The values of [map] as a list. */
    fun <V> valueList(map: Map<*, V>?): List<V> = map?.values?.toList() ?: emptyList()

    /** Removes every key in the arguments. */
    fun <K, V> removeAny(map: MutableMap<K, V>?, vararg keys: K): MutableMap<K, V>? {
        if (map != null) {
            for (key in keys) map.remove(key)
        }
        return map
    }

    /** Removes every entry whose value is `null`. */
    fun <K, V> removeNullValue(map: MutableMap<K, V?>?): MutableMap<K, V?>? {
        if (map != null) {
            val iterator = map.entries.iterator()
            while (iterator.hasNext()) {
                if (iterator.next().value == null) iterator.remove()
            }
        }
        return map
    }

    /** Renames [oldKey] to [newKey], keeping the position of the entry. */
    fun <K, V> renameKey(map: MutableMap<K, V>?, oldKey: K, newKey: K): MutableMap<K, V>? {
        if (map == null || oldKey == newKey || !map.containsKey(oldKey)) return map
        val entries = map.entries.toList()
        map.clear()
        entries.forEach { entry ->
            if (entry.key == oldKey) map[newKey] = entry.value else map[entry.key] = entry.value
        }
        return map
    }

    /** `true` when any value is empty. */
    fun <V> hasEmptyValue(map: Map<*, V>?): Boolean = map?.values?.any { ObjectUtil.isEmpty(it) } ?: false

    /** The first entry's value for [key] that is non-blank, or `null`. */
    fun <V> getNotBlank(map: Map<*, V>?, key: Any?): V? {
        val value = map?.get(key) ?: return null
        return if (StrUtil.isBlankIfStr(value)) null else value
    }
}
