package io.github.shijianjs.kmphutool.core.collection

/**
 * Snapshots an iterator into a list so later modification of the source cannot invalidate it.
 *
 * Port of `cn.hutool.core.collection.CopiedIter`. Removal is refused because the copy is read-only.
 *
 * @hutool cn.hutool.core.collection.CopiedIter
 */
class CopiedIter<E>(iterator: Iterator<E>) : Iterator<E> {
    private val listIterator: Iterator<E> = ListUtil.toList(iterator).iterator()

    override fun hasNext(): Boolean = listIterator.hasNext()

    override fun next(): E = listIterator.next()
    companion object {
        /** Builds a copy of [iterator]. */
        fun <E> copyOf(iterator: Iterator<E>): CopiedIter<E> = CopiedIter(iterator)
    }
}
