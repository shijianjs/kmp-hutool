package io.github.shijianjs.kmphutool.core.lang

/**
 * A value that can be replaced in place, and that can hand out a view which always reads the current
 * value.
 *
 * Port of `cn.hutool.core.lang.MutableObj`. Kotlin code often only needs the `var` itself; this class
 * exists so code shared with Hutool keeps the same shape, including [getOrDefault].
 *
 * @hutool cn.hutool.core.lang.mutable.MutableObj
 */
class MutableObj<T>(initialValue: T? = null) {
    private var value: T? = initialValue

    /** Returns the current value. */
    fun get(): T? = value

    /** Replaces the current value and returns this object. */
    fun set(newValue: T?): MutableObj<T> = apply { value = newValue }

    /** The current value, or [defaultValue] when it is `null`. */
    fun getOrDefault(defaultValue: T): T = value ?: defaultValue

    override fun toString(): String = value?.toString() ?: "null"
}
