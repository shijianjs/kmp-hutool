package io.github.shijianjs.kmphutool.core.lang

/**
 * Cached, compiled regular expressions.
 *
 * Port of `cn.hutool.core.lang.PatternPool`. Common code has no `WeakHashMap`, so the cache is a
 * plain [HashMap]; call [clear] when a long-lived process stops needing old patterns.
 *
 * @hutool cn.hutool.core.lang.PatternPool
 */
object PatternPool {
    val MAC_ADDRESS = Regex(RegexPool.MAC_ADDRESS)
    val HEX = Regex(RegexPool.HEX)
    val TIME = Regex(RegexPool.TIME)
    val CREDIT_CODE = Regex(RegexPool.CREDIT_CODE)
    val CAR_VIN = Regex(RegexPool.CAR_VIN)
    val CAR_DRIVING_LICENCE = Regex(RegexPool.CAR_DRIVING_LICENCE)
    val CHINESE_NAME = Regex(RegexPool.CHINESE_NAME)

    private val pool = HashMap<kotlin.Pair<String, Int>, Regex>()

    fun get(regex: String): Regex = get(regex, 0)

    fun get(regex: String, flags: Int): Regex {
        val key = kotlin.Pair(regex, flags)
        return pool.getOrPut(key) { Regex(regex, options(flags)) }
    }

    fun remove(regex: String, flags: Int): Regex? = pool.remove(kotlin.Pair(regex, flags))

    fun clear() = pool.clear()

    private fun options(flags: Int): Set<RegexOption> {
        if (flags == 0) return emptySet()
        val result = mutableSetOf<RegexOption>()
        if (flags and 2 != 0) result += RegexOption.IGNORE_CASE
        if (flags and 8 != 0) result += RegexOption.MULTILINE
        if (flags and (4 or 16 or 32) != 0) {
            throw IllegalArgumentException("Regex flags COMMENTS, LITERAL and DOT_MATCHES_ALL are not portable to Kotlin/JS")
        }
        return result
    }
}
