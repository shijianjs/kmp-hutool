package io.github.shijianjs.kmphutool.core.lang.hash

/**
 * Computes a hash value for [t].
 *
 * Port of `cn.hutool.core.lang.hash.Hash`. Kotlin has no `Number` hierarchy common to all platforms,
 * so the widest return types are used by the sub-interfaces.
 *
 * @hutool cn.hutool.core.lang.hash.Hash
 */
fun interface Hash<T> {
    /** The hash value of [t]. */
    fun hash(t: T): Any
}
