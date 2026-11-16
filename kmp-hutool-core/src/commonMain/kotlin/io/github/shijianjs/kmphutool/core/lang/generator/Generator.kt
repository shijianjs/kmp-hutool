package io.github.shijianjs.kmphutool.core.lang.generator

/**
 * Produces the next value on each call.
 *
 * Port of `cn.hutool.core.lang.generator.Generator`.
 *
 * @hutool cn.hutool.core.lang.generator.Generator
 */
fun interface Generator<T> {
    /** The next generated value. */
    fun next(): T
}
