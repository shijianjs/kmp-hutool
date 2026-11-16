package io.github.shijianjs.kmphutool.core.date.format

import kotlinx.datetime.TimeZone

/**
 * Pattern, zone and locale of a date formatter.
 *
 * Port of `cn.hutool.core.date.format.DateBasic`, adapted to common types: [kotlinx.datetime.TimeZone]
 * replaces `java.util.TimeZone` and [localeTag] is an IETF language tag (for example `zh-CN`) because
 * `java.util.Locale` has no common counterpart.
 *
 * @hutool cn.hutool.core.date.format.DateBasic
 */
interface DateBasic {
    /** Pattern compatible with [kotlinx.datetime.format.DateTimeFormat]. */
    val pattern: String

    val timeZone: TimeZone

    /** IETF language tag such as `en-US` or `zh-CN`. */
    val localeTag: String
}
