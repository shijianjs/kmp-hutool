package io.github.shijianjs.kmphutool.core.lang.hash

/** A 128-bit hash returning [Number128]. Port of `cn.hutool.core.lang.hash.Hash128`. @hutool cn.hutool.core.lang.hash.Hash128 */
fun interface Hash128<T> : Hash<T> {
    fun hash128(t: T): Number128

    override fun hash(t: T): Any = hash128(t)
}
