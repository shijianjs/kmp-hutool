package io.github.shijianjs.kmphutool.core.map.multi

import io.github.shijianjs.kmphutool.core.lang.func.Consumer3

/**
 * A table: two keys (row and column) map to one value.
 *
 * Port of `cn.hutool.core.map.multi.Table`. Default members keep the upstream behaviour: a missing row
 * or column yields `null`/`false` instead of throwing.
 *
 * @hutool cn.hutool.core.map.multi.Table
 */
interface Table<R, C, V> : Iterable<Table.Cell<R, C, V>> {
    /** Whether a cell exists; a present cell holding `null` still counts. */
    fun contains(rowKey: R, columnKey: C): Boolean = getRow(rowKey)?.containsKey(columnKey) ?: false

    /** Whether [rowKey] has a row. */
    fun containsRow(rowKey: R): Boolean = rowMap().containsKey(rowKey)

    /** The column-to-value map of [rowKey], or `null`. */
    fun getRow(rowKey: R): Map<C, V>? = rowMap()[rowKey]

    /** All row keys. */
    fun rowKeySet(): Set<R> = rowMap().keys

    /** Row key to column map. */
    fun rowMap(): MutableMap<R, MutableMap<C, V>>

    /** Whether any row has [columnKey]. */
    fun containsColumn(columnKey: C): Boolean = columnMap().containsKey(columnKey)

    /** The row-to-value map of [columnKey], or `null`. */
    fun getColumn(columnKey: C): Map<R, V>? = columnMap()[columnKey]

    /** All column keys. */
    fun columnKeySet(): Set<C> = columnMap().keys

    /** All column keys in iteration order (duplicates possible for multi-maps). */
    fun columnKeys(): List<C> {
        val map = columnMap()
        if (map.isEmpty()) return emptyList()
        return map.keys.toList()
    }

    /** Column key to row map. */
    fun columnMap(): MutableMap<C, MutableMap<R, V>>

    /** Whether any cell holds [value]. */
    fun containsValue(value: V?): Boolean = rowMap().values.any { it.containsValue(value) }

    /** The value at ([rowKey], [columnKey]), or `null`. */
    fun get(rowKey: R, columnKey: C): V? = getRow(rowKey)?.get(columnKey)

    /** All values. */
    fun values(): MutableCollection<V>

    /** All cells. */
    fun cellSet(): MutableSet<Cell<R, C, V>>

    /** Sets a cell, returning the previous value. */
    fun put(rowKey: R, columnKey: C, value: V): V?

    /** Copies every cell of [table] into this table. */
    fun putAll(table: Table<R, C, V>?) {
        if (table == null) return
        for (cell in table.cellSet()) put(cell.rowKey, cell.columnKey, cell.value)
    }

    /** Removes a cell, returning its previous value. */
    fun remove(rowKey: R, columnKey: C): V?

    /** Whether the table holds no cell. */
    fun isEmpty(): Boolean

    /** The number of cells. */
    fun size(): Int = rowMap().values.sumOf { it.size }

    /** Removes every cell. */
    fun clear()

    /** Visits every cell in iteration order. */
    fun forEach(consumer: Consumer3<R, C, V>) {
        for (cell in this) consumer.accept(cell.rowKey, cell.columnKey, cell.value)
    }

    /** One table cell. */
    interface Cell<R, C, V> {
        val rowKey: R
        val columnKey: C
        val value: V
    }
}
