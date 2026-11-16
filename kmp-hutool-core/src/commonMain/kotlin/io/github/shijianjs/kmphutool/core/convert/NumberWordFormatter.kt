package io.github.shijianjs.kmphutool.core.convert

import kotlin.math.round

/**
 * Spells a number in English, mainly for amounts: `123.45` -> `ONE HUNDRED AND TWENTY THREE AND CENTS
 * FORTY FIVE ONLY`.
 *
 * Port of `cn.hutool.core.convert.NumberWordFormatter`. `NumberUtil.decimalFormat` is replaced by
 * a local two-decimal helper.
 *
 * @hutool cn.hutool.core.convert.NumberWordFormatter
 */
object NumberWordFormatter {
    private val NUMBER = arrayOf("", "ONE", "TWO", "THREE", "FOUR", "FIVE", "SIX", "SEVEN", "EIGHT", "NINE")
    private val NUMBER_TEEN = arrayOf(
        "TEN", "ELEVEN", "TWELVE", "THIRTEEN", "FOURTEEN", "FIFTEEN", "SIXTEEN", "SEVENTEEN", "EIGHTEEN", "NINETEEN",
    )
    private val NUMBER_TEN = arrayOf("TEN", "TWENTY", "THIRTY", "FORTY", "FIFTY", "SIXTY", "SEVENTY", "EIGHTY", "NINETY")
    private val NUMBER_MORE = arrayOf("", "THOUSAND", "MILLION", "BILLION", "TRILLION")
    private val NUMBER_SUFFIX = arrayOf("k", "w", "m", "b", "t", "p", "e")
    private val STANDARD_UNIT_INDICES = intArrayOf(0, 2, 3, 4, 5, 6)

    /** Spells [x] in English; `null` gives an empty string. */
    fun format(x: Any?): String = if (x == null) "" else formatNumber(x.toString())

    /** Formats a number with `k`/`w` units, e.g. `2100` -> `2.1k`. */
    fun formatSimple(value: Long): String = formatSimple(value, true)

    /** Formats with `k`/`w` only when [isTwo], otherwise the `k..e` unit series. */
    fun formatSimple(value: Long, isTwo: Boolean): String {
        if (value < 1000) return value.toString()

        var res = value.toDouble()
        val index: Int
        if (isTwo) {
            if (value >= 10000) {
                res = value / 10000.0
                index = 1
            } else {
                res = value / 1000.0
                index = 0
            }
        } else {
            var unitIndex = -1
            while (res >= 1000 && unitIndex < STANDARD_UNIT_INDICES.size - 1) {
                res /= 1000
                unitIndex++
            }
            index = STANDARD_UNIT_INDICES[unitIndex]
        }
        return decimalFormat2(res) + NUMBER_SUFFIX[index]
    }

    private fun formatNumber(x: String): String {
        val z = x.indexOf('.')
        val lstr: String
        var rstr = ""
        if (z > -1) {
            lstr = x.substring(0, z)
            rstr = x.substring(z + 1)
        } else {
            lstr = x
        }

        var lstrrev = lstr.reversed()
        val a = arrayOfNulls<String>(5)
        when (lstrrev.length % 3) {
            1 -> lstrrev += "00"
            2 -> lstrrev += "0"
        }
        val lm = StringBuilder()
        for (i in 0 until lstrrev.length / 3) {
            a[i] = lstrrev.substring(3 * i, 3 * i + 3).reversed()
            if (a[i] != "000") {
                if (i != 0) {
                    lm.insert(0, transThree(a[i]!!) + " " + parseMore(i) + " ")
                } else {
                    lm.setLength(0)
                    lm.append(transThree(a[i]!!))
                }
            } else {
                lm.append(transThree(a[i]!!))
            }
        }

        var xs = if (lm.isEmpty()) "ZERO " else " "
        if (z > -1) {
            xs += "AND CENTS " + transTwo(rstr) + " "
        }
        return lm.toString().trim() + xs + "ONLY"
    }

    private fun parseTeen(s: String): String = NUMBER_TEEN[s.toInt() - 10]

    private fun parseTen(s: String): String = NUMBER_TEN[s.substring(0, 1).toInt() - 1]

    private fun parseMore(i: Int): String = NUMBER_MORE[i]

    private fun transTwo(sIn: String): String {
        var s = sIn
        s = if (s.length > 2) s.substring(0, 2) else if (s.length < 2) s + "0" else s
        return when {
            s.startsWith("0") -> parseLast(s)
            s.startsWith("1") -> parseTeen(s)
            s.endsWith("0") -> parseTen(s)
            else -> parseTen(s) + " " + parseLast(s)
        }
    }

    private fun transThree(s: String): String = when {
        s.startsWith("0") -> transTwo(s.substring(1))
        s.substring(1) == "00" -> parseLast(s.substring(0, 1)) + " HUNDRED"
        else -> parseLast(s.substring(0, 1)) + " HUNDRED AND " + transTwo(s.substring(1))
    }

    private fun parseLast(s: String): String = NUMBER[s.substring(s.length - 1).toInt()]

    private fun decimalFormat2(value: Double): String {
        val rounded = round(value * 100) / 100
        val whole = rounded.toLong()
        val cents = round((rounded - whole) * 100).toLong()
        return if (cents == 0L) whole.toString() else "$whole.${cents.toString().padStart(2, '0').trimEnd('0')}"
    }
}
