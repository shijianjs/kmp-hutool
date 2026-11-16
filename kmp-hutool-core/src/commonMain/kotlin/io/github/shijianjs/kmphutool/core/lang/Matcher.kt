package io.github.shijianjs.kmphutool.core.lang

/**
 * Tests a value against a rule.
 *
 * Port of `cn.hutool.core.lang.Matcher`, used by the text finders and replacement chains.
 *
 * @hutool cn.hutool.core.lang.Matcher
 */
fun interface Matcher<T> {
    /** `true` when [t] satisfies this matcher. */
    fun match(t: T): Boolean
}
