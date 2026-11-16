package io.github.shijianjs.kmphutool.core.cron.pattern.matcher

/**
 * A matcher that accepts every value.
 *
 * Port of `cn.hutool.cron.pattern.matcher.AlwaysTrueMatcher`.
 *
 * @hutool cn.hutool.cron.pattern.matcher.AlwaysTrueMatcher
 */
class AlwaysTrueMatcher : PartMatcher {
    override fun match(value: Int): Boolean = true

    override fun nextAfter(value: Int): Int = value

    override fun toString(): String = "[Matcher]: always true."

    companion object {
        val INSTANCE: AlwaysTrueMatcher = AlwaysTrueMatcher()
    }
}
