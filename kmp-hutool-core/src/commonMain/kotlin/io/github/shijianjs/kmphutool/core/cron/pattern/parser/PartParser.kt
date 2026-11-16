package io.github.shijianjs.kmphutool.core.cron.pattern.parser

import io.github.shijianjs.kmphutool.core.cron.CronException
import io.github.shijianjs.kmphutool.core.cron.pattern.Part
import io.github.shijianjs.kmphutool.core.cron.pattern.matcher.AlwaysTrueMatcher
import io.github.shijianjs.kmphutool.core.cron.pattern.matcher.BoolArrayMatcher
import io.github.shijianjs.kmphutool.core.cron.pattern.matcher.DayOfMonthMatcher
import io.github.shijianjs.kmphutool.core.cron.pattern.matcher.PartMatcher
import io.github.shijianjs.kmphutool.core.cron.pattern.matcher.YearValueMatcher
import io.github.shijianjs.kmphutool.core.date.Month
import io.github.shijianjs.kmphutool.core.date.Week

/**
 * Parses one field of a cron expression.
 *
 * Port of `cn.hutool.cron.pattern.parser.PartParser`. Supports `*`, `?`, `L`, lists, ranges,
 * inverse ranges, step values, month names and weekday names.
 *
 * @hutool cn.hutool.cron.pattern.parser.PartParser
 */
class PartParser(private val part: Part) {
    fun parse(value: String): PartMatcher {
        if (isMatchAllStr(value)) return AlwaysTrueMatcher.INSTANCE
        val values = parseArray(value)
        if (values.isEmpty()) throw CronException("Invalid part value: [{}]", value)
        return when (part) {
            Part.DAY_OF_MONTH -> DayOfMonthMatcher(values)
            Part.YEAR -> YearValueMatcher(values)
            else -> BoolArrayMatcher(values)
        }
    }

    private fun parseArray(value: String): List<Int> {
        val values = LinkedHashSet<Int>()
        for (part in value.split(',')) {
            values.addAll(parseStep(part))
        }
        return values.toList()
    }

    private fun parseStep(value: String): List<Int> {
        val parts = value.split('/')
        return when (parts.size) {
            1 -> parseRange(value, -1)
            2 -> {
                val step = parseNumber(parts[1], false)
                if (step < 1) throw CronException("Non positive divisor for field: [{}]", value)
                parseRange(parts[0], step)
            }
            else -> throw CronException("Invalid syntax of field: [{}]", value)
        }
    }

    private fun parseRange(value: String, initialStep: Int): List<Int> {
        val results = ArrayList<Int>()
        var step = initialStep
        if (value.length <= 2) {
            var minValue = part.min
            if (!isMatchAllStr(value)) {
                minValue = maxOf(minValue, parseNumber(value, true))
            } else if (step < 1) {
                step = 1
            }
            if (step > 0) {
                val maxValue = part.max
                if (minValue > maxValue) throw CronException("Invalid value {} > {}", minValue, maxValue)
                var i = minValue
                while (i <= maxValue) {
                    results.add(i)
                    i += step
                }
            } else {
                results.add(minValue)
            }
            return results
        }

        val parts = value.split('-')
        when (parts.size) {
            1 -> {
                val v1 = parseNumber(value, true)
                if (step > 0) appendRange(v1, part.max, step, results) else results.add(v1)
            }
            2 -> {
                val v1 = parseNumber(parts[0], true)
                val v2 = parseNumber(parts[1], true)
                if (step < 1) step = 1
                if (v1 <= v2) {
                    appendRange(v1, v2, step, results)
                } else {
                    appendRange(v1, part.max, step, results)
                    appendRange(part.min, v2, step, results)
                }
            }
            else -> throw CronException("Invalid syntax of field: [{}]", value)
        }
        return results
    }

    private fun parseNumber(value: String, checkValue: Boolean): Int {
        var i = try {
            value.toInt()
        } catch (_: NumberFormatException) {
            parseAlias(value)
        }
        if (i < 0) i += part.max
        if (part == Part.DAY_OF_WEEK && i == 7) i = 0
        return if (checkValue) part.checkValue(i) else i
    }

    private fun parseAlias(name: String): Int {
        if (name.equals("L", ignoreCase = true)) return part.max
        return when (part) {
            Part.MONTH -> Month.of(name).getValueBaseOne()
            Part.DAY_OF_WEEK -> Week.of(name).ordinal
            else -> throw CronException("Invalid alias value: [{}]", name)
        }
    }

    private fun appendRange(from: Int, to: Int, step: Int, results: MutableList<Int>) {
        var i = from
        while (i <= to) {
            results.add(i)
            i += step
        }
    }

    companion object {
        fun of(part: Part): PartParser = PartParser(part)

        private fun isMatchAllStr(value: String): Boolean =
            value.length == 1 && (value == "*" || value == "?")
    }
}
