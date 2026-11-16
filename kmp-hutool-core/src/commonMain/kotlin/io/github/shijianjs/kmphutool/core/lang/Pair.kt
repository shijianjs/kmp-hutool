package io.github.shijianjs.kmphutool.core.lang

/**
 * An immutable key/value pair.
 *
 * Port of `cn.hutool.core.lang.Pair`. Kotlin's property accessors already expose `getKey()`/
 * `getValue()` to Java callers, so no extra methods are needed.
 *
 * @hutool cn.hutool.core.lang.Pair
 */
data class Pair<K, V>(val key: K, val value: V) {
    override fun toString(): String = "Pair [key=$key, value=$value]"

    companion object {
        /** Builds a pair. */
        fun <K, V> of(key: K, value: V): Pair<K, V> = Pair(key, value)
    }
}
