package io.github.shijianjs.kmphutool.core.math

/**
 * Counting and enumerating arrangements and combinations, plus yuan/cent conversion.
 *
 * Port of `cn.hutool.core.math.MathUtil`. The Java class returns `String[]` per arrangement; the
 * port returns `List<List<String>>` so the result can be compared and iterated without arrays.
 *
 * @hutool cn.hutool.core.math.MathUtil
 */
object MathUtil {
    /** `nPr`: the number of ways to pick and order [m] items out of [n]. */
    fun arrangementCount(n: Int, m: Int): Long {
        require(n >= 0 && m >= 0) { "n and m must not be negative" }
        if (m > n) return 0
        var result = 1L
        for (value in (n - m + 1)..n) result *= value
        return result
    }

    /** `nPn`: the number of orderings of all [n] items. */
    fun arrangementCount(n: Int): Long = arrangementCount(n, n)

    /** `nCr`: the number of ways to pick [m] items out of [n], ignoring order. */
    fun combinationCount(n: Int, m: Int): Long {
        require(n >= 0 && m >= 0) { "n and m must not be negative" }
        if (m > n) return 0
        val k = minOf(m, n - m)
        var result = 1L
        for (index in 1..k) {
            result = result * (n - k + index) / index
        }
        return result
    }

    /** Every ordered selection of [m] items from [datas]. */
    fun arrangementSelect(datas: Array<String>, m: Int): List<List<String>> {
        require(m in 0..datas.size) { "m must be between 0 and the number of items" }
        val result = mutableListOf<List<String>>()
        if (m == 0) return listOf(emptyList())
        arrange(datas.toList(), mutableListOf(), BooleanArray(datas.size), m, result)
        return result
    }

    /** Every ordered selection of all [datas]. */
    fun arrangementSelect(datas: Array<String>): List<List<String>> = arrangementSelect(datas, datas.size)

    /** Every unordered selection of [m] items from [datas]. */
    fun combinationSelect(datas: Array<String>, m: Int): List<List<String>> {
        require(m in 0..datas.size) { "m must be between 0 and the number of items" }
        val result = mutableListOf<List<String>>()
        combine(datas.toList(), 0, mutableListOf(), m, result)
        return result
    }

    /** Converts [yuan] to cents, rounding half up: `12.34` → `1234`. */
    fun yuanToCent(yuan: Double): Long = (yuan * 100 + 0.5).toLong()

    /** Converts [cent] to yuan: `1234` → `12.34`. */
    fun centToYuan(cent: Long): Double = cent / 100.0

    private fun arrange(
        source: List<String>,
        prefix: MutableList<String>,
        used: BooleanArray,
        length: Int,
        result: MutableList<List<String>>,
    ) {
        if (prefix.size == length) {
            result.add(prefix.toList())
            return
        }
        for (index in source.indices) {
            if (used[index]) continue
            used[index] = true
            prefix.add(source[index])
            arrange(source, prefix, used, length, result)
            prefix.removeAt(prefix.size - 1)
            used[index] = false
        }
    }

    private fun combine(
        source: List<String>,
        start: Int,
        prefix: MutableList<String>,
        length: Int,
        result: MutableList<List<String>>,
    ) {
        if (prefix.size == length) {
            result.add(prefix.toList())
            return
        }
        for (index in start until source.size) {
            prefix.add(source[index])
            combine(source, index + 1, prefix, length, result)
            prefix.removeAt(prefix.size - 1)
        }
    }
}
