package io.github.shijianjs.kmphutool.core.lang.mutable

/**
 * A holder whose value can be swapped.
 *
 * Port of `cn.hutool.core.lang.mutable.Mutable`. The concrete numeric holders below are plain
 * Kotlin classes rather than `java.lang.Number` subclasses, so they work on every target.
 *
 * @hutool cn.hutool.core.lang.mutable.Mutable
 */
interface Mutable<T> {
    /** The current value. */
    fun get(): T

    /** Replaces the current value. */
    fun set(value: T)
}
