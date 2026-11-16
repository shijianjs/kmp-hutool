package io.github.shijianjs.kmphutool.core.map

/**
 * Insertion-ordered variant of [CamelCaseMap].
 *
 * Port of `cn.hutool.core.map.CamelCaseLinkedMap`; in Kotlin both variants use `LinkedHashMap`, so
 * the only difference from the Java pair is the documented iteration order.
 *
 * @hutool cn.hutool.core.map.CamelCaseLinkedMap
 */
class CamelCaseLinkedMap<K, V>(initial: Map<K, V>? = null) : CamelCaseMap<K, V>(linkedMapOf()) {
    init {
        initial?.let { putAll(it) }
    }
}
