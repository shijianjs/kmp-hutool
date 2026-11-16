package io.github.shijianjs.kmphutool.core.map

/**
 * Case-insensitive map that keeps insertion order.
 *
 * Port of `cn.hutool.core.map.CaseInsensitiveLinkedMap`. The base [CaseInsensitiveMap] is already
 * backed by a `LinkedHashMap`, so this subclass differs only in name — same as upstream's hierarchy.
 *
 * @hutool cn.hutool.core.map.CaseInsensitiveLinkedMap
 */
class CaseInsensitiveLinkedMap<V>(initial: Map<String, V>? = null) : CaseInsensitiveMap<V>(initial)
