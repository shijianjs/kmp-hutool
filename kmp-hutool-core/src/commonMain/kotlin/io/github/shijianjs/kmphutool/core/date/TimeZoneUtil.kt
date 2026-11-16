package io.github.shijianjs.kmphutool.core.date

import kotlinx.datetime.TimeZone

/**
 * Time-zone helpers for platforms without `java.util.TimeZone`.
 *
 * Port of `cn.hutool.core.date.TimeZoneUtil`, adapted to [kotlinx.datetime.TimeZone]. Upstream's
 * `ZoneId.SHORT_IDS` mapping only exists on the JVM and is not reproduced; unknown IDs fall back to
 * GMT, mirroring `TimeZone.getTimeZone` behaviour.
 *
 * @hutool cn.hutool.core.date.TimeZoneUtil
 */
object TimeZoneUtil {
    const val GMT_ID: String = "GMT"

    val GMT: TimeZone = TimeZone.of(GMT_ID)

    /** Returns the zone named [id], or [GMT] when the ID is unknown. */
    fun getTimeZone(id: String): TimeZone = try {
        TimeZone.of(id)
    } catch (_: IllegalArgumentException) {
        GMT
    }

    /** Returns [timeZone], or the current system zone when it is `null`. */
    fun toTimeZone(timeZone: TimeZone?): TimeZone = timeZone ?: TimeZone.currentSystemDefault()
}
