package io.github.shijianjs.kmphutool.core.util

import io.github.shijianjs.kmphutool.core.exceptions.UtilException

/**
 * Version-expression matching: comparisons, ranges and "any of" lists.
 *
 * Port of `cn.hutool.core.util.VersionUtil`. Comparison is delegated to [StrUtil.compareVersion];
 * expressions are separated by `;` unless another delimiter is supplied.
 *
 * @hutool cn.hutool.core.util.VersionUtil
 */
object VersionUtil {
    private val COMPARE_REG = Regex("^[<>≥≤]=?")
    private const val DEFAULT_VERSIONS_DELIMITER = ";"

    /** Whether [currentVersion] matches any of [compareVersions]. */
    fun anyMatch(currentVersion: String, compareVersions: Collection<String>): Boolean =
        matchEl(currentVersion, compareVersions.joinToString(DEFAULT_VERSIONS_DELIMITER))

    /** Whether [currentVersion] matches any of [compareVersions]. */
    fun anyMatch(currentVersion: String, vararg compareVersions: String): Boolean =
        matchEl(currentVersion, compareVersions.joinToString(DEFAULT_VERSIONS_DELIMITER))

    /** Whether [currentVersion] is greater than [compareVersion]. */
    fun isGreaterThan(currentVersion: String, compareVersion: String): Boolean =
        matchEl(currentVersion, ">$compareVersion")

    /** Whether [currentVersion] is greater than or equal to [compareVersion]. */
    fun isGreaterThanOrEqual(currentVersion: String, compareVersion: String): Boolean =
        matchEl(currentVersion, ">=$compareVersion")

    /** Whether [currentVersion] is less than [compareVersion]. */
    fun isLessThan(currentVersion: String, compareVersion: String): Boolean =
        matchEl(currentVersion, "<$compareVersion")

    /** Whether [currentVersion] is less than or equal to [compareVersion]. */
    fun isLessThanOrEqual(currentVersion: String, compareVersion: String): Boolean =
        matchEl(currentVersion, "<=$compareVersion")

    /** Whether [currentVersion] satisfies [versionEl], using `;` as the list delimiter. */
    fun matchEl(currentVersion: String, versionEl: String): Boolean = matchEl(currentVersion, versionEl, DEFAULT_VERSIONS_DELIMITER)

    /**
     * Whether [currentVersion] satisfies [versionEl].
     *
     * Expressions may be combined with [versionsDelimiter]: `>=1.0`, `<2.0`, `1.0.1-1.2.4` (inclusive
     * range), an open range such as `-1.2.4`, or an exact version.
     */
    fun matchEl(currentVersion: String, versionEl: String, versionsDelimiter: String): Boolean {
        if (
            versionsDelimiter.isEmpty() ||
            versionsDelimiter == "-" ||
            COMPARE_REG.containsMatchIn(versionsDelimiter)
        ) {
            throw UtilException("非法的版本分隔符：$versionsDelimiter")
        }
        if (versionEl.isBlank()) return false
        val trimmedVersion = currentVersion.trim()
        val els = StrUtil.split(versionEl, versionsDelimiter, isTrim = true, ignoreEmpty = true)
        if (els.isEmpty()) return false

        for (raw in els) {
            val el = raw.trim()
            val match = COMPARE_REG.find(el)
            if (match != null) {
                val op = match.value
                val ver = el.removePrefix(op).let { if (it.equals("null", ignoreCase = true)) null else it }
                when (op) {
                    ">=", "≥" -> if (StrUtil.compareVersion(trimmedVersion, ver) >= 0) return true
                    "<=", "≤" -> if (StrUtil.compareVersion(trimmedVersion, ver) <= 0) return true
                    "<" -> if (StrUtil.compareVersion(trimmedVersion, ver) < 0) return true
                    ">" -> if (StrUtil.compareVersion(trimmedVersion, ver) > 0) return true
                    else -> return false
                }
            } else if ('-' in el) {
                val index = el.indexOf('-')
                val left = el.substring(0, index).trim()
                val right = el.substring(index + 1).trim()
                val leftMatch = left.isEmpty() || StrUtil.compareVersion(left, trimmedVersion) <= 0
                val rightMatch = right.isEmpty() || StrUtil.compareVersion(right, trimmedVersion) >= 0
                if (leftMatch && rightMatch) return true
            } else if (trimmedVersion == el) {
                return true
            }
        }
        return false
    }
}
