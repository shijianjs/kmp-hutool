package io.github.shijianjs.kmphutool.core

import io.github.shijianjs.kmphutool.core.lang.PatternPool
import io.github.shijianjs.kmphutool.core.lang.RegexPool
import kotlin.test.Test
import kotlin.test.assertTrue

class RegexPoolTest {
    @Test
    fun commonPatternsWork() {
        assertTrue(Regex(RegexPool.NUMBERS).matches("12345"))
        assertTrue(Regex(RegexPool.EMAIL).matches("alice@example.com"))
        assertTrue(Regex(RegexPool.MAC_ADDRESS).matches("00:11:22:33:44:55"))
        assertTrue(Regex(RegexPool.HEX).matches("DeadBeef"))
    }

    @Test
    fun allPortablePatternsCompile() {
        val patterns = listOf(
            RegexPool.GENERAL, RegexPool.NUMBERS, RegexPool.WORD, RegexPool.CHINESE, RegexPool.CHINESES,
            RegexPool.GROUP_VAR, RegexPool.IPV4, RegexPool.IPV6, RegexPool.MONEY, RegexPool.EMAIL,
            RegexPool.EMAIL_WITH_CHINESE, RegexPool.MOBILE, RegexPool.MOBILE_HK, RegexPool.MOBILE_TW,
            RegexPool.MOBILE_MO, RegexPool.TEL, RegexPool.TEL_400_800, RegexPool.CITIZEN_ID,
            RegexPool.ZIP_CODE, RegexPool.BIRTHDAY, RegexPool.URI, RegexPool.URL, RegexPool.URL_HTTP,
            RegexPool.GENERAL_WITH_CHINESE, RegexPool.UUID, RegexPool.UUID_SIMPLE, RegexPool.MAC_ADDRESS,
            RegexPool.HEX, RegexPool.TIME, RegexPool.PLATE_NUMBER, RegexPool.CREDIT_CODE, RegexPool.CAR_VIN,
            RegexPool.CAR_DRIVING_LICENCE, RegexPool.CHINESE_NAME,
        )
        val names = listOf("GENERAL", "NUMBERS", "WORD", "CHINESE", "CHINESES", "GROUP_VAR", "IPV4", "IPV6", "MONEY", "EMAIL", "EMAIL_WITH_CHINESE", "MOBILE", "MOBILE_HK", "MOBILE_TW", "MOBILE_MO", "TEL", "TEL_400_800", "CITIZEN_ID", "ZIP_CODE", "BIRTHDAY", "URI", "URL", "URL_HTTP", "GENERAL_WITH_CHINESE", "UUID", "UUID_SIMPLE", "MAC_ADDRESS", "HEX", "TIME", "PLATE_NUMBER", "CREDIT_CODE", "CAR_VIN", "CAR_DRIVING_LICENCE", "CHINESE_NAME")
        for ((index, pattern) in patterns.withIndex()) {
            try {
                Regex(pattern)
            } catch (e: Throwable) {
                throw AssertionError("${names[index]} failed: ${e.message}")
            }
        }
    }

    @Test
    fun patternPoolCachesAndReuses() {
        val first = PatternPool.get("a+b")
        val second = PatternPool.get("a+b")
        assertTrue(first === second)
        assertTrue(first.containsMatchIn("xxaaab"))
        assertTrue(PatternPool.get("ABC", 2).matches("abc"))
        PatternPool.remove("a+b", 0)
        PatternPool.clear()
    }
}
