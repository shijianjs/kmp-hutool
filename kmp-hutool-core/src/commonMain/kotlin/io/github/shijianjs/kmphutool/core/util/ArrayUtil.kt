package io.github.shijianjs.kmphutool.core.util

/**
 * Operations on Kotlin arrays that Hutool provides for Java's `Object[]`.
 *
 * Port of `cn.hutool.core.util.ArrayUtil`. The Java class allocates arrays reflectively
 * (`Array.newInstance`); the port takes the element type from an `inline reified` parameter instead.
 * Members that exist only for Java's primitive arrays plus `Object`-to-primitive casts (`cast`,
 * `getComponentType`, `getArrayType`, `wrap` of primitives, `toArray` of arbitrary collections) are
 * not ported: Kotlin arrays already carry their element type.
 *
 * @hutool cn.hutool.core.util.ArrayUtil
 */
object ArrayUtil {
    /** `true` when [array] is `null` or empty. */
    fun isEmpty(array: Array<*>?): Boolean = array == null || array.isEmpty()

    /** `true` when [array] is non-null and has at least one element. */
    fun isNotEmpty(array: Array<*>?): Boolean = !isEmpty(array)

    /** The length of [array], 0 for `null`. */
    fun length(array: Array<*>?): Int = array?.size ?: 0

    /** `true` when [array] contains [value]. */
    fun <T> contains(array: Array<T>?, value: T): Boolean = array?.any { it == value } ?: false

    /** `true` when [array] contains [value] ignoring case; `null` elements never match. */
    fun containsIgnoreCase(array: Array<String?>?, value: String?): Boolean {
        if (array == null || value == null) return false
        return array.any { it != null && it.equals(value, ignoreCase = true) }
    }

    /** `true` when [array] contains every value in [values]. */
    fun <T> containsAll(array: Array<T>?, vararg values: T): Boolean =
        array != null && values.all { array.contains(it) }

    /** `true` when [array] contains at least one value in [values]. */
    fun <T> containsAny(array: Array<T>?, vararg values: T): Boolean =
        array != null && values.any { array.contains(it) }

    /** The index of [value] in [array], `-1` when absent. */
    fun <T> indexOf(array: Array<T>?, value: T): Int = array?.indexOf(value) ?: -1

    /** The index of [value] ignoring case, `-1` when absent. */
    fun indexOfIgnoreCase(array: Array<String?>?, value: String?): Int {
        if (array == null || value == null) return -1
        for (index in array.indices) {
            val element = array[index] ?: continue
            if (element.equals(value, ignoreCase = true)) return index
        }
        return -1
    }

    /** The last index of [value] in [array], `-1` when absent. */
    fun <T> lastIndexOf(array: Array<T>?, value: T): Int = array?.lastIndexOf(value) ?: -1

    /** The element at [index], or [defaultValue] when the index is outside the array. */
    fun <T> get(array: Array<T>?, index: Int, defaultValue: T? = null): T? {
        if (array == null || index < 0 || index >= array.size) return defaultValue
        return array[index]
    }

    /** An element of [array] at random, `null` for an empty array. */
    fun <T> getAny(array: Array<T>?): T? = if (isEmpty(array)) null else array!![0]

    /** The first non-null element. */
    fun <T : Any> firstNonNull(array: Array<T?>?): T? = array?.firstOrNull { it != null }

    /** The first element satisfying [matcher]. */
    fun <T> firstMatch(array: Array<T>?, matcher: (T) -> Boolean): T? = array?.firstOrNull(matcher)

    /** `true` when the array has at least one `null` element. */
    fun hasNull(array: Array<*>?): Boolean = array?.any { it == null } ?: false

    /** `true` when every element is `null`; an empty or `null` array counts as all-null. */
    fun isAllNull(array: Array<*>?): Boolean = array?.all { it == null } ?: true

    /** `true` when no element is `null`. */
    fun isAllNotNull(array: Array<*>?): Boolean = array?.all { it != null } ?: false

    /** `true` when no element is empty. */
    fun isAllNotEmpty(array: Array<*>?): Boolean = array != null && array.all { ObjectUtil.isNotEmpty(it) }

    /** `true` when every element is empty. */
    fun isAllEmpty(array: Array<*>?): Boolean = array == null || array.all { ObjectUtil.isEmpty(it) }

    /** How many elements are empty. */
    fun emptyCount(array: Array<*>?): Int = array?.count { ObjectUtil.isEmpty(it) } ?: 0

    /** Joins the elements with [conjunction], surrounded by [prefix] and [suffix]. */
    fun <T> join(
        array: Array<T>?,
        conjunction: CharSequence,
        prefix: CharSequence = "",
        suffix: CharSequence = "",
    ): String = StrUtil.join(array?.toList(), conjunction, prefix, suffix)

    /** The sub-array `[startInclude, endExclude)`, clamped to the input. */
    inline fun <reified T> sub(array: Array<T>?, startInclude: Int, endExclude: Int): Array<T> {
        if (array == null || array.isEmpty()) return emptyArray()
        val start = startInclude.coerceIn(0, array.size)
        val end = endExclude.coerceIn(0, array.size)
        if (end <= start) return emptyArray()
        return array.copyOfRange(start, end)
    }

    /** A copy of [array] with [element] appended. */
    inline fun <reified T> append(array: Array<T>?, element: T): Array<T> {
        if (array == null) return arrayOf(element)
        @Suppress("UNCHECKED_CAST")
        val result = array.copyOf(array.size + 1) as Array<T>
        result[array.size] = element
        return result
    }

    /** A copy of [array] with every element of [values] appended. */
    inline fun <reified T> addAll(array: Array<T>?, values: Array<T>?): Array<T> {
        if (isEmpty(values)) return array ?: emptyArray()
        if (array == null) return values!!.copyOf()
        @Suppress("UNCHECKED_CAST")
        val result = array.copyOf(array.size + values!!.size) as Array<T>
        values.copyInto(result, array.size)
        return result
    }

    /** A copy of [array] with [element] inserted at [index]; the index is clamped to the array. */
    inline fun <reified T> insert(array: Array<T>?, index: Int, element: T): Array<T> {
        if (array == null) return arrayOf(element)
        val at = index.coerceIn(0, array.size)
        val result = arrayOfNulls<T>(array.size + 1)
        array.copyInto(result, 0, 0, at)
        result[at] = element
        array.copyInto(result, at + 1, at)
        @Suppress("UNCHECKED_CAST")
        return result as Array<T>
    }

    /** A copy of [array] without the element at [index]; out-of-range indices return a copy. */
    inline fun <reified T> remove(array: Array<T>?, index: Int): Array<T> {
        if (array == null) return emptyArray()
        if (index < 0 || index >= array.size) return array.copyOf()
        val result = arrayOfNulls<T>(array.size - 1)
        array.copyInto(result, 0, 0, index)
        array.copyInto(result, index, index + 1)
        @Suppress("UNCHECKED_CAST")
        return result as Array<T>
    }

    /** A copy of [array] without the first occurrence of [element]. */
    inline fun <reified T> removeEle(array: Array<T>?, element: T): Array<T> {
        if (array == null) return emptyArray()
        val index = array.indexOf(element)
        return if (index < 0) array.copyOf() else remove(array, index)
    }

    /** A copy of [array] resized to [newSize], padded with `null` when it grows. */
    inline fun <reified T> resize(array: Array<T>?, newSize: Int): Array<T> {
        if (array == null) return emptyArray()
        require(newSize >= 0) { "New size must not be negative" }
        @Suppress("UNCHECKED_CAST")
        return array.copyOf(newSize) as Array<T>
    }

    /** Reverses [array] in place and returns it for chaining. */
    fun <T> reverse(array: Array<T>?): Array<T>? {
        if (array == null) return null
        var left = 0
        var right = array.size - 1
        while (left < right) {
            val tmp = array[left]
            array[left] = array[right]
            array[right] = tmp
            left++
            right--
        }
        return array
    }

    /** Swaps the elements at [index1] and [index2]; out-of-range indices are ignored. */
    fun <T> swap(array: Array<T>?, index1: Int, index2: Int): Array<T>? {
        if (array == null || index1 !in array.indices || index2 !in array.indices) return array
        val tmp = array[index1]
        array[index1] = array[index2]
        array[index2] = tmp
        return array
    }

    /** The distinct elements of [array], keeping the first occurrence and its order. */
    inline fun <reified T> distinct(array: Array<T>?): Array<T> {
        if (array == null) return emptyArray()
        @Suppress("UNCHECKED_CAST")
        return array.toList().distinct().toTypedArray() as Array<T>
    }

    /** The elements of [array] that are neither `null` nor empty. */
    inline fun <reified T : Any> removeNull(array: Array<T?>?): Array<T> {
        if (array == null) return emptyArray()
        @Suppress("UNCHECKED_CAST")
        return array.filterNotNull().toTypedArray() as Array<T>
    }

    /** The elements of [array] that are not empty (`null`, blank text, empty collection). */
    inline fun <reified T> removeEmpty(array: Array<T>?): Array<T> {
        if (array == null) return emptyArray()
        @Suppress("UNCHECKED_CAST")
        return array.filter { ObjectUtil.isNotEmpty(it) }.toTypedArray() as Array<T>
    }

    /** The elements of [array] that are not blank text. */
    fun removeBlank(array: Array<String?>?): Array<String> {
        if (array == null) return emptyArray()
        return array.filterNotNull().filter { StrUtil.isNotBlank(it) }.toTypedArray()
    }

    /** `null` becomes an empty array. */
    inline fun <reified T> nullToEmpty(array: Array<T>?): Array<T> = array ?: emptyArray()

    /** Filters [array] into an array. */
    inline fun <reified T> filter(array: Array<T>?, filter: (T) -> Boolean): Array<T> {
        if (array == null) return emptyArray()
        @Suppress("UNCHECKED_CAST")
        return array.filter(filter).toTypedArray() as Array<T>
    }

    /** The set of distinct elements of [array], keeping insertion order. */
    fun <T> mapToSet(array: Array<T>?): Set<T> = array?.toSet() ?: emptySet()

    /** The array as a [List], or an empty list for `null`. */
    fun <T> toList(array: Array<T>?): List<T> = array?.toList() ?: emptyList()

    /** Wraps a single value in an array. */
    inline fun <reified T> wrap(value: T?): Array<T?> = arrayOf(value)

    /** `true` when [array] is sorted ascending according to its natural order. */
    fun <T : Comparable<T>> isSortedASC(array: Array<T>?): Boolean {
        if (array == null) return true
        for (index in 0 until array.size - 1) {
            if (array[index] > array[index + 1]) return false
        }
        return true
    }

    /** `true` when [array] is sorted descending according to its natural order. */
    fun <T : Comparable<T>> isSortedDESC(array: Array<T>?): Boolean {
        if (array == null) return true
        for (index in 0 until array.size - 1) {
            if (array[index] < array[index + 1]) return false
        }
        return true
    }
}
