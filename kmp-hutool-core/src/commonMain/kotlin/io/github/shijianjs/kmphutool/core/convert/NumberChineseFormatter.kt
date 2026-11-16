package io.github.shijianjs.kmphutool.core.convert

import kotlin.math.round

/**
 * Converts numbers to Chinese numerals, including the formal uppercase money form.
 *
 * Port of `cn.hutool.core.convert.NumberChineseFormatter`. The `BigDecimal` overloads
 * ([cn.hutool.core.convert.NumberChineseFormatter.format] with a `BigDecimal` and
 * `chineseMoneyToNumber`) are not ported because commonMain has no `BigDecimal`; money formatting
 * from a [Double] and the reverse [chineseToNumber] are available.
 *
 * @hutool cn.hutool.core.convert.NumberChineseFormatter
 */
object NumberChineseFormatter {
    private val DIGITS = charArrayOf(
        '零', '一', '壹', '二', '贰', '三', '叁', '四', '肆', '五', '伍',
        '六', '陆', '七', '柒', '八', '捌', '九', '玖',
    )

    private class ChineseUnit(val name: Char, val value: Int, val secUnit: Boolean)

    private val CHINESE_NAME_VALUE = arrayOf(
        ChineseUnit(' ', 1, false),
        ChineseUnit('十', 10, false),
        ChineseUnit('拾', 10, false),
        ChineseUnit('百', 100, false),
        ChineseUnit('佰', 100, false),
        ChineseUnit('千', 1000, false),
        ChineseUnit('仟', 1000, false),
        ChineseUnit('万', 10_000, true),
        ChineseUnit('亿', 100_000_000, true),
    )

    private const val MAX = 99_9999_9999_9999.99

    /** Formats a decimal with two fractional digits, rounded. */
    fun format(amount: Double, isUseTraditional: Boolean): String =
        format(amount, isUseTraditional, false)

    /** Formats [amount] for invoices: `(负数)壹拾贰圆叁角贰分`. */
    fun format(
        amountIn: Double,
        isUseTraditional: Boolean,
        isMoneyMode: Boolean,
        negativeName: String? = "负",
        unitName: String? = "元",
    ): String {
        var amount = amountIn
        val unit = unitName ?: "元"
        if (amount == 0.0) return if (isMoneyMode) "零$unit" + "整" else "零"
        require(amount >= -MAX && amount <= MAX) {
            "Number support only: (-99999999999999.99 ~ 99999999999999.99)!"
        }

        val chineseStr = StringBuilder()
        if (amount < 0) {
            chineseStr.append(negativeName ?: "负")
            amount = -amount
        }

        var yuan = round(amount * 100).toLong()
        val fen = (yuan % 10).toInt()
        yuan /= 10
        val jiao = (yuan % 10).toInt()
        yuan /= 10

        if (!isMoneyMode || yuan != 0L) {
            chineseStr.append(longToChinese(yuan, isUseTraditional))
            if (isMoneyMode) chineseStr.append(unit)
        }

        if (jiao == 0 && fen == 0) {
            if (isMoneyMode) chineseStr.append("整")
            return chineseStr.toString()
        }

        if (!isMoneyMode) chineseStr.append("点")

        if (yuan == 0L && jiao == 0) {
            if (!isMoneyMode) chineseStr.append("零")
        } else {
            chineseStr.append(numberToChinese(jiao, isUseTraditional))
            if (isMoneyMode && jiao != 0) chineseStr.append("角")
        }

        if (fen != 0) {
            chineseStr.append(numberToChinese(fen, isUseTraditional))
            if (isMoneyMode) chineseStr.append("分")
        }
        return chineseStr.toString()
    }

    /** Formats [amount] with money mode off. */
    fun format(amount: Double, isUseTraditional: Boolean, isMoneyMode: Boolean): String =
        format(amount, isUseTraditional, isMoneyMode, "负", "元")

    /** Formats a whole number, supporting negatives. */
    fun format(amountIn: Long, isUseTraditional: Boolean): String {
        var amount = amountIn
        if (amount == 0L) return "零"
        require(kotlin.math.abs(amount) <= MAX) {
            "Number support only: (-99999999999999.99 ~ 99999999999999.99)!"
        }
        val chineseStr = StringBuilder()
        if (amount < 0) {
            chineseStr.append("负")
            amount = -amount
        }
        chineseStr.append(longToChinese(amount, isUseTraditional))
        return chineseStr.toString()
    }

    /** Rounds and appends `万`/`亿`/`万亿`, e.g. `-55555` -> `-5.56万`. */
    fun formatSimple(amount: Long): String {
        if (amount in -9999..9999) return amount.toString()
        return when {
            amount in -9999_9999..9999_9999 -> divide2(amount, 10_000) + "万"
            amount in -9999_9999_9999L..9999_9999_9999L -> divide2(amount, 100_000_000) + "亿"
            else -> divide2(amount, 1_000_000_000_000) + "万亿"
        }
    }

    /** Formats `-999..999`; ten to nineteen omit the leading `一`. */
    fun formatThousand(amount: Int, isUseTraditional: Boolean): String {
        require(amount in -999..999) { "Number support only: (-999 ~ 999)!" }
        val chinese = thousandToChinese(amount, isUseTraditional)
        return if (amount in 10..19) chinese.substring(1) else chinese
    }

    /** Converts a digit character; non-digits pass through unchanged. */
    fun numberCharToChinese(c: Char, isUseTraditional: Boolean): String {
        if (c < '0' || c > '9') return c.toString()
        return numberToChinese(c - '0', isUseTraditional).toString()
    }

    /** Parses Chinese numerals such as `二百二十` or `一千零一十二`. */
    fun chineseToNumber(chinese: String): Int {
        var result = 0
        var section = 0
        var number = 0
        var unit: ChineseUnit? = null
        chinese.forEachIndexed { i, c ->
            val num = chineseDigit(c)
            if (num >= 0) {
                if (num == 0) {
                    if (number > 0 && unit != null) section += number * (unit!!.value / 10)
                    unit = null
                } else if (number > 0) {
                    throw IllegalArgumentException("Bad number '${chinese[i - 1]}$c' at: $i")
                }
                number = num
            } else {
                unit = chineseToUnit(c) ?: throw IllegalArgumentException("Unknown unit '$c' at: $i")
                if (unit!!.secUnit) {
                    section = (section + number) * unit!!.value
                    result += section
                    section = 0
                } else {
                    val unitNumber = if (number == 0 && i == 0) 1 else number
                    section += unitNumber * unit!!.value
                }
                number = 0
            }
        }
        if (number > 0 && unit != null) number *= unit!!.value / 10
        return result + section + number
    }

    private fun longToChinese(amountIn: Long, isUseTraditional: Boolean): String {
        var amount = amountIn
        if (amount == 0L) return "零"
        val parts = IntArray(4)
        var i = 0
        while (amount != 0L && i < parts.size) {
            parts[i] = (amount % 10000).toInt()
            amount /= 10000
            i++
        }
        val chineseStr = StringBuilder()

        var partValue = parts[0]
        if (partValue > 0) {
            chineseStr.insert(0, thousandToChinese(partValue, isUseTraditional))
            if (partValue < 1000) addPreZero(chineseStr)
        }

        partValue = parts[1]
        if (partValue > 0) {
            if (partValue % 10 == 0 && parts[0] > 0) addPreZero(chineseStr)
            chineseStr.insert(0, thousandToChinese(partValue, isUseTraditional) + "万")
            if (partValue < 1000) addPreZero(chineseStr)
        } else {
            addPreZero(chineseStr)
        }

        partValue = parts[2]
        if (partValue > 0) {
            if (partValue % 10 == 0 && parts[1] > 0) addPreZero(chineseStr)
            chineseStr.insert(0, thousandToChinese(partValue, isUseTraditional) + "亿")
            if (partValue < 1000) addPreZero(chineseStr)
        } else {
            addPreZero(chineseStr)
        }

        partValue = parts[3]
        if (partValue > 0) {
            if (parts[2] == 0) chineseStr.insert(0, "亿")
            chineseStr.insert(0, thousandToChinese(partValue, isUseTraditional) + "万")
        }

        if (chineseStr.isNotEmpty() && chineseStr[0] == '零') return chineseStr.substring(1)
        return chineseStr.toString()
    }

    private fun thousandToChinese(amountPart: Int, isUseTraditional: Boolean): String {
        if (amountPart == 0) return DIGITS[0].toString()
        var temp = amountPart
        val chineseStr = StringBuilder()
        var lastIsZero = true
        var i = 0
        while (temp > 0) {
            val digit = temp % 10
            if (digit == 0) {
                if (!lastIsZero) chineseStr.insert(0, "零")
                lastIsZero = true
            } else {
                chineseStr.insert(0, numberToChinese(digit, isUseTraditional).toString() + getUnitName(i, isUseTraditional))
                lastIsZero = false
            }
            temp /= 10
            i++
        }
        return chineseStr.toString()
    }

    private fun chineseToUnit(chinese: Char): ChineseUnit? = CHINESE_NAME_VALUE.firstOrNull { it.name == chinese }

    private fun chineseDigit(chineseIn: Char): Int {
        val chinese = if (chineseIn == '两') '二' else chineseIn
        val i = DIGITS.indexOfFirst { it == chinese }
        return if (i > 0) (i + 1) / 2 else i
    }

    private fun numberToChinese(number: Int, isUseTraditional: Boolean): Char {
        if (number == 0) return DIGITS[0]
        return DIGITS[number * 2 - if (isUseTraditional) 0 else 1]
    }

    private fun getUnitName(index: Int, isUseTraditional: Boolean): String {
        if (index == 0) return ""
        return CHINESE_NAME_VALUE[index * 2 - if (isUseTraditional) 0 else 1].name.toString()
    }

    private fun addPreZero(chineseStr: StringBuilder) {
        if (chineseStr.isEmpty()) return
        if (chineseStr[0] != '零') chineseStr.insert(0, '零')
    }

    private fun divide2(value: Long, divisor: Long): String {
        val negative = value < 0
        val rounded = round(kotlin.math.abs(value).toDouble() / divisor * 100) / 100
        val whole = rounded.toLong()
        val cents = round((rounded - whole) * 100).toLong()
        val text = if (cents == 0L) whole.toString()
        else "$whole.${cents.toString().padStart(2, '0').trimEnd('0')}"
        return if (negative) "-$text" else text
    }
}
