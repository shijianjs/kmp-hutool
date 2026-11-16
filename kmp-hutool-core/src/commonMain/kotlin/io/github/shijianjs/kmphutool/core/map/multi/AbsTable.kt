package io.github.shijianjs.kmphutool.core.map.multi

/**
 * Base [Table] implementation supplying `equals`, `hashCode`, `toString`, [values] and [cellSet].
 *
 * Port of `cn.hutool.core.map.multi.AbsTable` (adapted from Guava). The Kotlin port builds the views
 * from a [Cell] iterator rather than the Java `AbstractCollection`/`AbstractSet` machinery.
 *
 * @hutool cn.hutool.core.map.multi.AbsTable
 */
abstract class AbsTable<R, C, V> : Table<R, C, V> {
    private var valuesCache: MutableCollection<V>? = null
    private var cellSetCache: MutableSet<Table.Cell<R, C, V>>? = null

    override fun equals(other: Any?): Boolean {
        if (other === this) return true
        if (other !is Table<*, *, *>) return false
        return cellSet() == other.cellSet()
    }

    override fun hashCode(): Int = cellSet().hashCode()

    override fun toString(): String = rowMap().toString()

    override fun values(): MutableCollection<V> = valuesCache ?: Values().also { valuesCache = it }

    override fun cellSet(): MutableSet<Table.Cell<R, C, V>> = cellSetCache ?: CellSet().also { cellSetCache = it }

    override fun iterator(): MutableIterator<Table.Cell<R, C, V>> = CellIterator()

    private inner class Values : AbstractMutableCollection<V>() {
        override val size: Int get() = this@AbsTable.size()

        override fun iterator(): MutableIterator<V> = object : MutableIterator<V> {
            private val backing = cellSet().iterator()

            override fun hasNext(): Boolean = backing.hasNext()

            override fun next(): V = backing.next().value

            override fun remove() = backing.remove()
        }

        override fun contains(element: V): Boolean = containsValue(element)

        override fun add(element: V): Boolean = throw UnsupportedOperationException("Table values are read only.")

        override fun clear() = this@AbsTable.clear()
    }

    private inner class CellSet : AbstractMutableSet<Table.Cell<R, C, V>>() {
        override val size: Int get() = this@AbsTable.size()

        override fun iterator(): MutableIterator<Table.Cell<R, C, V>> = CellIterator()

        override fun add(element: Table.Cell<R, C, V>): Boolean = throw UnsupportedOperationException("Table cells are read only.")

        override fun contains(element: Table.Cell<R, C, V>): Boolean {
            val row = getRow(element.rowKey) ?: return false
            return row[element.columnKey] == element.value
        }

        override fun remove(element: Table.Cell<R, C, V>): Boolean {
            if (!contains(element)) return false
            this@AbsTable.remove(element.rowKey, element.columnKey)
            return true
        }

        override fun clear() = this@AbsTable.clear()
    }

    /** Walks every cell row by row. */
    private inner class CellIterator : MutableIterator<Table.Cell<R, C, V>> {
        private val rowIterator = rowMap().entries.iterator()
        private var rowEntry: MutableMap.MutableEntry<R, MutableMap<C, V>>? = null
        private var columnIterator: MutableIterator<MutableMap.MutableEntry<C, V>> = mutableListOf<MutableMap.MutableEntry<C, V>>().iterator()

        override fun hasNext(): Boolean = rowIterator.hasNext() || columnIterator.hasNext()

        override fun next(): Table.Cell<R, C, V> {
            if (!columnIterator.hasNext()) {
                rowEntry = rowIterator.next()
                columnIterator = rowEntry!!.value.entries.iterator()
            }
            val columnEntry = columnIterator.next()
            return SimpleCell(rowEntry!!.key, columnEntry.key, columnEntry.value)
        }

        override fun remove() {
            columnIterator.remove()
            if (rowEntry!!.value.isEmpty()) rowIterator.remove()
        }
    }

    private class SimpleCell<R, C, V>(
        override val rowKey: R,
        override val columnKey: C,
        override val value: V,
    ) : Table.Cell<R, C, V> {
        override fun equals(other: Any?): Boolean = other is Table.Cell<*, *, *> &&
            rowKey == other.rowKey && columnKey == other.columnKey && value == other.value

        override fun hashCode(): Int {
            var result = rowKey?.hashCode() ?: 0
            result = 31 * result + (columnKey?.hashCode() ?: 0)
            result = 31 * result + (value?.hashCode() ?: 0)
            return result
        }

        override fun toString(): String = "($rowKey,$columnKey)=$value"
    }
}


