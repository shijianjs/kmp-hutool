package io.github.shijianjs.kmphutool.core.map.multi

/**
 * A multi-value map whose values are [ArrayList]s.
 *
 * Port of `cn.hutool.core.map.multi.ListValueMap`.
 *
 * @hutool cn.hutool.core.map.multi.ListValueMap
 */
class ListValueMap<K, V> : AbsCollValueMap<K, V, MutableList<V>> {
    constructor() : super(HashMap())

    constructor(initialCapacity: Int) : super(HashMap(initialCapacity))

    constructor(m: Map<K, Collection<V>>) : super(HashMap()) {
        putAllValues(m)
    }

    constructor(initialCapacity: Int, loadFactor: Float) : super(HashMap(initialCapacity, loadFactor))

    override fun createCollection(): MutableList<V> = ArrayList(3)
}
