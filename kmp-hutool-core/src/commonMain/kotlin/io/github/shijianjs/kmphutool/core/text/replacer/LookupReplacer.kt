package io.github.shijianjs.kmphutool.core.text.replacer

import io.github.shijianjs.kmphutool.core.text.StrBuilder

/**
 * Replaces keys with values from a lookup table, preferring the longest matching key at each
 * position.
 *
 * Port of `cn.hutool.core.text.replacer.LookupReplacer`.
 *
 * @hutool cn.hutool.core.text.replacer.LookupReplacer
 */
class LookupReplacer(vararg lookup: Array<String>) : StrReplacer() {
    private val lookupMap = mutableMapOf<String, String>()
    private val prefixSet = mutableSetOf<Char>()
    private val minLength: Int
    private val maxLength: Int

    init {
        var min = Int.MAX_VALUE
        var max = 0
        for (pair in lookup) {
            val key = pair[0]
            lookupMap[key] = pair[1]
            prefixSet.add(key[0])
            if (key.length > max) max = key.length
            if (key.length < min) min = key.length
        }
        maxLength = max
        minLength = min
    }

    override fun replaceAt(str: CharSequence, pos: Int, out: StrBuilder): Int {
        if (!prefixSet.contains(str[pos])) return 0

        var max = maxLength
        if (pos + maxLength > str.length) max = str.length - pos
        for (length in max downTo minLength) {
            val candidate = str.substring(pos, pos + length)
            val replacement = lookupMap[candidate]
            if (replacement != null) {
                out.append(replacement)
                return length
            }
        }
        return 0
    }
}
