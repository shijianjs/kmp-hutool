package io.github.shijianjs.kmphutool.core.util

import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import kotlin.time.Clock

/**
 * Validation and parsing of the Chinese resident identity card (身份证).
 *
 * Port of `cn.hutool.core.util.IdcardUtil`. The port keeps the 15/18-digit checks, province codes,
 * birthday, gender and age. It does not port `Idcard` (a bean over the same data) nor the Hong Kong /
 * Taiwan card checks, which need region tables that do not belong in a general-purpose library.
 * Birthdays are [LocalDate] from kotlinx-datetime instead of `java.util.Date`.
 *
 * @hutool cn.hutool.core.util.IdcardUtil
 */
object IdcardUtil {
    private val WEIGHTS = intArrayOf(7, 9, 10, 5, 8, 4, 2, 1, 6, 3, 7, 9, 10, 5, 8, 4, 2)
    private const val CHECK_CODES = "10X98765432"
    private val PROVINCES = listOf(
        "11" to "北京", "12" to "天津", "13" to "河北", "14" to "山西", "15" to "内蒙古",
        "21" to "辽宁", "22" to "吉林", "23" to "黑龙江", "31" to "上海", "32" to "江苏",
        "33" to "浙江", "34" to "安徽", "35" to "福建", "36" to "江西", "37" to "山东",
        "41" to "河南", "42" to "湖北", "43" to "湖南", "44" to "广东", "45" to "广西",
        "46" to "海南", "50" to "重庆", "51" to "四川", "52" to "贵州", "53" to "云南",
        "54" to "西藏", "61" to "陕西", "62" to "甘肃", "63" to "青海", "64" to "宁夏",
        "65" to "新疆", "71" to "台湾", "81" to "香港", "82" to "澳门", "91" to "国外",
    )

    /** `true` for a valid 15- or 18-digit card number. */
    fun isValidCard(idCard: CharSequence?): Boolean =
        isValidCard18(idCard) || isValidCard15(idCard)

    /** `true` when [idcard] is 18 characters with a valid birthday and check digit. */
    fun isValidCard18(idcard: CharSequence?, ignoreCase: Boolean = true): Boolean {
        val text = idcard?.toString() ?: return false
        if (text.length != 18) return false
        val digits = text.substring(0, 17)
        if (!digits.all { it.isDigit() }) return false
        val checkCode = text[17]
        if (!checkCode.isDigit() && !(ignoreCase && (checkCode == 'x' || checkCode == 'X'))) return false
        if (!isValidBirthday(text.substring(6, 14))) return false
        val sum = WEIGHTS.indices.sumOf { index -> digits[index].digitToInt() * WEIGHTS[index] }
        return CHECK_CODES[sum % 11] == checkCode.uppercaseChar()
    }

    /** `true` when [idcard] is 15 digits with a valid 1900s birthday. */
    fun isValidCard15(idcard: CharSequence?): Boolean {
        val text = idcard?.toString() ?: return false
        if (text.length != 15 || !text.all { it.isDigit() }) return false
        return isValidBirthday("19" + text.substring(6, 12))
    }

    /** Converts a 15-digit card number to its 18-digit form; other input is returned unchanged. */
    fun convert15To18(idCard: String?): String? {
        val text = idCard ?: return null
        if (text.length != 15) return text
        val body = StringBuilder(text).insert(6, "19").toString()
        val sum = WEIGHTS.indices.sumOf { index -> body[index].digitToInt() * WEIGHTS[index] }
        return body + CHECK_CODES[sum % 11]
    }

    /** Converts an 18-digit card number to its 15-digit form; other input is returned unchanged. */
    fun convert18To15(idCard: String?): String? {
        val text = idCard ?: return null
        if (text.length != 18) return text
        return text.substring(0, 6) + text.substring(8, 17)
    }

    /** The birthday as `yyyyMMdd`, or `null` when the number is not a valid card. */
    fun getBirthByIdCard(idcard: String?): String? {
        val text = idcard ?: return null
        return when {
            isValidCard18(text) -> text.substring(6, 14)
            isValidCard15(text) -> "19" + text.substring(6, 12)
            else -> null
        }
    }

    /** The birthday as a [LocalDate], or `null`. */
    fun getBirthDate(idCard: String?): LocalDate? {
        val birthday = getBirthByIdCard(idCard) ?: return null
        return LocalDate(
            birthday.substring(0, 4).toInt(),
            birthday.substring(4, 6).toInt(),
            birthday.substring(6, 8).toInt(),
        )
    }

    /** The birth year, or `null`. */
    fun getYearByIdCard(idcard: String?): Int? = getBirthByIdCard(idcard)?.substring(0, 4)?.toIntOrNull()

    /** The birth month, or `null`. */
    fun getMonthByIdCard(idcard: String?): Int? = getBirthByIdCard(idcard)?.substring(4, 6)?.toIntOrNull()

    /** The birth day, or `null`. */
    fun getDayByIdCard(idcard: String?): Int? = getBirthByIdCard(idcard)?.substring(6, 8)?.toIntOrNull()

    /** `1` for male, `0` for female, or `-1` when the number is invalid. */
    fun getGenderByIdCard(idcard: String?): Int {
        val text = idcard ?: return -1
        val genderDigit = when {
            isValidCard18(text) -> text[16]
            isValidCard15(text) -> text[14]
            else -> return -1
        }
        return if (genderDigit.digitToInt() % 2 == 1) 1 else 0
    }

    /** The two-digit province code, or `null`. */
    fun getProvinceCodeByIdCard(idcard: String?): String? {
        val text = idcard?.takeIf { isValidCard(it) } ?: return null
        return text.substring(0, 2)
    }

    /** The province name for the card's code, or `null`. */
    fun getProvinceByIdCard(idcard: String?): String? {
        val code = getProvinceCodeByIdCard(idcard) ?: return null
        return PROVINCES.firstOrNull { it.first == code }?.second
    }

    /** The 4-digit city code, or `null`. */
    fun getCityCodeByIdCard(idcard: String?): String? {
        val text = idcard?.takeIf { isValidCard(it) } ?: return null
        return text.substring(0, 4)
    }

    /** The 6-digit district code, or `null`. */
    fun getDistrictCodeByIdCard(idcard: String?): String? {
        val text = idcard?.takeIf { isValidCard(it) } ?: return null
        return text.substring(0, 6)
    }

    /** Age in whole years on [today]; `-1` when the number is invalid. */
    fun getAgeByIdCard(idcard: String?, today: LocalDate = today()): Int {
        val birthday = getBirthDate(idcard) ?: return -1
        var age = today.year - birthday.year
        val monthDayComparison = today.month.ordinal compareTo birthday.month.ordinal
        if (monthDayComparison < 0 || (monthDayComparison == 0 && today.day < birthday.day)) {
            age--
        }
        return age
    }

    /** Masks characters `[startInclude, endExclude)` of the card number. */
    fun hide(idcard: CharSequence?, startInclude: Int, endExclude: Int): String =
        DesensitizedUtil.hide(idcard, startInclude, endExclude)

    private fun today(): LocalDate =
        Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date

    private fun isValidBirthday(yyyymmdd: String): Boolean {
        if (yyyymmdd.length != 8) return false
        val year = yyyymmdd.substring(0, 4).toIntOrNull() ?: return false
        val month = yyyymmdd.substring(4, 6).toIntOrNull() ?: return false
        val day = yyyymmdd.substring(6, 8).toIntOrNull() ?: return false
        if (year < 1800 || year > 2100 || month !in 1..12 || day < 1) return false
        return day <= daysInMonth(year, month)
    }

    private fun daysInMonth(year: Int, month: Int): Int = when (month) {
        1, 3, 5, 7, 8, 10, 12 -> 31
        4, 6, 9, 11 -> 30
        else -> if (year % 4 == 0 && (year % 100 != 0 || year % 400 == 0)) 29 else 28
    }
}
