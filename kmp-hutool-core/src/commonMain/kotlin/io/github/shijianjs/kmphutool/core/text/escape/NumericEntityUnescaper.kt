package io.github.shijianjs.kmphutool.core.text.escape

import io.github.shijianjs.kmphutool.core.text.StrBuilder
import io.github.shijianjs.kmphutool.core.text.replacer.StrReplacer
import io.github.shijianjs.kmphutool.core.util.CharUtil

/**
 * Turns numeric entities such as `&#39;` or `&#x27;` back into characters.
 *
 * Port of `cn.hutool.core.text.escape.NumericEntityUnescaper`. Parsing uses `toIntOrNull`, so a
 * malformed number is ignored instead of throwing like upstream's caught `NumberFormatException`.
 *
 * @hutool cn.hutool.core.text.escape.NumericEntityUnescaper
 */
class NumericEntityUnescaper : StrReplacer() {
    override fun replaceAt(str: CharSequence, pos: Int, out: StrBuilder): Int {
        val length = str.length
        if (str[pos] != '&' || pos >= length - 2 || str[pos + 1] != '#') return 0

        var start = pos + 2
        var isHex = false
        if (str[start] == 'x' || str[start] == 'X') {
            start++
            isHex = true
        }
        if (start == length) return 0

        var end = start
        while (end < length && CharUtil.isHexChar(str[end])) end++

        val isSemiNext = end != length && str[end] == ';'
        if (!isSemiNext) return 0

        val digits = str.substring(start, end)
        val value = if (isHex) digits.toIntOrNull(16) else digits.toIntOrNull(10)
        if (value == null) return 0

        out.append(value.toChar())
        return 2 + (end - start) + (if (isHex) 1 else 0) + 1
    }
}
