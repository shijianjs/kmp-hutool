package io.github.shijianjs.kmphutool.core.lang

/**
 * Predicate deciding whether an element is accepted.
 *
 * Port of `cn.hutool.core.lang.Filter`; used by [io.github.shijianjs.kmphutool.core.collection.FilterIter].
 *
 * @hutool cn.hutool.core.lang.Filter
 */
fun interface Filter<T> {
    /** `true` when [t] should pass through. */
    fun accept(t: T): Boolean
}
