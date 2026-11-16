package io.github.shijianjs.kmphutool.core.util

/**
 * Null-safe operations on any value: emptiness, defaults and comparison.
 *
 * Port of `cn.hutool.core.util.ObjectUtil`. Members that rely on Java reflection or serialisation
 * (`clone`, `cloneByStream`, `serialize`, `deserialize`, `getTypeArgument`, `isBasicType`) are not
 * ported: commonMain has no runtime type introspection, and Kotlin's own `copy`/serialisation libraries
 * do that job better.
 *
 * @hutool cn.hutool.core.util.ObjectUtil
 */
object ObjectUtil {
    /** `true` when both are `null` or `a.equals(b)`. */
    fun equals(a: Any?, b: Any?): Boolean = a == b

    /** `true` when the two arguments are not equal. */
    fun notEqual(a: Any?, b: Any?): Boolean = !equals(a, b)

    /**
     * A size for the value: length of text and arrays, size of collections and maps, `1` for any
     * other non-null value and `0` for `null`.
     */
    fun length(obj: Any?): Int = when (obj) {
        null -> 0
        is CharSequence -> obj.length
        is Collection<*> -> obj.size
        is Map<*, *> -> obj.size
        is Array<*> -> obj.size
        is BooleanArray -> obj.size
        is ByteArray -> obj.size
        is CharArray -> obj.size
        is ShortArray -> obj.size
        is IntArray -> obj.size
        is LongArray -> obj.size
        is FloatArray -> obj.size
        is DoubleArray -> obj.size
        else -> 1
    }

    /** `true` when [obj] contains [element] (text, collection, map key or array). */
    fun contains(obj: Any?, element: Any?): Boolean = when (obj) {
        null -> false
        is String -> obj.contains(element?.toString() ?: return false)
        is Collection<*> -> obj.contains(element)
        is Map<*, *> -> obj.containsKey(element)
        is Array<*> -> obj.any { it == element }
        else -> false
    }

    /** `true` when [obj] is `null`. */
    fun isNull(obj: Any?): Boolean = obj == null

    /** `true` when [obj] is not `null`. */
    fun isNotNull(obj: Any?): Boolean = obj != null

    /**
     * `true` when the value has no content: `null`, an empty string or a blank string, an empty
     * collection/map/array. Numbers and other objects are never "empty", as upstream.
     */
    fun isEmpty(obj: Any?): Boolean = when (obj) {
        null -> true
        is CharSequence -> obj.isEmpty()
        is Collection<*> -> obj.isEmpty()
        is Map<*, *> -> obj.isEmpty()
        is Array<*> -> obj.isEmpty()
        is BooleanArray -> obj.isEmpty()
        is ByteArray -> obj.isEmpty()
        is CharArray -> obj.isEmpty()
        is ShortArray -> obj.isEmpty()
        is IntArray -> obj.isEmpty()
        is LongArray -> obj.isEmpty()
        is FloatArray -> obj.isEmpty()
        is DoubleArray -> obj.isEmpty()
        else -> false
    }

    /** `true` when the value has content. See [isEmpty]. */
    fun isNotEmpty(obj: Any?): Boolean = !isEmpty(obj)

    /** Returns [defaultValue] when [value] is `null`. */
    fun <T> defaultIfNull(value: T?, defaultValue: T): T = value ?: defaultValue

    /** Returns [defaultValue] when [value] is `null`, computing it lazily. */
    fun <T> defaultIfNull(value: T?, defaultValue: () -> T): T = value ?: defaultValue()

    /** Returns [defaultValue] when [value] is empty. See [isEmpty]. */
    fun <T> defaultIfEmpty(value: T?, defaultValue: T): T = if (isEmpty(value)) defaultValue else value as T

    /** Returns [defaultValue] when [value] is empty, computing it lazily. */
    fun <T> defaultIfEmpty(value: T?, defaultValue: () -> T): T = if (isEmpty(value)) defaultValue() else value as T

    /** Returns [defaultValue] when [value] is blank. */
    fun <T> defaultIfBlank(value: T?, defaultValue: T): T = if (StrUtil.isBlankIfStr(value)) defaultValue else value as T

    /** Returns [defaultValue] when [value] is blank, computing it lazily. */
    fun <T> defaultIfBlank(value: T?, defaultValue: () -> T): T =
        if (StrUtil.isBlankIfStr(value)) defaultValue() else value as T

    /** Applies [handler] to the value when it is not `null`, otherwise returns `null`. */
    fun <T, R> apply(value: T?, handler: (T) -> R): R? = value?.let(handler)

    /** Calls [consumer] with the value when it is not `null`. */
    fun <T> accept(value: T?, consumer: (T) -> Unit) {
        if (value != null) consumer(value)
    }

    /** `true` when [value] is a number. */
    fun isNumber(value: Any?): Boolean = value is Number

    /**
     * `true` when [value] is a number that is finite: `NaN` and infinities are not valid numbers,
     * matching Hutool's `isValidIfNumber`.
     */
    fun isValidIfNumber(value: Any?): Boolean = when (value) {
        is Double -> value.isFinite()
        is Float -> value.isFinite()
        is Number -> true
        else -> false
    }

    /** Compares two non-null values, using [compareValues] so `null` never participates. */
    fun <T : Comparable<T>> compare(c1: T?, c2: T?): Int = compareValues(c1, c2)

    /**
     * Compares two values; when [nullGreater] is `true` a `null` sorts *after* a value instead of
     * before it.
     */
    fun <T : Comparable<T>> compare(c1: T?, c2: T?, nullGreater: Boolean): Int {
        if (c1 == null) return if (c2 == null) 0 else if (nullGreater) 1 else -1
        if (c2 == null) return if (nullGreater) -1 else 1
        return c1.compareTo(c2)
    }

    /** `toString()` of [obj], or `null`. */
    fun toString(obj: Any?): String? = obj?.toString()

    /** How many of the arguments are empty. */
    fun emptyCount(vararg objs: Any?): Int = objs.count { isEmpty(it) }

    /** `true` when any argument is `null`. */
    fun hasNull(vararg objs: Any?): Boolean = objs.any { it == null }

    /** `true` when any argument is empty. */
    fun hasEmpty(vararg objs: Any?): Boolean = objs.any { isEmpty(it) }

    /** `true` when every argument is empty. */
    fun isAllEmpty(vararg objs: Any?): Boolean = objs.all { isEmpty(it) }

    /** `true` when every argument is non-empty. */
    fun isAllNotEmpty(vararg objs: Any?): Boolean = objs.all { isNotEmpty(it) }
}
