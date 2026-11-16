package io.github.shijianjs.kmphutool.core.map.multi

/**
 * A multi-value map whose values are insertion-ordered sets.
 *
 * Port of `cn.hutool.core.map.multi.SetValueMap`.
 *
 * @hutool cn.hutool.core.map.multi.SetValueMap
 */
class SetValueMap<K, V> : AbsCollValueMap<K, V, MutableSet<V>> {
    constructor() : super(HashMap())

    constructor(initialCapacity: Int) : super(HashMap(initialCapacity))

    constructor(m: Map<K, Collection<V>>) : super(HashMap()) {
        putAllValues(m)
    }

    constructor(initialCapacity: Int, loadFactor: Float) : super(HashMap(initialCapacity, loadFactor))

    override fun createCollection(): MutableSet<V> = LinkedHashSet(3)
}
