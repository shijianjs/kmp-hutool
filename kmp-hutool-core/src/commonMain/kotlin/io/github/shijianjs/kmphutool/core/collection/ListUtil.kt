package io.github.shijianjs.kmphutool.core.collection

/**
 * List helpers: creation, paging, sorting and positional edits.
 *
 * Port of `cn.hutool.core.collection.ListUtil`. Methods that need Java's `Enumeration`,
 * `CopyOnWriteArrayList` or `Comparator` over arbitrary property names (`sortByProperty`,
 * `sortByPinyin`) are not ported: there is no reflection to read a property by name in commonMain, and
 * Kotlin sorts with a lambda.
 *
 * @hutool cn.hutool.core.collection.ListUtil
 */
object ListUtil {
    /** An empty list. */
    fun <T> empty(): MutableList<T> = mutableListOf()

    /** A new mutable list, insertion-ordered ([isLinked] is meaningless in Kotlin and kept for parity). */
    fun <T> list(isLinked: Boolean = false): MutableList<T> = mutableListOf()

    /** A mutable list of the given values. */
    fun <T> of(vararg values: T): MutableList<T> = values.toMutableList()

    /** A mutable list copy of [collection]. */
    fun <T> toList(collection: Collection<T>?): MutableList<T> =
        collection?.toMutableList() ?: mutableListOf()

    /** A mutable list copy of [iterable]. */
    fun <T> toList(iterable: Iterable<T>?): MutableList<T> =
        iterable?.toMutableList() ?: mutableListOf()

    /** A mutable list of the values [iterator] yields. */
    fun <T> toList(iterator: Iterator<T>?): MutableList<T> {
        val result = mutableListOf<T>()
        while (iterator != null && iterator.hasNext()) result.add(iterator.next())
        return result
    }

    /** A snapshot of [list], or an empty list for `null`. */
    fun <T> unmodifiable(list: List<T>?): List<T> = list?.toList() ?: emptyList()

    /**
     * One page of [list], 1-based as Hutool's `page(pageNo, pageSize, list)`.
     * A page outside the range yields an empty list.
     */
    fun <T> page(pageNo: Int, pageSize: Int, list: List<T>?): List<T> {
        if (list == null || pageSize <= 0 || pageNo < 1) return emptyList()
        val from = (pageNo - 1) * pageSize
        if (from >= list.size) return emptyList()
        return list.subList(from, minOf(from + pageSize, list.size)).toList()
    }

    /**
     * Sorts [list] in place with [comparator] and returns it. Null-safe: a `null` list stays `null`.
     */
    fun <T> sort(list: MutableList<T>?, comparator: Comparator<in T>): MutableList<T>? {
        list?.sortWith(comparator)
        return list
    }

    /** Reverses [list] in place and returns it. */
    fun <T> reverse(list: MutableList<T>?): MutableList<T>? {
        list?.reverse()
        return list
    }

    /** A reversed copy of [list]. */
    fun <T> reverseNew(list: List<T>?): MutableList<T> {
        val result = list?.toMutableList() ?: mutableListOf()
        result.reverse()
        return result
    }

    /** Sets [element] at [index], or appends it when [index] is out of range. */
    fun <T> setOrAppend(list: MutableList<T>?, index: Int, element: T): MutableList<T> {
        val target = list ?: mutableListOf()
        if (index < target.size) {
            target[index] = element
        } else {
            target.add(element)
        }
        return target
    }

    /**
     * Sets [element] at [index], padding the list with [paddingElement] when it is too short.
     *
     * @param indexLimit the maximum index that will be padded to; larger indices just append
     */
    fun <T> setOrPadding(
        list: MutableList<T>?,
        index: Int,
        element: T,
        paddingElement: T? = null,
        indexLimit: Int = Int.MAX_VALUE,
    ): MutableList<T> {
        val target = list ?: mutableListOf()
        if (index < 0) return target
        if (index < target.size) {
            target[index] = element
            return target
        }
        if (index > indexLimit) {
            target.add(element)
            return target
        }
        while (target.size < index) target.add(paddingElement as T)
        target.add(element)
        return target
    }

    /** The sub-list `[start, end)`, clamped to the list, stepping [step] elements at a time. */
    fun <T> sub(list: List<T>?, start: Int, end: Int, step: Int = 1): List<T> {
        if (list == null || step <= 0) return emptyList()
        val from = start.coerceIn(0, list.size)
        val to = end.coerceIn(0, list.size)
        if (to <= from) return emptyList()
        val result = mutableListOf<T>()
        var index = from
        while (index < to) {
            result.add(list[index])
            index += step
        }
        return result
    }

    /** Every index whose element satisfies [matcher]. */
    fun <T> indexOfAll(list: List<T>?, matcher: (T) -> Boolean): List<Int> {
        if (list == null) return emptyList()
        val result = mutableListOf<Int>()
        for (index in list.indices) if (matcher(list[index])) result.add(index)
        return result
    }

    /** The last index whose element satisfies [matcher], `-1` when none does. */
    fun <T> lastIndexOf(list: List<T>?, matcher: (T) -> Boolean): Int {
        if (list == null) return -1
        for (index in list.indices.reversed()) if (matcher(list[index])) return index
        return -1
    }

    /** Splits [list] into consecutive pages of [size] elements. */
    fun <T> partition(list: List<T>?, size: Int): List<List<T>> {
        if (list == null || size <= 0) return emptyList()
        return list.chunked(size)
    }

    /** Splits [list] into consecutive parts of [size] elements; alias of [partition]. */
    fun <T> split(list: List<T>?, size: Int): List<List<T>> = partition(list, size)

    /**
     * Splits [list] into [limit] parts of as equal a size as possible; the first
     * `list.size % limit` parts get one extra element.
     */
    fun <T> splitAvg(list: List<T>?, limit: Int): List<List<T>> {
        if (list == null || list.isEmpty() || limit <= 0) return emptyList()
        val result = mutableListOf<List<T>>()
        val size = list.size
        val avg = size / limit
        val remainder = size % limit
        var from = 0
        for (index in 0 until limit) {
            val count = avg + if (index < remainder) 1 else 0
            if (count == 0) break
            result.add(list.subList(from, from + count).toList())
            from += count
        }
        return result
    }

    /** Moves [element] to [targetIndex]; a missing element or index leaves the list unchanged. */
    fun <T> swapTo(list: MutableList<T>?, element: T, targetIndex: Int): MutableList<T>? {
        if (list == null) return null
        val index = list.indexOf(element)
        if (index < 0) return list
        return move(list, index, targetIndex)
    }

    /** Swaps [element] with [targetElement]; either missing leaves the list unchanged. */
    fun <T> swapElement(list: MutableList<T>?, element: T, targetElement: T): MutableList<T>? {
        if (list == null) return null
        val index1 = list.indexOf(element)
        val index2 = list.indexOf(targetElement)
        if (index1 < 0 || index2 < 0) return list
        val tmp = list[index1]
        list[index1] = list[index2]
        list[index2] = tmp
        return list
    }

    /** Moves the element at [index] to [newPosition], shifting the rest. */
    fun <T> move(list: MutableList<T>?, index: Int, newPosition: Int): MutableList<T>? {
        if (list == null || index !in list.indices) return list
        val element = list.removeAt(index)
        list.add(newPosition.coerceIn(0, list.size), element)
        return list
    }

    /** Pairs [listA] and [listB] with [zipper], stopping at the shorter list. */
    fun <A, B, R> zip(listA: List<A>?, listB: List<B>?, zipper: (A, B) -> R): List<R> {
        if (listA == null || listB == null) return emptyList()
        return (0 until minOf(listA.size, listB.size)).map { zipper(listA[it], listB[it]) }
    }

    /** A mutable list of the given [values]. */
    fun <T> toLinkedList(vararg values: T): MutableList<T> = values.toMutableList()
}
