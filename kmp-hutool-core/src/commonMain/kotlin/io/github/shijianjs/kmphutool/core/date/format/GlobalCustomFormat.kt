package io.github.shijianjs.kmphutool.core.date.format

import kotlinx.datetime.Instant

/**
 * Registry for custom pseudo-patterns such as `#sss` (Unix seconds) and `#SSS` (epoch millis).
 *
 * Port of `cn.hutool.core.date.format.GlobalCustomFormat`. `java.util.Date` becomes
 * `kotlinx.datetime.Instant` and `java.util.function.Function` becomes a Kotlin function type.
 *
 * @hutool cn.hutool.core.date.format.GlobalCustomFormat
 */
object GlobalCustomFormat {
    /** Unix seconds. */
    const val FORMAT_SECONDS = "#sss"

    /** Epoch milliseconds. */
    const val FORMAT_MILLISECONDS = "#SSS"

    private val formatterMap = mutableMapOf<String, (Instant) -> String>()
    private val parserMap = mutableMapOf<String, (CharSequence) -> Instant>()

    init {
        formatterMap[FORMAT_SECONDS] = { it.epochSeconds.toString() }
        parserMap[FORMAT_SECONDS] = { Instant.fromEpochSeconds(it.toString().toLong()) }
        formatterMap[FORMAT_MILLISECONDS] = { it.toEpochMilliseconds().toString() }
        parserMap[FORMAT_MILLISECONDS] = { Instant.fromEpochMilliseconds(it.toString().toLong()) }
    }

    fun putFormatter(format: String, func: (Instant) -> String) {
        formatterMap[format] = func
    }

    fun putParser(format: String, func: (CharSequence) -> Instant) {
        parserMap[format] = func
    }

    fun isCustomFormat(format: String): Boolean = formatterMap.containsKey(format)

    /** Formats [date] with a registered custom format, or returns `null`. */
    fun format(date: Instant, format: String): String? = formatterMap[format]?.invoke(date)

    /** Parses [dateStr] with a registered custom format, or returns `null`. */
    fun parse(dateStr: CharSequence, format: String): Instant? = parserMap[format]?.invoke(dateStr)
}
