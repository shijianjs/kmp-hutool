package io.github.shijianjs.kmphutool.core.convert

/**
 * Unchecked generic casts used where Java's wildcards cannot be expressed.
 *
 * Port of `cn.hutool.core.convert.CastUtil`. Kotlin's declaration-site variance already permits
 * most of these call sites; the functions remain for source compatibility with Hutool code.
 *
 * @hutool cn.hutool.core.convert.CastUtil
 */
object CastUtil {
    @Suppress("UNCHECKED_CAST")
    fun <T> castUp(collection: Collection<out T>): Collection<T> = collection as Collection<T>

    @Suppress("UNCHECKED_CAST")
    fun <T> castDown(collection: Collection<*>): Collection<T> = collection as Collection<T>

    @Suppress("UNCHECKED_CAST")
    fun <T> castUp(set: Set<out T>): Set<T> = set as Set<T>

    @Suppress("UNCHECKED_CAST")
    fun <T> castDown(set: Set<*>): Set<T> = set as Set<T>

    @Suppress("UNCHECKED_CAST")
    fun <T> castUp(list: List<out T>): List<T> = list as List<T>

    @Suppress("UNCHECKED_CAST")
    fun <T> castDown(list: List<*>): List<T> = list as List<T>

    @Suppress("UNCHECKED_CAST")
    fun <K, V> castUp(map: Map<out K, out V>): Map<K, V> = map as Map<K, V>

    @Suppress("UNCHECKED_CAST")
    fun <K, V> castDown(map: Map<*, *>): Map<K, V> = map as Map<K, V>
}
