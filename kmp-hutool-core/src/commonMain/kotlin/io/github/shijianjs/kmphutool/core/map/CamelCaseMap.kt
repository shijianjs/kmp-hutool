package io.github.shijianjs.kmphutool.core.map

import io.github.shijianjs.kmphutool.core.text.NamingCase

/**
 * A map that camel-cases string keys, so `map["int_value"]` and `map["intValue"]` hit the same entry.
 *
 * Port of `cn.hutool.core.map.CamelCaseMap`.
 *
 * @hutool cn.hutool.core.map.CamelCaseMap
 */
open class CamelCaseMap<K, V>(emptyMap: MutableMap<K, V> = linkedMapOf()) :
    FuncKeyMap<K, V>(emptyMap, { key -> camelKey(key) }) {

    companion object {
        @Suppress("UNCHECKED_CAST")
        private fun <K> camelKey(key: K): K =
            if (key is CharSequence) NamingCase.toCamelCase(key) as K else key
    }
}
