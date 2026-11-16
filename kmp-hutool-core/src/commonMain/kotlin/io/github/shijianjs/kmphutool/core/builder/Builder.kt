package io.github.shijianjs.kmphutool.core.builder

/**
 * Something that produces a value on demand.
 *
 * Port of `cn.hutool.core.builder.Builder`. Kotlin call sites usually pass a lambda; the interface
 * stays for classes that build incrementally, such as `StrBuilder`.
 *
 * @hutool cn.hutool.core.builder.Builder
 */
interface Builder<T> {
    /** Builds the value; calling it twice may return the same instance. */
    fun build(): T
}
