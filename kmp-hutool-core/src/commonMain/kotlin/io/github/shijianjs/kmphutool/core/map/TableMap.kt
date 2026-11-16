package io.github.shijianjs.kmphutool.core.map

/**
 * A map that allows duplicate keys and values, with forward and reverse lookup.
 *
 * Port of `cn.hutool.core.map.TableMap`. Keys and values live in parallel lists, so lookups scan
 * (slower than a HashMap, as upstream documents).
 *
 * @hutool cn.hutool.core.map.TableMap
 */
class TableMap<K, V> : MutableMap<K, V> {
    private val keyList: MutableList<K> = ArrayList()
    private val valueList: MutableList<V> = ArrayList()

    constructor() : this(10)

    constructor(size: Int) {
        // Lists grow on demand; the capacity hint is kept for API parity.
        if (size > 0) {
            // no-op: ArrayList has no public ensureCapacity in common code
        }
    }

    constructor(keys: Array<K>, values: Array<V>) {
        require(keys.size == values.size) { "keys and values must have the same size" }
        this.keyList.addAll(keys)
        this.valueList.addAll(values)
    }

    override val size: Int get() = keyList.size

    override fun isEmpty(): Boolean = keyList.isEmpty()

    override fun containsKey(key: K): Boolean = keyList.contains(key)

    override fun containsValue(value: V): Boolean = valueList.contains(value)

    override fun get(key: K): V? {
        val index = keyList.indexOf(key)
        return if (index > -1) valueList[index] else null
    }

    /** The first key mapped to [value], or `null`. */
    fun getKey(value: V): K? {
        val index = valueList.indexOf(value)
        return if (index > -1) keyList[index] else null
    }

    /** Every value stored under [key]. */
    fun getValues(key: K): List<V> = keyList.indices.filter { keyList[it] == key }.map { valueList[it] }

    /** Every key mapped to [value]. */
    fun getKeys(value: V): List<K> = valueList.indices.filter { valueList[it] == value }.map { keyList[it] }

    override fun put(key: K, value: V): V? {
        keyList.add(key)
        valueList.add(value)
        return null
    }

    /** Removes every occurrence of [key] and returns the last removed value. */
    override fun remove(key: K): V? {
        var lastValue: V? = null
        var index = keyList.indexOf(key)
        while (index > -1) {
            lastValue = removeByIndex(index)
            index = keyList.indexOf(key)
        }
        return lastValue
    }

    /** Removes the entry at [index] and returns its value. */
    fun removeByIndex(index: Int): V {
        val old = valueList.removeAt(index)
        keyList.removeAt(index)
        return old
    }

    override fun putAll(from: Map<out K, V>) {
        from.forEach { (k, v) -> put(k, v) }
    }

    override fun clear() {
        keyList.clear()
        valueList.clear()
    }

    override val keys: MutableSet<K>
        get() = object : AbstractMutableSet<K>() {
            override val size: Int get() = this@TableMap.size
            override fun iterator(): MutableIterator<K> = this@TableMap.keyList.iterator()
            override fun add(element: K): Boolean = throw UnsupportedOperationException()
        }

    /** Keys in insertion order. */
    fun keysList(): List<K> = keyList.toList()

    override val values: MutableCollection<V>
        get() = object : AbstractMutableCollection<V>() {
            override val size: Int get() = this@TableMap.size
            override fun iterator(): MutableIterator<V> = this@TableMap.valueList.iterator()
            override fun add(element: V): Boolean = throw UnsupportedOperationException()
        }

    override val entries: MutableSet<MutableMap.MutableEntry<K, V>>
        get() = object : AbstractMutableSet<MutableMap.MutableEntry<K, V>>() {
            override val size: Int get() = this@TableMap.size
            override fun iterator(): MutableIterator<MutableMap.MutableEntry<K, V>> =
                object : MutableIterator<MutableMap.MutableEntry<K, V>> {
                    private var index = 0
                    override fun hasNext(): Boolean = index < this@TableMap.size
                    override fun next(): MutableMap.MutableEntry<K, V> {
                        val i = index++
                        return object : MutableMap.MutableEntry<K, V> {
                            override val key: K get() = this@TableMap.keyList[i]
                            override val value: V get() = this@TableMap.valueList[i]
                            override fun setValue(newValue: V): V {
                                val old = this@TableMap.valueList[i]
                                this@TableMap.valueList[i] = newValue
                                return old
                            }
                        }
                    }

                    override fun remove() {
                        index--
                        this@TableMap.removeByIndex(index)
                    }
                }
            override fun add(element: MutableMap.MutableEntry<K, V>): Boolean =
                throw UnsupportedOperationException()
        }

    override fun toString(): String =
        keyList.indices.joinToString(", ", "{", "}") { "${keyList[it]}=${valueList[it]}" }
}
