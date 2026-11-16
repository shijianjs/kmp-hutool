package io.github.shijianjs.kmphutool.core.map

/**
 * A map whose string keys are matched ignoring case, while keeping insertion order.
 *
 * Port of `cn.hutool.core.map.CaseInsensitiveMap`. Hutool normalizes keys by upper-casing;
 * Kotlin's [String.lowercase] has the same effect for the ASCII keys configuration uses, and unlike
 * `Locale`-dependent Java casing it never changes meaning with the platform locale.
 *
 * @hutool cn.hutool.core.map.CaseInsensitiveMap
 */
class CaseInsensitiveMap<V>(initial: Map<String, V>? = null) : MutableMap<String, V> {
    private val map = LinkedHashMap<String, V>()
    private val originalKeys = LinkedHashMap<String, String>()

    init {
        initial?.forEach { (key, value) -> put(key, value) }
    }

    override val size: Int
        get() = map.size

    override val keys: MutableSet<String>
        get() = originalKeys.keys

    override val values: MutableCollection<V>
        get() = map.values

    override val entries: MutableSet<MutableMap.MutableEntry<String, V>>
        get() = map.entries.map { entry ->
            object : MutableMap.MutableEntry<String, V> {
                override val key: String = originalKeys[entry.key] ?: entry.key
                override val value: V = entry.value
                override fun setValue(newValue: V): V = throw UnsupportedOperationException("read-only entry view")
            }
        }.toMutableSet()

    override fun containsKey(key: String): Boolean = map.containsKey(normalize(key))

    override fun containsValue(value: V): Boolean = map.containsValue(value)

    override fun get(key: String): V? = map[normalize(key)]

    override fun isEmpty(): Boolean = map.isEmpty()

    override fun clear() {
        map.clear()
        originalKeys.clear()
    }

    override fun put(key: String, value: V): V? {
        val normalized = normalize(key)
        originalKeys[normalized] = key
        return map.put(normalized, value)
    }

    override fun putAll(from: Map<out String, V>) {
        from.forEach { (key, value) -> put(key, value) }
    }

    override fun remove(key: String): V? {
        val normalized = normalize(key)
        originalKeys.remove(normalized)
        return map.remove(normalized)
    }

    /** A plain, case-sensitive copy with the original key spelling. */
    fun toNormalMap(): LinkedHashMap<String, V> {
        val result = LinkedHashMap<String, V>()
        map.forEach { (normalized, value) -> result[originalKeys[normalized] ?: normalized] = value }
        return result
    }

    override fun toString(): String = toNormalMap().toString()

    private fun normalize(key: String): String = key.lowercase()
}
