package io.github.shijianjs.kmphutool.core.lang

/**
 * Transforms an input value into another value of the same kind.
 *
 * Port of `cn.hutool.core.lang.Replacer`.
 *
 * @hutool cn.hutool.core.lang.Replacer
 */
fun interface Replacer<T> {
    /** Returns the transformed value. */
    fun replace(t: T): T
}
