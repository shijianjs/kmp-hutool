package io.github.shijianjs.kmphutool.core.cron.pattern.matcher

/**
 * Matches one field value and finds the next matching value.
 *
 * Port of `cn.hutool.cron.pattern.matcher.PartMatcher`.
 *
 * @hutool cn.hutool.cron.pattern.matcher.PartMatcher
 */
interface PartMatcher {
    fun match(value: Int): Boolean

    /** The first matching value at or after [value]; implementations may wrap to their minimum. */
    fun nextAfter(value: Int): Int
}
