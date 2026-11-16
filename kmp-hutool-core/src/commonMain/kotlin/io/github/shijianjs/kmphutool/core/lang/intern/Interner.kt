package io.github.shijianjs.kmphutool.core.lang.intern

/**
 * Canonicalises equal objects so that they can be compared with reference equality.
 *
 * Port of `cn.hutool.core.lang.intern.Interner`, the portable contract behind
 * `InternUtil`. Implementations that need weak references are JVM-specific.
 *
 * @hutool cn.hutool.core.lang.intern.Interner
 */
interface Interner<T> {
    /** Returns the canonical instance equal to [sample]. */
    fun intern(sample: T): T
}
