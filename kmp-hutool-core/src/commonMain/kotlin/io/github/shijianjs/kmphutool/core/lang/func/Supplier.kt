package io.github.shijianjs.kmphutool.core.lang.func

/**
 * Parameterised instance factories used by [io.github.shijianjs.kmphutool.core.builder.GenericBuilder].
 *
 * Ports of `cn.hutool.core.lang.func.Supplier1` … `Supplier5`. Kotlin's lambdas already allow this,
 * but keeping the interfaces lets Hutool-style APIs express "constructor with N arguments" without
 * reflection.
 *
 * @hutool cn.hutool.core.lang.func.Supplier1
 */
fun interface Supplier1<T, P1> {
    fun get(p1: P1): T

    fun toSupplier(p1: P1): () -> T = { get(p1) }
}

/**
 * Two-parameter supplier.
 *
 * @hutool cn.hutool.core.lang.func.Supplier2
 */
fun interface Supplier2<T, P1, P2> {
    fun get(p1: P1, p2: P2): T

    fun toSupplier(p1: P1, p2: P2): () -> T = { get(p1, p2) }
}

/**
 * Three-parameter supplier.
 *
 * @hutool cn.hutool.core.lang.func.Supplier3
 */
fun interface Supplier3<T, P1, P2, P3> {
    fun get(p1: P1, p2: P2, p3: P3): T

    fun toSupplier(p1: P1, p2: P2, p3: P3): () -> T = { get(p1, p2, p3) }
}

/**
 * Four-parameter supplier.
 *
 * @hutool cn.hutool.core.lang.func.Supplier4
 */
fun interface Supplier4<T, P1, P2, P3, P4> {
    fun get(p1: P1, p2: P2, p3: P3, p4: P4): T

    fun toSupplier(p1: P1, p2: P2, p3: P3, p4: P4): () -> T = { get(p1, p2, p3, p4) }
}

/**
 * Five-parameter supplier.
 *
 * @hutool cn.hutool.core.lang.func.Supplier5
 */
fun interface Supplier5<T, P1, P2, P3, P4, P5> {
    fun get(p1: P1, p2: P2, p3: P3, p4: P4, p5: P5): T

    fun toSupplier(p1: P1, p2: P2, p3: P3, p4: P4, p5: P5): () -> T = { get(p1, p2, p3, p4, p5) }
}
