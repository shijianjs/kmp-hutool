package io.github.shijianjs.kmphutool.core.cron.pattern

import io.github.shijianjs.kmphutool.core.builder.Builder

/**
 * Fluent builder for a cron expression string.
 *
 * Port of `cn.hutool.cron.pattern.CronPatternBuilder`. Unset minute/hour/day/month/weekday fields
 * default to `*`; an unset second or year field is omitted, so the result has five, six or seven
 * fields.
 *
 * @hutool cn.hutool.cron.pattern.CronPatternBuilder
 */
class CronPatternBuilder : Builder<String> {
    private val parts = arrayOfNulls<String>(7)

    fun setValues(part: Part, vararg values: Int): CronPatternBuilder {
        for (value in values) part.checkValue(value)
        return set(part, values.joinToString(","))
    }

    fun setRange(part: Part, begin: Int, end: Int): CronPatternBuilder {
        part.checkValue(begin)
        part.checkValue(end)
        return set(part, "$begin-$end")
    }

    fun set(part: Part, value: String): CronPatternBuilder = apply {
        parts[part.ordinal] = value
    }

    override fun build(): String {
        for (i in Part.MINUTE.ordinal until Part.YEAR.ordinal) {
            if (parts[i].isNullOrBlank()) parts[i] = "*"
        }
        return parts.filterNotNull().joinToString(" ")
    }

    companion object {
        fun of(): CronPatternBuilder = CronPatternBuilder()
    }
}
