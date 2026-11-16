package io.github.shijianjs.kmphutool.core.collection

import io.github.shijianjs.kmphutool.core.map.MapBuilder

/**
 * A set that de-duplicates by an externally supplied key instead of `equals`.
 *
 * Port of `cn.hutool.core.collection.UniqueKeySet`; `clone()` is not ported because Kotlin common
 * collections cannot be shallow-cloned.
 *
 * @hutool cn.hutool.core.collection.UniqueKeySet
 */
class UniqueKeySet<K, V>(
    private val uniqueGenerator: (V) -> K,
    private val map: MutableMap<K, V> = HashMap(),
) : AbstractMutableSet<V>() {

    constructor(uniqueGenerator: (V) -> K, c: Collection<V>) : this(uniqueGenerator) {
        addAll(c)
    }

    constructor(isLinked: Boolean, uniqueGenerator: (V) -> K) : this(uniqueGenerator, if (isLinked) linkedMapOf() else HashMap())

    constructor(isLinked: Boolean, uniqueGenerator: (V) -> K, c: Collection<V>) : this(isLinked, uniqueGenerator) {
        addAll(c)
    }

    constructor(initialCapacity: Int, loadFactor: Float, uniqueGenerator: (V) -> K) :
        this(uniqueGenerator, HashMap(initialCapacity, loadFactor))

    constructor(builder: MapBuilder<K, V>, uniqueGenerator: (V) -> K) : this(uniqueGenerator, builder.build())

    override val size: Int get() = map.size

    override fun isEmpty(): Boolean = map.isEmpty()

    override fun iterator(): MutableIterator<V> = map.values.iterator()

    override fun contains(element: V): Boolean = map.containsKey(uniqueGenerator(element))

    override fun add(element: V): Boolean = map.put(uniqueGenerator(element), element) == null

    /** Adds [element] only when its key is absent. */
    fun addIfAbsent(element: V): Boolean {
        val key = uniqueGenerator(element)
        if (map.containsKey(key)) return false
        map[key] = element
        return true
    }

    /** Adds every element whose key is still absent. */
    fun addAllIfAbsent(c: Collection<V>): Boolean {
        var modified = false
        for (element in c) if (addIfAbsent(element)) modified = true
        return modified
    }

    override fun remove(element: V): Boolean = map.remove(uniqueGenerator(element)) != null

    override fun clear() = map.clear()
}
