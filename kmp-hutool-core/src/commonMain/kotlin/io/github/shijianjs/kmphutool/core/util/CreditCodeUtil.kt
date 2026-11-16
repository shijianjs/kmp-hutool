package io.github.shijianjs.kmphutool.core.util

/**
 * Validation of the 18-character Unified Social Credit Code (统一社会信用代码).
 *
 * Port of `cn.hutool.core.util.CreditCodeUtil`. The checksum algorithm, the character set and the
 * weight table are copied from upstream's `CreditCodeUtil`/`Validator`.
 *
 * @hutool cn.hutool.core.util.CreditCodeUtil
 */
object CreditCodeUtil {
    private const val CHARSET = "0123456789ABCDEFGHJKLMNPQRTUWXY"
    private val WEIGHTS = intArrayOf(1, 3, 9, 27, 19, 26, 16, 17, 20, 29, 25, 13, 8, 24, 10, 30, 28)

    /** `true` when the text has 18 allowed characters; no checksum verification. */
    fun isCreditCodeSimple(code: CharSequence?): Boolean {
        if (code == null || code.length != 18) return false
        return code.all { CHARSET.indexOf(it) >= 0 }
    }

    /** `true` when the text is an 18-character code with a valid check digit. */
    fun isCreditCode(code: CharSequence?): Boolean {
        if (!isCreditCodeSimple(code)) return false
        val text = code!!.toString().uppercase()
        var sum = 0
        for (index in 0 until 17) {
            sum += CHARSET.indexOf(text[index]) * WEIGHTS[index]
        }
        val checkValue = 31 - sum % 31
        val expected = CHARSET[if (checkValue == 31) 0 else checkValue]
        return expected == text[17]
    }

    /**
     * Builds a random, checksum-valid code.
     *
     * @param regionCode the 6-digit administrative division code, `110000` when not given
     */
    fun randomCreditCode(regionCode: String = "110000"): String {
        require(regionCode.length == 6 && regionCode.all { it.isDigit() }) { "regionCode must be 6 digits" }
        val builder = StringBuilder(18)
        builder.append(regionCode)
        // 11 random digits complete the body (organisation code plus its check digit).
        repeat(11) { builder.append(CHARSET[RandomUtil.randomInt(0, 10)]) }
        val body = builder.toString()
        var sum = 0
        for (index in 0 until 17) sum += CHARSET.indexOf(body[index]) * WEIGHTS[index]
        val checkValue = 31 - sum % 31
        return body + CHARSET[if (checkValue == 31) 0 else checkValue]
    }
}
