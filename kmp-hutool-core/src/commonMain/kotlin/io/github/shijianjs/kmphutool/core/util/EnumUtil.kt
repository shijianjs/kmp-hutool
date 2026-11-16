package io.github.shijianjs.kmphutool.core.util

/**
 * Name-based helpers for enum constants.
 *
 * Port of `cn.hutool.core.util.EnumUtil`. Hutool resolves the constant *class* at runtime with
 * `Class`/reflection; the port takes the constants themselves (from `enumValues<E>()`) or a lambda
 * that produces them, which keeps every function usable from common code. The reflection-based
 * members (`getField`, `getFieldNames`, `getEnumConstantsMap` with annotations) are not ported.
 *
 * @hutool cn.hutool.core.util.EnumUtil
 */
object EnumUtil {
    /** The names of [values]. */
    fun <E : Enum<E>> getNames(values: Array<E>): List<String> = values.map { it.name }

    /** The names of every constant of [E]. */
    inline fun <reified E : Enum<E>> getNames(): List<String> = enumValues<E>().map { it.name }

    /** `true` when a constant named [name] exists in [values]; the comparison is case-insensitive. */
    fun <E : Enum<E>> contains(values: Array<E>, name: CharSequence?): Boolean =
        name != null && values.any { it.name.equals(name.toString(), ignoreCase = true) }

    /** `true` when [E] has a constant named [name]. */
    inline fun <reified E : Enum<E>> contains(name: CharSequence?): Boolean = contains(enumValues<E>(), name)

    /** The constant named [name], or `null`; case-insensitive. */
    fun <E : Enum<E>> fromString(values: Array<E>, name: CharSequence?): E? =
        if (name == null) null else values.firstOrNull { it.name.equals(name.toString(), ignoreCase = true) }

    /** The constant named [name], or `null`. */
    inline fun <reified E : Enum<E>> fromString(name: CharSequence?): E? = fromString(enumValues<E>(), name)

    /** The constant at [index], or `null` when out of range. */
    fun <E : Enum<E>> getAt(values: Array<E>, index: Int): E? = values.getOrNull(index)

    /** A map from name to constant, in declaration order. */
    fun <E : Enum<E>> getNameMap(values: Array<E>): Map<String, E> = values.associateBy { it.name }

    /** The index of [value] in [values]. */
    fun <E : Enum<E>> indexOf(values: Array<E>, value: E?): Int = if (value == null) -1 else values.indexOf(value)

    /** `true` when [name] differs from [value]'s name only by case. */
    fun <E : Enum<E>> likeValue(value: E, name: CharSequence?): Boolean =
        name != null && value.name.equals(name.toString(), ignoreCase = true)
}
