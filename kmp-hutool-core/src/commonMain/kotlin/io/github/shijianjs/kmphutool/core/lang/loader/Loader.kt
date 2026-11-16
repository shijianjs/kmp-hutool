package io.github.shijianjs.kmphutool.core.lang.loader

/**
 * Supplies a fully prepared object, blocking until it is ready.
 *
 * Port of `cn.hutool.core.lang.loader.Loader`.
 *
 * @hutool cn.hutool.core.lang.loader.Loader
 */
fun interface Loader<T> {
    /** The loaded object. */
    fun get(): T
}

/**
 * Lazily initialises its object on first [get], then returns the same instance.
 *
 * Port of `cn.hutool.core.lang.loader.LazyLoader`. Kotlin's [lazy] provides the double-checked
 * locking upstream implements by hand.
 *
 * @hutool cn.hutool.core.lang.loader.LazyLoader
 */
abstract class LazyLoader<T> : Loader<T> {
    private val delegate: Lazy<T> = lazy { init() }

    override fun get(): T = delegate.value

    /** Creates the object; called at most once. */
    protected abstract fun init(): T
}
