package io.github.shijianjs.kmphutool.core.lang.hash

/** A 64-bit hash. Port of `cn.hutool.core.lang.hash.Hash64`. @hutool cn.hutool.core.lang.hash.Hash64 */
fun interface Hash64<T> : Hash<T> {
    fun hash64(t: T): Long

    override fun hash(t: T): Any = hash64(t)
}
