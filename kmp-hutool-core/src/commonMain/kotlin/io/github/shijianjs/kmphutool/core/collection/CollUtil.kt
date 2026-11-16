package io.github.shijianjs.kmphutool.core.collection

import io.github.shijianjs.kmphutool.core.util.ObjectUtil
import io.github.shijianjs.kmphutool.core.util.StrUtil

/**
 * Collection operations that operate on any [Collection], whatever its implementation.
 *
 * Port of `cn.hutool.core.collection.CollUtil` (which Hutool's own `CollectionUtil` only extends).
 * Members that depend on Java-only types or reflection — `newCopyOnWriteArrayList`,
 * `newBlockingQueue`, `asEnumeration`, `sortByPinyin`, `sortByProperty`, `getElementType`,
 * `fieldValueMap` and the `Func`/`Editor`/`Filter` overloads — are not ported; a Kotlin lambda does the
 * same job without a type token.
 *
 * @hutool cn.hutool.core.collection.CollUtil
 * @hutool cn.hutool.core.collection.CollectionUtil
 */
object CollUtil {
    /** `true` when [collection] is `null` or empty. */
    fun isEmpty(collection: Collection<*>?): Boolean = collection == null || collection.isEmpty()

    /** `true` when [collection] has at least one element. */
    fun isNotEmpty(collection: Collection<*>?): Boolean = !isEmpty(collection)

    /** An empty mutable list. */
    fun <T> empty(): MutableList<T> = mutableListOf()

    /** An empty mutable list; [isLinked] is kept for parity and has no effect in Kotlin. */
    fun <T> create(isLinked: Boolean = false): MutableList<T> = mutableListOf()

    /** A new mutable list. */
    fun <T> newArrayList(): MutableList<T> = mutableListOf()

    /** A new mutable set, keeping insertion order. */
    fun <T> newHashSet(): MutableSet<T> = mutableSetOf()

    /** A new mutable set, keeping insertion order. */
    fun <T> newLinkedHashSet(): MutableSet<T> = linkedSetOf()

    /** A new mutable list, the Kotlin spelling of `newLinkedList`. */
    fun <T> newLinkedList(): MutableList<T> = mutableListOf()

    /** `[collection]` or an empty list when it is `null`. */
    fun <T> emptyIfNull(collection: Collection<T>?): Collection<T> = collection ?: emptyList()

    /** `[collection]` or an empty list when it is `null`, typed as a list. */
    fun <T> defaultIfEmpty(collection: List<T>?, defaultValue: List<T>): List<T> =
        if (isEmpty(collection)) defaultValue else collection!!

    /** Adds every element of [values] to [collection], ignoring `null` elements. */
    fun <T> addAll(collection: MutableCollection<T>?, vararg values: T?): Boolean {
        if (collection == null || values.isEmpty()) return false
        var changed = false
        for (value in values) {
            if (value != null) changed = collection.add(value) || changed
        }
        return changed
    }

    /** Adds [value] when [collection] does not contain it. */
    fun <T> addIfAbsent(collection: MutableCollection<T>?, value: T?): Boolean {
        if (collection == null || value == null) return false
        if (collection.contains(value)) return false
        return collection.add(value)
    }

    /** Adds every element of [values] that [collection] does not contain yet. */
    fun <T> addAllIfNotContains(collection: MutableCollection<T>?, vararg values: T?): Boolean {
        if (collection == null) return false
        var changed = false
        for (value in values) {
            if (value == null) continue
            if (!collection.contains(value)) changed = collection.add(value) || changed
        }
        return changed
    }

    /** `true` when [collection] contains [value]. */
    fun contains(collection: Collection<*>?, value: Any?): Boolean =
        collection != null && collection.contains(value)

    /** `true` when [collection] contains [value], or is empty and [value] is `null`. */
    fun safeContains(collection: Collection<*>?, value: Any?): Boolean =
        if (isEmpty(collection)) value == null else collection!!.contains(value)

    /** `true` when [collection] contains every value in [values]. */
    fun containsAll(collection: Collection<*>?, vararg values: Any?): Boolean =
        collection != null && values.all { collection.contains(it) }

    /** `true` when [collection] contains at least one value in [values]. */
    fun containsAny(collection: Collection<*>?, vararg values: Any?): Boolean =
        collection != null && values.any { collection.contains(it) }

    /** The element at [index], or `null` when out of range. */
    fun <T> get(collection: List<T>?, index: Int): T? {
        if (collection == null || index < 0 || index >= collection.size) return null
        return collection[index]
    }

    /** The element at [index], or [defaultValue] when out of range. */
    fun <T> get(collection: List<T>?, index: Int, defaultValue: T?): T? = get(collection, index) ?: defaultValue

    /** The first element, `null` when empty. */
    fun <E> getFirst(iterable: Iterable<E>?): E? = iterable?.firstOrNull()

    /** The last element, `null` when empty. */
    fun <E> getLast(iterable: Iterable<E>?): E? {
        if (iterable == null) return null
        return when (iterable) {
            is List<E> -> iterable.lastOrNull()
            else -> iterable.lastOrNull()
        }
    }

    /** Some element of [collection], `null` when empty. */
    fun <T> getAny(collection: Collection<T>?): T? = if (isEmpty(collection)) null else collection!!.first()

    /** Joins the elements with [conjunction], surrounded by [prefix] and [suffix]. */
    fun <T> join(
        collection: Collection<T>?,
        conjunction: CharSequence,
        prefix: CharSequence = "",
        suffix: CharSequence = "",
    ): String = collection?.joinToString(conjunction.toString(), prefix, suffix) ?: ""

    /** The distinct elements of [collection], keeping the first occurrence and its order. */
    fun <T> distinct(collection: Collection<T>?): List<T> = collection?.distinct() ?: emptyList()

    /**
     * The union of both collections, keeping every element of [a] and then every element of [b] that
     * [a] did not already contain.
     */
    /**
     * The union of [a] and [b], element counts combined like an SQL `UNION ALL` over multisets: an
     * element present three times on one side and once on the other appears three times.
     * Order follows the first appearance of each element, as Hutool does.
     */
    fun <T> union(a: Collection<T>?, b: Collection<T>?): List<T> {
        if (isEmpty(a) && isEmpty(b)) return emptyList()
        if (isEmpty(a)) return b!!.toList()
        if (isEmpty(b)) return a!!.toList()
        val counts = linkedMapOf<T, Int>()
        a!!.forEach { counts[it] = (counts[it] ?: 0) + 1 }
        b!!.forEach { element ->
            val otherCount = b.count { it == element }
            counts[element] = maxOf(counts[element] ?: 0, otherCount)
        }
        val result = mutableListOf<T>()
        counts.forEach { (element, count) -> repeat(count) { result.add(element) } }
        return result
    }

    /** All elements of both collections, duplicates included. */
    fun <T> unionAll(a: Collection<T>?, b: Collection<T>?): List<T> {
        val result = mutableListOf<T>()
        a?.let { result.addAll(it) }
        b?.let { result.addAll(it) }
        return result
    }

    /** The distinct union of both collections. */
    fun <T> unionDistinct(a: Collection<T>?, b: Collection<T>?): List<T> = union(a, b).distinct()

    /** The elements present in both collections. */
    fun <T> intersection(a: Collection<T>?, b: Collection<T>?): List<T> {
        if (a == null || b == null) return emptyList()
        return a.filter { b.contains(it) }
    }

    /** The distinct elements present in both collections. */
    fun <T> intersectionDistinct(a: Collection<T>?, b: Collection<T>?): List<T> = intersection(a, b).distinct()

    /** The elements of [a] that [b] does not contain. */
    fun <T> subtract(a: Collection<T>?, b: Collection<T>?): List<T> {
        if (a == null) return emptyList()
        if (b == null) return a.toList()
        return a.filter { !b.contains(it) }
    }

    /** [subtract] into [list]. */
    fun <T> subtractToList(a: Collection<T>?, b: Collection<T>?, list: MutableList<T>): MutableList<T> {
        list.addAll(subtract(a, b))
        return list
    }

    /** The elements that belong to exactly one of the two collections. */
    fun <T> disjunction(a: Collection<T>?, b: Collection<T>?): List<T> {
        val result = subtract(a, b).toMutableList()
        result.addAll(subtract(b, a))
        return result
    }

    /** The elements of [collection] satisfying [filter]. */
    fun <T> filter(collection: Collection<T>?, filter: (T) -> Boolean): List<T> =
        collection?.filter(filter) ?: emptyList()

    /** `true` when every element satisfies [matcher]. */
    fun <T> allMatch(collection: Collection<T>?, matcher: (T) -> Boolean): Boolean =
        collection?.all(matcher) ?: false

    /** `true` when at least one element satisfies [matcher]. */
    fun <T> anyMatch(collection: Collection<T>?, matcher: (T) -> Boolean): Boolean =
        collection?.any(matcher) ?: false

    /** The first element satisfying [matcher], or `null`. */
    fun <T> findOne(collection: Collection<T>?, matcher: (T) -> Boolean): T? = collection?.firstOrNull(matcher)

    /** A map from element to the number of its occurrences. */
    fun <T> countMap(collection: Collection<T>?): Map<T, Int> {
        if (collection == null) return emptyMap()
        val result = LinkedHashMap<T, Int>()
        collection.forEach { result[it] = (result[it] ?: 0) + 1 }
        return result
    }

    /** Groups the elements by [keyMapper], preserving encounter order. */
    fun <T, K> group(collection: Collection<T>?, keyMapper: (T) -> K): Map<K, List<T>> =
        collection?.groupBy(keyMapper) ?: emptyMap()

    /** The sub-collection `[start, end)`, clamped to the input. */
    fun <T> sub(collection: List<T>?, start: Int, end: Int): List<T> {
        if (collection == null) return emptyList()
        val from = start.coerceIn(0, collection.size)
        val to = end.coerceIn(0, collection.size)
        return if (to <= from) emptyList() else collection.subList(from, to).toList()
    }

    /** One page of [collection], 1-based; see [ListUtil.page]. */
    fun <T> page(pageNo: Int, pageSize: Int, collection: List<T>?): List<T> =
        ListUtil.page(pageNo, pageSize, collection)

    /** Splits [collection] into parts of [size] elements. */
    fun <T> split(collection: List<T>?, size: Int): List<List<T>> = ListUtil.partition(collection, size)

    /** Splits [collection] into [limit] parts of nearly equal size. */
    fun <T> splitList(collection: List<T>?, limit: Int): List<List<T>> = ListUtil.splitAvg(collection, limit)

    /** Sorts [collection] in place with [comparator]. */
    fun <T> sort(collection: MutableList<T>?, comparator: Comparator<in T>): MutableList<T>? =
        ListUtil.sort(collection, comparator)

    /** Reverses [collection] in place. */
    fun <T> reverse(collection: MutableList<T>?): MutableList<T>? = ListUtil.reverse(collection)

    /** A reversed copy of [collection]. */
    fun <T> reverseNew(collection: List<T>?): MutableList<T> = ListUtil.reverseNew(collection)

    /** Removes every `null` element from [collection]. */
    fun <T> removeNull(collection: MutableCollection<T?>?): MutableCollection<T?>? {
        collection?.removeAll { it == null }
        return collection
    }

    /** Removes every empty element (`null`, blank text, empty collection) from [collection]. */
    fun <T> removeEmpty(collection: MutableCollection<T?>?): MutableCollection<T?>? {
        collection?.removeAll { ObjectUtil.isEmpty(it) }
        return collection
    }

    /** Removes every blank string from [collection]. */
    fun <T> removeBlank(collection: MutableCollection<T?>?): MutableCollection<T?>? {
        collection?.removeAll { StrUtil.isBlankIfStr(it) }
        return collection
    }

    /** Removes every occurrence of each argument from [collection]. */
    fun <T> removeAny(collection: MutableCollection<T?>?, vararg values: T?): MutableCollection<T?>? {
        if (collection != null) {
            for (value in values) collection.remove(value)
        }
        return collection
    }

    /** Removes the elements that [filter] rejects. */
    fun <T> removeIf(collection: MutableCollection<T>?, filter: (T) -> Boolean): Boolean =
        collection?.removeAll(filter) ?: false

    /** The largest element, or `null` when empty. */
    fun <T : Comparable<T>> max(collection: Collection<T>?): T? = collection?.maxOrNull()

    /** The smallest element, or `null` when empty. */
    fun <T : Comparable<T>> min(collection: Collection<T>?): T? = collection?.minOrNull()

    /** The number of elements, 0 for `null`. */
    fun size(collection: Collection<*>?): Int = collection?.size ?: 0

    /** `true` when both collections hold equal elements in the same order. */
    fun isEqualList(list1: Collection<*>?, list2: Collection<*>?): Boolean =
        if (list1 == null || list2 == null) list1 == null && list2 == null else list1.toList() == list2.toList()

    /** A read-only view of [collection]; Kotlin lists are immutable interfaces already. */
    fun <T> unmodifiable(collection: Collection<T>?): List<T> = collection?.toList() ?: emptyList()

    /** Pairs [listA] and [listB] with [zipper], stopping at the shorter list. */
    fun <A, B, R> zip(listA: List<A>?, listB: List<B>?, zipper: (A, B) -> R): List<R> =
        ListUtil.zip(listA, listB, zipper)

    /** Every index whose element satisfies [matcher]. */
    fun <T> indexOfAll(collection: List<T>?, matcher: (T) -> Boolean): List<Int> =
        ListUtil.indexOfAll(collection, matcher)

    /** The last index whose element satisfies [matcher]. */
    fun <T> lastIndexOf(collection: List<T>?, matcher: (T) -> Boolean): Int =
        ListUtil.lastIndexOf(collection, matcher)

    /** Sets [value] at [index], appending when the index is out of range. */
    fun <T> setOrAppend(collection: MutableList<T>?, index: Int, value: T): MutableList<T> =
        ListUtil.setOrAppend(collection, index, value)

    /** `true` when any element is `null`. */
    fun hasNull(collection: Collection<*>?): Boolean = collection?.any { it == null } ?: false
}
