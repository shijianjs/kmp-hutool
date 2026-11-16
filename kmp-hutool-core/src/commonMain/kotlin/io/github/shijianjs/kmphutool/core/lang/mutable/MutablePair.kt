package io.github.shijianjs.kmphutool.core.lang.mutable

import io.github.shijianjs.kmphutool.core.lang.Pair

/**
 * A [Pair] whose key and value can be replaced.
 *
 * Port of `cn.hutool.core.lang.mutable.MutablePair`. The Java class extends an immutable `Pair`; Kotlin's
 * `Pair` is a `data class` with `val`s, so this is a standalone holder implementing [Mutable] and
 * comparing equal to a `Pair` with the same key and value.
 *
 * @hutool cn.hutool.core.lang.mutable.MutablePair
 */
class MutablePair<K, V>(
    var key: K,
    var value: V,
) : Mutable<Pair<K, V>> {
    /** Replaces the key and returns this. */
    fun setKey(key: K): MutablePair<K, V> {
        this.key = key
        return this
    }

    /** Replaces the value and returns this. */
    fun setValue(value: V): MutablePair<K, V> {
        this.value = value
        return this
    }

    override fun get(): Pair<K, V> = Pair(key, value)

    override fun set(value: Pair<K, V>) {
        key = value.key
        this.value = value.value
    }

    override fun equals(other: Any?): Boolean = when (other) {
        is MutablePair<*, *> -> key == other.key && value == other.value
        is Pair<*, *> -> key == other.key && value == other.value
        else -> false
    }

    override fun hashCode(): Int = 31 * key.hashCode() + value.hashCode()

    override fun toString(): String = "MutablePair [key=$key, value=$value]"
}
