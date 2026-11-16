package io.github.shijianjs.kmphutool.core.lang.copier

/**
 * A source of copies.
 *
 * Port of `cn.hutool.core.lang.copier.Copier`.
 *
 * @hutool cn.hutool.core.lang.copier.Copier
 */
fun interface Copier<T> {
    /** Produces the copy. */
    fun copy(): T
}
