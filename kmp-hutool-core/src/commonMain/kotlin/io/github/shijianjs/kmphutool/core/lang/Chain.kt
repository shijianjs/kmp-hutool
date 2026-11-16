package io.github.shijianjs.kmphutool.core.lang

/**
 * Builds a chain of elements where a subclass supplies [addChain].
 *
 * Port of `cn.hutool.core.lang.Chain`, so a usable Kotlin counterpart exists for the replacer chain.
 *
 * @hutool cn.hutool.core.lang.Chain
 */
interface Chain<E, T : Chain<E, T>> : Iterable<E> {
    /** Appends [element] and returns this for chaining. */
    fun addChain(element: E): T
}
