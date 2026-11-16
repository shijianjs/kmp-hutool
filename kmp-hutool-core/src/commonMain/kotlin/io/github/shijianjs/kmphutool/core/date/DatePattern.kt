package io.github.shijianjs.kmphutool.core.date

/**
 * Common date/time patterns, in the `SimpleDateFormat` syntax Hutool exposes.
 *
 * Port of `cn.hutool.core.date.DatePattern`. Only the pattern strings are ported: the
 * `FastDateFormat`/`DateTimeFormatter` constants and `createFormatter` need a JVM pattern engine
 * (`java.text`/`java.time`), and kotlinx-datetime builds formatters from its own pattern API.
 *
 * @hutool cn.hutool.core.date.DatePattern
 */
object DatePattern {
    /** Normalized datetime matcher. */
    const val NORM_REGEX = """\d{4}-\d{1,2}-\d{1,2}(\s\d{1,2}:\d{1,2}(:\d{1,2})?(.\d{1,6})?)?"""

    const val NORM_YEAR_PATTERN = "yyyy"
    const val NORM_MONTH_PATTERN = "yyyy-MM"
    const val SIMPLE_MONTH_PATTERN = "yyyyMM"
    const val NORM_DATE_PATTERN = "yyyy-MM-dd"
    const val NORM_TIME_PATTERN = "HH:mm:ss"
    const val NORM_DATETIME_MINUTE_PATTERN = "yyyy-MM-dd HH:mm"
    const val NORM_DATETIME_PATTERN = "yyyy-MM-dd HH:mm:ss"
    const val NORM_DATETIME_MS_PATTERN = "yyyy-MM-dd HH:mm:ss.SSS"
    const val ISO8601_PATTERN = "yyyy-MM-dd HH:mm:ss,SSS"
    const val CHINESE_DATE_PATTERN = "yyyy年MM月dd日"
    const val CHINESE_DATE_TIME_PATTERN = "yyyy年MM月dd日HH时mm分ss秒"
    const val PURE_DATE_PATTERN = "yyyyMMdd"
    const val PURE_TIME_PATTERN = "HHmmss"
    const val PURE_DATETIME_PATTERN = "yyyyMMddHHmmss"
    const val PURE_DATETIME_MS_PATTERN = "yyyyMMddHHmmssSSS"
    const val HTTP_DATETIME_PATTERN = "EEE, dd MMM yyyy HH:mm:ss z"
    const val JDK_DATETIME_PATTERN = "EEE MMM dd HH:mm:ss zzz yyyy"
    const val UTC_SIMPLE_PATTERN = "yyyy-MM-dd'T'HH:mm:ss"
    const val UTC_SIMPLE_MS_PATTERN = "yyyy-MM-dd'T'HH:mm:ss.SSS"
    const val UTC_PATTERN = "yyyy-MM-dd'T'HH:mm:ss'Z'"
    const val UTC_WITH_ZONE_OFFSET_PATTERN = "yyyy-MM-dd'T'HH:mm:ssZ"
    const val UTC_WITH_XXX_OFFSET_PATTERN = "yyyy-MM-dd'T'HH:mm:ssXXX"
    const val UTC_SIMPLE_MINUTE_WITH_XXX_OFFSET_PATTERN = "yyyy-MM-dd'T'HH:mmXXX"
    const val UTC_MS_PATTERN = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'"
    const val UTC_MS_WITH_ZONE_OFFSET_PATTERN = "yyyy-MM-dd'T'HH:mm:ss.SSSZ"
    const val UTC_MS_WITH_XXX_OFFSET_PATTERN = "yyyy-MM-dd'T'HH:mm:ss.SSSXXX"
}
