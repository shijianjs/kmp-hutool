package io.github.shijianjs.kmphutool.core.text.escape

/**
 * Swaps the two columns of an `[from, to]` table, turning an escape table into an unescape table.
 *
 * Port of `cn.hutool.core.text.escape.InternalEscapeUtil`; the Java class is package-private and
 * this one is `internal` for the same reason.
 *
 * @hutool cn.hutool.core.text.escape.InternalEscapeUtil
 */
internal object InternalEscapeUtil {
    /** Returns a copy of [array] with each pair inverted. */
    fun invert(array: Array<Array<String>>): Array<Array<String>> =
        Array(array.size) { i -> arrayOf(array[i][1], array[i][0]) }
}
