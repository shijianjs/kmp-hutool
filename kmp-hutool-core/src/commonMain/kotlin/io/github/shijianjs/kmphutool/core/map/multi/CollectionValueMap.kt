package io.github.shijianjs.kmphutool.core.map.multi

/**
 * A multi-value map with a pluggable collection factory.
 *
 * Port of `cn.hutool.core.map.multi.CollectionValueMap`.
 *
 * @hutool cn.hutool.core.map.multi.CollectionValueMap
 */
class CollectionValueMap<K, V> private constructor(
    rawMap: MutableMap<K, MutableCollection<V>>,
    private val collectionFactory: () -> MutableCollection<V>,
) : AbsCollValueMap<K, V, MutableCollection<V>>(rawMap) {

    constructor() : this(3, 0.75f)

    constructor(initialCapacity: Int) : this(initialCapacity, 0.75f)

    constructor(m: Map<K, Collection<V>>) : this(0.75f, m)

    constructor(loadFactor: Float, m: Map<K, Collection<V>>) : this(loadFactor, m, { ArrayList() })

    constructor(initialCapacity: Int, loadFactor: Float) :
        this(HashMap(initialCapacity, loadFactor), { ArrayList() })

    constructor(loadFactor: Float, m: Map<K, Collection<V>>, factory: () -> MutableCollection<V>) :
        this(HashMap<K, MutableCollection<V>>(m.size, loadFactor), factory)

    private fun addAllValues(m: Map<K, Collection<V>>) {
        for ((key, values) in m) for (value in values) putValue(key, value)
    }

    override fun createCollection(): MutableCollection<V> = collectionFactory()
}

