package io.github.shijianjs.kmphutool.core.text

import io.github.shijianjs.kmphutool.core.util.NumberUtil

/**
 * Text similarity, measured as the longest common subsequence over letters, digits and CJK
 * characters — everything else is dropped before comparing, as upstream does.
 *
 * Port of `cn.hutool.core.text.TextSimilarity`. The DP matrix is the same; the Java `StringBuilder`
 * becomes a `StringBuilder` too, which exists in `commonMain`.
 *
 * @hutool cn.hutool.core.text.TextSimilarity
 */
object TextSimilarity {
    /**
     * Similarity in `0.0..1.0`: the longest common subsequence length divided by the longer cleaned
     * string. Two strings that clean down to nothing count as identical.
     */
    fun similar(strA: String, strB: String): Double {
        val newStrA: String
        val newStrB: String
        if (strA.length < strB.length) {
            newStrA = removeSign(strB)
            newStrB = removeSign(strA)
        } else {
            newStrA = removeSign(strA)
            newStrB = removeSign(strB)
        }

        // The longer string is the denominator, the common part the numerator.
        val temp = maxOf(newStrA.length, newStrB.length)
        if (temp == 0) return 1.0

        return NumberUtil.div(longestCommonSubstringLength(newStrA, newStrB), temp)
    }

    /** [similar] as a percentage string with [scale] decimals. */
    fun similar(strA: String, strB: String, scale: Int): String =
        NumberUtil.formatPercent(similar(strA, strB), scale)

    /**
     * The longest common subsequence itself. The characters need not be adjacent, matching
     * upstream's documented behaviour.
     */
    fun longestCommonSubstring(strA: String, strB: String): String {
        val matrix = generateMatrix(strA, strB)

        var m = strA.length
        var n = strB.length
        val result = CharArray(matrix[m][n])
        var currentIndex = result.size - 1
        while (matrix[m][n] != 0) {
            when {
                matrix[m][n] == matrix[m][n - 1] -> n--
                matrix[m][n] == matrix[m - 1][n] -> m--
                else -> {
                    result[currentIndex] = strA[m - 1]
                    currentIndex--
                    n--
                    m--
                }
            }
        }
        return result.concatToString()
    }

    private fun removeSign(str: String): String {
        val sb = StringBuilder(str.length)
        for (c in str) if (isValidChar(c)) sb.append(c)
        return sb.toString()
    }

    private fun isValidChar(charValue: Char): Boolean =
        (charValue in '\u4E00'..'\u9FFF') ||
            (charValue in 'a'..'z') ||
            (charValue in 'A'..'Z') ||
            (charValue in '0'..'9')

    private fun longestCommonSubstringLength(strA: String, strB: String): Int =
        generateMatrix(strA, strB)[strA.length][strB.length]

    /** Classic LCS table: equal characters extend the diagonal, otherwise take the larger neighbour. */
    private fun generateMatrix(strA: String, strB: String): Array<IntArray> {
        val m = strA.length
        val n = strB.length
        val matrix = Array(m + 1) { IntArray(n + 1) }
        for (i in 1..m) {
            for (j in 1..n) {
                matrix[i][j] = if (strA[i - 1] == strB[j - 1]) {
                    matrix[i - 1][j - 1] + 1
                } else {
                    maxOf(matrix[i][j - 1], matrix[i - 1][j])
                }
            }
        }
        return matrix
    }
}
