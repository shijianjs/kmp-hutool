package io.github.shijianjs.kmphutool.core.cron.pattern.parser

import io.github.shijianjs.kmphutool.core.cron.CronException
import io.github.shijianjs.kmphutool.core.cron.pattern.Part
import io.github.shijianjs.kmphutool.core.cron.pattern.matcher.AlwaysTrueMatcher
import io.github.shijianjs.kmphutool.core.cron.pattern.matcher.PartMatcher
import io.github.shijianjs.kmphutool.core.cron.pattern.matcher.PatternMatcher

/**
 * Parses a full cron expression, including `a | b` alternatives.
 *
 * Port of `cn.hutool.cron.pattern.parser.PatternParser`. Five fields are `minute hour day month
 * weekday`; six fields add seconds; seven fields also add a year.
 *
 * @hutool cn.hutool.cron.pattern.parser.PatternParser
 */
object PatternParser {
    private val SECOND_VALUE_PARSER = PartParser.of(Part.SECOND)
    private val MINUTE_VALUE_PARSER = PartParser.of(Part.MINUTE)
    private val HOUR_VALUE_PARSER = PartParser.of(Part.HOUR)
    private val DAY_OF_MONTH_VALUE_PARSER = PartParser.of(Part.DAY_OF_MONTH)
    private val MONTH_VALUE_PARSER = PartParser.of(Part.MONTH)
    private val DAY_OF_WEEK_VALUE_PARSER = PartParser.of(Part.DAY_OF_WEEK)
    private val YEAR_VALUE_PARSER = PartParser.of(Part.YEAR)

    fun parse(cronPattern: String): List<PatternMatcher> =
        cronPattern.split('|').map { parseSingle(it.trim()) }

    private fun parseSingle(pattern: String): PatternMatcher {
        val parts = pattern.split(Regex("\\s+")).filter { it.isNotEmpty() }
        if (parts.size !in 5..7) {
            throw CronException("Pattern [{}] is invalid, it must be 5-7 parts!", pattern)
        }

        val offset = if (parts.size == 6 || parts.size == 7) 1 else 0
        val secondPart = if (offset == 1) parts[0] else "0"
        val yearMatcher: PartMatcher =
            if (parts.size == 7) YEAR_VALUE_PARSER.parse(parts[6]) else AlwaysTrueMatcher.INSTANCE

        return PatternMatcher(
            SECOND_VALUE_PARSER.parse(secondPart),
            MINUTE_VALUE_PARSER.parse(parts[offset]),
            HOUR_VALUE_PARSER.parse(parts[1 + offset]),
            DAY_OF_MONTH_VALUE_PARSER.parse(parts[2 + offset]),
            MONTH_VALUE_PARSER.parse(parts[3 + offset]),
            DAY_OF_WEEK_VALUE_PARSER.parse(parts[4 + offset]),
            yearMatcher,
        )
    }
}
