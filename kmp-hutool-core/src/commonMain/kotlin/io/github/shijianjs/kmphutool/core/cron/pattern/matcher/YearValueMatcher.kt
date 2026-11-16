package io.github.shijianjs.kmphutool.core.cron.pattern.matcher

/**
 * Year matcher backed by a set instead of a boolean array.
 *
 * Port of `cn.hutool.cron.pattern.matcher.YearValueMatcher`.
 *
 * @hutool cn.hutool.cron.pattern.matcher.YearValueMatcher
 */
class YearValueMatcher(intValueList: Collection<Int>) : PartMatcher {
    private val valueList: Set<Int> = intValueList.toCollection(LinkedHashSet())

    override fun match(value: Int): Boolean = valueList.contains(value)

    override fun nextAfter(value: Int): Int =
        valueList.firstOrNull { it >= value } ?: -1
}
