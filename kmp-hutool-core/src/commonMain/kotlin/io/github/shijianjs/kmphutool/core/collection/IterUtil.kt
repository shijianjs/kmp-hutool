package io.github.shijianjs.kmphutool.core.collection

/**
 * Iterator and iterable helpers that Hutool keeps apart from [CollUtil].
 *
 * Port of `cn.hutool.core.collection.IterUtil`. Java's `Enumeration`, `Func1`/`Editor`/`Filter`
 * interfaces and map-to-bean conversion (`fieldValueMap`) are not ported: Kotlin uses lambdas and there
 * is no bean reflection in commonMain.
 *
 * @hutool cn.hutool.core.collection.IterUtil
 */
object IterUtil {
    /** `true` when [iterable] is `null` or has no element. */
    fun isEmpty(iterable: Iterable<*>?): Boolean = iterable == null || !iterable.iterator().hasNext()

    /** `true` when [iterable] has at least one element. */
    fun isNotEmpty(iterable: Iterable<*>?): Boolean = !isEmpty(iterable)

    /** `true` when [iterator] is `null` or already exhausted. */
    fun isEmpty(iterator: Iterator<*>?): Boolean = iterator == null || !iterator.hasNext()

    /** `true` when [iterator] has at least one element. */
    fun isNotEmpty(iterator: Iterator<*>?): Boolean = !isEmpty(iterator)

    /** `true` when at least one element is `null`. */
    fun hasNull(iterable: Iterable<*>?): Boolean = iterable?.any { it == null } ?: false

    /** `true` when every element is `null`. */
    fun isAllNull(iterable: Iterable<*>?): Boolean = iterable?.all { it == null } ?: true

    /** The number of elements that [iterator] still yields; consumes it. */
    fun size(iterator: Iterator<*>?): Int {
        var count = 0
        while (iterator != null && iterator.hasNext()) { iterator.next(); count++ }
        return count
    }

    /** The number of elements in [iterable]. */
    fun size(iterable: Iterable<*>?): Int = iterable?.count() ?: 0

    /** The element at [index], or `null` when the iterator ends first. */
    fun <E> get(iterator: Iterator<E>?, index: Int): E? {
        if (iterator == null || index < 0) return null
        var current = 0
        while (iterator.hasNext()) {
            val element = iterator.next()
            if (current == index) return element
            current++
        }
        return null
    }

    /** The first element, `null` for an empty iterator. */
    fun <T> getFirst(iterable: Iterable<T>?): T? = iterable?.firstOrNull()

    /** The first non-null element, or `null`. */
    fun <T : Any> getFirstNoneNull(iterable: Iterable<T?>?): T? = iterable?.firstOrNull { it != null }

    /** The first element satisfying [matcher], or `null`. */
    fun <T> firstMatch(iterable: Iterable<T>?, matcher: (T) -> Boolean): T? = iterable?.firstOrNull(matcher)

    /** The elements satisfying [matcher], as a list. */
    fun <E> filterToList(iterable: Iterable<E>?, filter: (E) -> Boolean): List<E> =
        iterable?.filter(filter) ?: emptyList()

    /** A lazy sequence of the elements satisfying [filter]. */
    fun <E> filtered(iterator: Iterator<E>?, filter: (E) -> Boolean): Iterator<E> {
        val source = iterator ?: return emptyList<E>().iterator()
        val result = mutableListOf<E>()
        while (source.hasNext()) {
            val element = source.next()
            if (filter(element)) result.add(element)
        }
        return result.iterator()
    }

    /** The iterator as a list. */
    fun <E> toList(iterator: Iterator<E>?): List<E> {
        val result = mutableListOf<E>()
        while (iterator != null && iterator.hasNext()) result.add(iterator.next())
        return result
    }

    /** The iterable as a list. */
    fun <E> toList(iterable: Iterable<E>?): List<E> = iterable?.toList() ?: emptyList()

    /** A map from the elements of [iterable], keyed by [keyMapper]; later keys win. */
    fun <V, K> toMap(iterable: Iterable<V>?, keyMapper: (V) -> K): Map<K, V> {
        if (iterable == null) return emptyMap()
        val result = LinkedHashMap<K, V>()
        iterable.forEach { result[keyMapper(it)] = it }
        return result
    }

    /** A map built by [keyMapper] and [valueMapper] from the elements of [iterable]. */
    fun <T, K, V> toMap(iterable: Iterable<T>?, keyMapper: (T) -> K, valueMapper: (T) -> V): Map<K, V> {
        if (iterable == null) return emptyMap()
        val result = LinkedHashMap<K, V>()
        iterable.forEach { result[keyMapper(it)] = valueMapper(it) }
        return result
    }

    /** Groups the elements of [iterable] into a map of lists, keyed by [keyMapper]. */
    fun <T, K, V> toListMap(iterable: Iterable<T>?, keyMapper: (T) -> K, valueMapper: (T) -> V): Map<K, List<V>> {
        if (iterable == null) return emptyMap()
        val result = LinkedHashMap<K, MutableList<V>>()
        iterable.forEach { element ->
            result.getOrPut(keyMapper(element)) { mutableListOf() }.add(valueMapper(element))
        }
        return result
    }

    /** Joins the elements of [iterable] with [conjunction]. */
    fun <T> join(iterable: Iterable<T>?, conjunction: CharSequence): String =
        iterable?.joinToString(conjunction.toString()) ?: ""

    /** Joins the elements of [iterable] with [conjunction], wrapped in [prefix] and [suffix]. */
    fun <T> join(
        iterable: Iterable<T>?,
        conjunction: CharSequence,
        prefix: CharSequence,
        suffix: CharSequence,
    ): String = if (iterable == null) "" else iterable.joinToString(conjunction.toString(), prefix, suffix)

    /** Joins the elements after mapping each with [func]. */
    fun <T> join(
        iterable: Iterable<T>?,
        conjunction: CharSequence,
        func: (T) -> CharSequence?,
    ): String = iterable?.joinToString(conjunction.toString()) { func(it).toString() } ?: ""

    /** Applies [action] to every element and returns [iterable] for chaining. */
    fun <T> forEach(iterable: Iterable<T>?, action: (T) -> Unit): Iterable<T>? {
        iterable?.forEach(action)
        return iterable
    }

    /** Empties any collection passed in, then returns it. */
    fun <T : MutableCollection<*>> clear(collection: T?): T? {
        collection?.clear()
        return collection
    }

    /** `true` when both iterables yield equal elements in the same order. */
    fun isEqualList(list1: Iterable<*>?, list2: Iterable<*>?): Boolean {
        if (list1 == null || list2 == null) return list1 == null && list2 == null
        val iterator1 = list1.iterator()
        val iterator2 = list2.iterator()
        while (iterator1.hasNext() && iterator2.hasNext()) {
            if (iterator1.next() != iterator2.next()) return false
        }
        return !iterator1.hasNext() && !iterator2.hasNext()
    }

    /** Maps every element with [function], eagerly, into a list. */
    fun <F, T> trans(iterable: Iterable<F>?, function: (F) -> T): List<T> = iterable?.map(function) ?: emptyList()

    /** The string form of the elements, comma separated and without brackets. */
    fun <E> toStr(iterable: Iterable<E>?): String = iterable?.joinToString(",") ?: ""

    /** The string form of the elements after mapping each with [transFunc]. */
    fun <E> toStr(iterable: Iterable<E>?, transFunc: (E) -> String?): String =
        iterable?.joinToString(",") { transFunc(it) ?: "null" } ?: ""
}
