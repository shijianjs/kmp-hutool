package io.github.shijianjs.kmphutool.core.lang.hash

/** A 32-bit hash. Port of `cn.hutool.core.lang.hash.Hash32`. @hutool cn.hutool.core.lang.hash.Hash32 */
fun interface Hash32<T> : Hash<T> {
    fun hash32(t: T): Int

    override fun hash(t: T): Any = hash32(t)
}
