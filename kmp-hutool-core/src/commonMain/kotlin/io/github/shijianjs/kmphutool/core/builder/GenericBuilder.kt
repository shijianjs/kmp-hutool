package io.github.shijianjs.kmphutool.core.builder

import io.github.shijianjs.kmphutool.core.lang.func.Consumer3
import io.github.shijianjs.kmphutool.core.lang.func.Supplier1
import io.github.shijianjs.kmphutool.core.lang.func.Supplier2
import io.github.shijianjs.kmphutool.core.lang.func.Supplier3
import io.github.shijianjs.kmphutool.core.lang.func.Supplier4
import io.github.shijianjs.kmphutool.core.lang.func.Supplier5

/**
 * A reflection-free builder: an instance factory plus a list of mutations applied before `build()`.
 *
 * Port of `cn.hutool.core.builder.GenericBuilder`. Kotlin function types replace
 * `java.util.function.Supplier`/`Consumer`/`BiConsumer`; the numbered `Supplier1..5` interfaces keep
 * the "constructor with N arguments" entry points.
 *
 * @hutool cn.hutool.core.builder.GenericBuilder
 */
class GenericBuilder<T>(private val instant: () -> T) : Builder<T> {
    private val modifiers = mutableListOf<(T) -> Unit>()

    /** Applies an in-place mutation to the instance being built. */
    fun with(consumer: (T) -> Unit): GenericBuilder<T> = apply { modifiers.add(consumer) }

    /** Applies a one-argument method to the instance. */
    fun <P1> with(consumer: (T, P1) -> Unit, p1: P1): GenericBuilder<T> =
        apply { modifiers.add { instance -> consumer(instance, p1) } }

    /** Applies a two-argument method to the instance. */
    fun <P1, P2> with(consumer: (T, P1, P2) -> Unit, p1: P1, p2: P2): GenericBuilder<T> =
        apply { modifiers.add { instance -> consumer(instance, p1, p2) } }

    override fun build(): T {
        val value = instant()
        modifiers.forEach { it(value) }
        modifiers.clear()
        return value
    }

    companion object {
        /** Starts from a zero-argument factory. */
        fun <T> of(instant: () -> T): GenericBuilder<T> = GenericBuilder(instant)

        fun <T, P1> of(instant: Supplier1<T, P1>, p1: P1): GenericBuilder<T> =
            of(instant.toSupplier(p1))

        fun <T, P1, P2> of(instant: Supplier2<T, P1, P2>, p1: P1, p2: P2): GenericBuilder<T> =
            of(instant.toSupplier(p1, p2))

        fun <T, P1, P2, P3> of(
            instant: Supplier3<T, P1, P2, P3>,
            p1: P1,
            p2: P2,
            p3: P3,
        ): GenericBuilder<T> = of(instant.toSupplier(p1, p2, p3))

        fun <T, P1, P2, P3, P4> of(
            instant: Supplier4<T, P1, P2, P3, P4>,
            p1: P1,
            p2: P2,
            p3: P3,
            p4: P4,
        ): GenericBuilder<T> = of(instant.toSupplier(p1, p2, p3, p4))

        fun <T, P1, P2, P3, P4, P5> of(
            instant: Supplier5<T, P1, P2, P3, P4, P5>,
            p1: P1,
            p2: P2,
            p3: P3,
            p4: P4,
            p5: P5,
        ): GenericBuilder<T> = of(instant.toSupplier(p1, p2, p3, p4, p5))
    }
}
