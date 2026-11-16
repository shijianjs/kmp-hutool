package io.github.shijianjs.kmphutool.core.setting

/**
 * A map of `group -> (key -> value)`, the shape of an INI/properties file with sections.
 *
 * Port of `cn.hutool.setting.GroupedMap`. Upstream guards every access with a
 * `ReentrantReadWriteLock`; common code has no lock primitive here, so this port is not
 * thread-safe and says so instead of pretending.
 *
 * @hutool cn.hutool.setting.GroupedMap
 */
class GroupedMap : MutableMap<String, LinkedHashMap<String, String>> {
    private val backing = LinkedHashMap<String, LinkedHashMap<String, String>>()

    override val size: Int get() = backing.values.sumOf { it.size }

    override fun isEmpty(): Boolean = size == 0

    override fun containsKey(key: String): Boolean = backing.containsKey(key)

    override fun containsValue(value: LinkedHashMap<String, String>): Boolean = backing.containsValue(value)

    override fun get(key: String): LinkedHashMap<String, String>? = backing[key]

    override fun put(key: String, value: LinkedHashMap<String, String>): LinkedHashMap<String, String>? =
        backing.put(key, value)

    override fun remove(key: String): LinkedHashMap<String, String>? = backing.remove(key)

    override fun putAll(from: Map<out String, LinkedHashMap<String, String>>) = backing.putAll(from)

    override fun clear() = backing.clear()

    override val keys: MutableSet<String> get() = backing.keys

    override val values: MutableCollection<LinkedHashMap<String, String>> get() = backing.values

    override val entries: MutableSet<MutableMap.MutableEntry<String, LinkedHashMap<String, String>>>
        get() = backing.entries

    /** Value of [key] inside [group], or `null`. */
    fun get(group: String?, key: String): String? = backing[group.orEmpty()]?.get(key)

    /** Puts [value] under `group/key`, returning the previous value. */
    fun put(group: String?, key: String, value: String): String? {
        val normalized = group.orEmpty().trim()
        val map = backing.getOrPut(normalized) { LinkedHashMap() }
        return map.put(key, value)
    }

    /** Puts every entry of [m] under [group]. */
    fun putAll(group: String?, m: Map<String, String>): GroupedMap {
        for ((key, value) in m) put(group, key, value)
        return this
    }

    /** Removes `group/key`, returning its previous value. */
    fun remove(group: String?, key: String): String? = backing[group.orEmpty().trim()]?.remove(key)

    fun isEmpty(group: String?): Boolean = backing[group.orEmpty().trim()]?.isEmpty() ?: true

    fun containsKey(group: String?, key: String): Boolean =
        backing[group.orEmpty().trim()]?.containsKey(key) == true

    fun containsValue(group: String?, value: String): Boolean =
        backing[group.orEmpty().trim()]?.containsValue(value) == true

    fun clear(group: String?): GroupedMap {
        backing[group.orEmpty().trim()]?.clear()
        return this
    }

    fun keySet(group: String?): Set<String> = backing[group.orEmpty().trim()]?.keys ?: emptySet()

    fun values(group: String?): Collection<String> = backing[group.orEmpty().trim()]?.values ?: emptyList()

    fun entrySet(group: String?): Set<Map.Entry<String, String>> =
        backing[group.orEmpty().trim()]?.entries ?: emptySet()
}
