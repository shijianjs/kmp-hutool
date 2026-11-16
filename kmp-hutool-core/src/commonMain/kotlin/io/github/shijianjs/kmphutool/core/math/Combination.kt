package io.github.shijianjs.kmphutool.core.math

import io.github.shijianjs.kmphutool.core.util.StrUtil

/**
 * Combinations `C(n, m)` of a fixed data set.
 *
 * Port of `cn.hutool.core.math.Combination`. Upstream's `countBig`/`countSafe` return
 * `BigInteger`, which has no `commonMain` equivalent; [count] therefore does the same
 * incremental multiply-and-divide with `Long` and throws on overflow instead of silently wrapping.
 *
 * @hutool cn.hutool.core.math.Combination
 */
class Combination(private val datas: Array<String>) {
    /** All combinations of [m] elements, in lexicographic order of the source array. */
    fun select(m: Int): List<Array<String>> {
        val result = ArrayList<Array<String>>(count(datas.size, m).coerceAtMost(Int.MAX_VALUE.toLong()).toInt())
        select(0, arrayOfNulls<String>(m), 0, result)
        return result
    }

    /** Every non-empty combination, sizes 1 through `datas.size`. */
    fun selectAll(): List<Array<String>> {
        val result = ArrayList<Array<String>>()
        for (i in 1..datas.size) result.addAll(select(i))
        return result
    }

    private fun select(dataIndex: Int, resultList: Array<String?>, resultIndex: Int, result: MutableList<Array<String>>) {
        val resultLen = resultList.size
        val resultCount = resultIndex + 1
        if (resultCount > resultLen) {
            @Suppress("UNCHECKED_CAST")
            result.add(resultList.copyOf() as Array<String>)
            return
        }
        var i = dataIndex
        while (i < datas.size + resultCount - resultLen) {
            resultList[resultIndex] = datas[i]
            select(i + 1, resultList, resultIndex + 1, result)
            i++
        }
    }

    companion object {
        /** `C(n, m)`; throws [ArithmeticException] when the true value exceeds `Long`. */
        fun count(n: Int, m: Int): Long {
            require(n >= 0 && m >= 0) { "n and m must be non-negative. got n=$n, m=$m" }
            if (m > n) return 0
            if (m == 0 || n == m) return 1
            val k = minOf(m, n - m)
            var result = 1L
            for (i in 1..k) {
                val numerator = (n - k + i).toLong()
                val divisor = i.toLong()
                // Cancel the shared factor first so the product stays exact and inside Long.
                val shared = gcd(result, divisor)
                val reducedDivisor = divisor / shared
                result /= shared
                val reducedNumerator = numerator / reducedDivisor
                val next = result * reducedNumerator
                if (reducedNumerator != 0L && next / reducedNumerator != result) {
                    throw ArithmeticException("Overflow computing C($n,$m)")
                }
                result = next
            }
            return result
        }

        /** `C(n,1) + C(n,2) + ... + C(n,n)` = `2^n - 1`. */
        fun countAll(n: Int): Long {
            if (n < 0 || n > 63) {
                throw IllegalArgumentException(StrUtil.format("countAll must have n >= 0 and n <= 63, but got n={}", n))
            }
            return if (n == 63) Long.MAX_VALUE else (1L shl n) - 1
        }

        private tailrec fun gcd(a: Long, b: Long): Long = if (b == 0L) a else gcd(b, a % b)
    }
}
