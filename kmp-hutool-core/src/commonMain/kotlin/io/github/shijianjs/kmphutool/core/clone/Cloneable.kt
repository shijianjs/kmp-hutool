package io.github.shijianjs.kmphutool.core.clone

/**
 * A type whose instances can produce a copy of themselves.
 *
 * Port of `cn.hutool.core.clone.Cloneable`. Kotlin has no checked `CloneNotSupportedException` and no
 * `Object.clone()`, so an implementation decides how the copy is made (a `copy()` on a data class, a
 * manual constructor call, or a platform clone in a target-specific source set) and throws
 * [CloneRuntimeException] when it cannot.
 *
 * @hutool cn.hutool.core.clone.Cloneable
 */
interface Cloneable<T> {
    /** Returns a copy; throwing [CloneRuntimeException] is allowed. */
    fun clone(): T
}
