package io.github.shijianjs.kmphutool.core.lang.loader

import io.github.shijianjs.kmphutool.core.lang.Assert

/**
 * A lazy loader driven by a factory function.
 *
 * Port of `cn.hutool.core.lang.loader.LazyFunLoader`. The factory is dropped after the first call so
 * the reference does not keep its captures alive.
 *
 * @hutool cn.hutool.core.lang.loader.LazyFunLoader
 */
class LazyFunLoader<T>(private var supplier: (() -> T)?) : LazyLoader<T>() {
    init {
        Assert.notNull(supplier)
    }

    override fun init(): T {
        val factory = supplier ?: throw IllegalStateException("supplier already released")
        val value = factory()
        supplier = null
        return value
    }

    /** `true` once the value has been created and the factory released. */
    fun isInitialize(): Boolean = supplier == null

    /** Runs [consumer] with the value only when it has already been initialised. */
    fun ifInitialized(consumer: (T) -> Unit) {
        Assert.notNull(consumer)
        if (isInitialize()) consumer(get())
    }

    companion object {
        /** Creates a loader from [supplier]. */
        fun <T> on(supplier: () -> T): LazyFunLoader<T> {
            Assert.notNull(supplier, "supplier must be not null!")
            return LazyFunLoader(supplier)
        }
    }
}
