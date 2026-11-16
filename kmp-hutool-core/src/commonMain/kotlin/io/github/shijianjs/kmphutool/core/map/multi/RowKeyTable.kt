package io.github.shijianjs.kmphutool.core.map.multi

import io.github.shijianjs.kmphutool.core.builder.Builder

/**
 * A [Table] keyed primarily by row: `row -> (column -> value)`.
 *
 * Port of `cn.hutool.core.map.multi.RowKeyTable`. Column views are live: reading a column walks the
 * rows, and writing through [getColumn] updates the owning table.
 *
 * @hutool cn.hutool.core.map.multi.RowKeyTable
 */
class RowKeyTable<R, C, V>(
    private val raw: MutableMap<R, MutableMap<C, V>>,
    private val columnBuilder: Builder<MutableMap<C, V>>,
) : AbsTable<R, C, V>() {

    constructor() : this(HashMap(), hashMapBuilder())

    constructor(isLinked: Boolean) : this(
        if (isLinked) linkedMapOf() else HashMap(),
        if (isLinked) linkedMapBuilder() else hashMapBuilder(),
    )

    constructor(raw: MutableMap<R, MutableMap<C, V>>) : this(raw, hashMapBuilder())

    private var columnMapCache: MutableMap<C, MutableMap<R, V>>? = null
    private var columnKeySetCache: MutableSet<C>? = null

    override fun rowMap(): MutableMap<R, MutableMap<C, V>> = raw

    override fun put(rowKey: R, columnKey: C, value: V): V? {
        val row = raw.getOrPut(rowKey) { columnBuilder.build() }
        return row.put(columnKey, value)
    }

    override fun remove(rowKey: R, columnKey: C): V? {
        val row = raw[rowKey] ?: return null
        val value = row.remove(columnKey)
        if (row.isEmpty()) raw.remove(rowKey)
        return value
    }

    override fun isEmpty(): Boolean = raw.isEmpty()

    override fun clear() = raw.clear()

    override fun containsColumn(columnKey: C): Boolean = raw.values.any { it.containsKey(columnKey) }

    override fun columnMap(): MutableMap<C, MutableMap<R, V>> =
        columnMapCache ?: ColumnMap().also { columnMapCache = it }

    override fun columnKeySet(): MutableSet<C> = columnKeySetCache ?: ColumnKeySet().also { columnKeySetCache = it }

    override fun columnKeys(): List<C> {
        val result = ArrayList<C>()
        for (row in raw.values) result.addAll(row.keys)
        return result
    }

    override fun getColumn(columnKey: C): MutableMap<R, V>? =
        if (containsColumn(columnKey)) Column(columnKey) else null

    private inner class ColumnMap : AbstractMutableMap<C, MutableMap<R, V>>() {
        override val size: Int get() = columnKeySet().size

        override fun containsKey(key: C): Boolean = containsColumn(key)

        override fun get(key: C): MutableMap<R, V>? = this@RowKeyTable.getColumn(key)

        override fun put(key: C, value: MutableMap<R, V>): MutableMap<R, V>? =
            throw UnsupportedOperationException("Column map is read only.")

        override val entries: MutableSet<MutableMap.MutableEntry<C, MutableMap<R, V>>>
            get() = object : AbstractMutableSet<MutableMap.MutableEntry<C, MutableMap<R, V>>>() {
                override val size: Int get() = this@ColumnMap.size

                override fun add(element: MutableMap.MutableEntry<C, MutableMap<R, V>>): Boolean =
                    throw UnsupportedOperationException("Column map entries are read only.")


                override fun iterator(): MutableIterator<MutableMap.MutableEntry<C, MutableMap<R, V>>> {
                    val keys = columnKeySet().iterator()
                    return object : MutableIterator<MutableMap.MutableEntry<C, MutableMap<R, V>>> {
                        override fun hasNext(): Boolean = keys.hasNext()

                        override fun next(): MutableMap.MutableEntry<C, MutableMap<R, V>> {
                            val key = keys.next()
                            return ColumnEntry(key)
                        }

                        override fun remove() = keys.remove()
                    }
                }
            }
    }

    private inner class ColumnEntry(private val columnKey: C) : MutableMap.MutableEntry<C, MutableMap<R, V>> {
        override val key: C get() = columnKey

        override val value: MutableMap<R, V> get() = Column(columnKey)

        override fun setValue(newValue: MutableMap<R, V>): MutableMap<R, V> =
            throw UnsupportedOperationException("Column map value cannot be replaced.")
    }

    private inner class ColumnKeySet : AbstractMutableSet<C>() {
        override val size: Int get() = columnKeys().distinct().size

        override fun add(element: C): Boolean = throw UnsupportedOperationException("Column keys are read only.")


        override fun iterator(): MutableIterator<C> {
            val rows = raw.values.iterator()
            var current: Iterator<C> = emptyList<C>().iterator()
            return object : MutableIterator<C> {
                override fun hasNext(): Boolean {
                    while (!current.hasNext()) {
                        if (!rows.hasNext()) return false
                        current = rows.next().keys.iterator()
                    }
                    return true
                }

                override fun next(): C {
                    if (!hasNext()) throw NoSuchElementException()
                    return current.next()
                }

                override fun remove() = throw UnsupportedOperationException()
            }
        }
    }

    private inner class Column(private val columnKey: C) : AbstractMutableMap<R, V>() {
        override val size: Int get() = raw.values.count { it.containsKey(columnKey) }

        override fun containsKey(key: R): Boolean = raw[key]?.containsKey(columnKey) ?: false

        override fun get(key: R): V? = raw[key]?.get(columnKey)

        override fun put(key: R, value: V): V? = this@RowKeyTable.put(key, columnKey, value)

        override fun remove(key: R): V? = this@RowKeyTable.remove(key, columnKey)

        override val entries: MutableSet<MutableMap.MutableEntry<R, V>>
            get() = object : AbstractMutableSet<MutableMap.MutableEntry<R, V>>() {
                override val size: Int get() = this@Column.size

                override fun add(element: MutableMap.MutableEntry<R, V>): Boolean =
                    throw UnsupportedOperationException("Column entries are read only.")


                override fun iterator(): MutableIterator<MutableMap.MutableEntry<R, V>> {
                    val rows = raw.entries.iterator()
                    var nextRow: R? = null
                    var hasNextRow = false
                    return object : MutableIterator<MutableMap.MutableEntry<R, V>> {
                        override fun hasNext(): Boolean {
                            while (!hasNextRow && rows.hasNext()) {
                                val row = rows.next()
                                if (row.value.containsKey(columnKey)) {
                                    nextRow = row.key
                                    hasNextRow = true
                                }
                            }
                            return hasNextRow
                        }

                        override fun next(): MutableMap.MutableEntry<R, V> {
                            if (!hasNext()) throw NoSuchElementException()
                            val entry = RowEntry(nextRow!!, columnKey)
                            hasNextRow = false
                            nextRow = null
                            return entry
                        }

                        override fun remove() = throw UnsupportedOperationException()
                    }
                }
            }
    }

    private inner class RowEntry(private val rowKey: R, private val colKey: C) : MutableMap.MutableEntry<R, V> {
        override val key: R get() = rowKey

        override val value: V get() = raw[rowKey]!![colKey] as V

        override fun setValue(newValue: V): V {
            val old = value
            raw[rowKey]!![colKey] = newValue
            return old
        }
    }

    companion object {
        private fun <K, V> hashMapBuilder(): Builder<MutableMap<K, V>> =
            object : Builder<MutableMap<K, V>> {
                override fun build(): MutableMap<K, V> = HashMap()
            }

        private fun <K, V> linkedMapBuilder(): Builder<MutableMap<K, V>> =
            object : Builder<MutableMap<K, V>> {
                override fun build(): MutableMap<K, V> = linkedMapOf()
            }
    }
}



