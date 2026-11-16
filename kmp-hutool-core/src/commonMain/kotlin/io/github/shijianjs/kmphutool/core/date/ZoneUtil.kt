package io.github.shijianjs.kmphutool.core.date

import kotlinx.datetime.TimeZone

/**
 * Returns a zone for an optional zone value.
 *
 * Port of `cn.hutool.core.date.ZoneUtil`. Common code has only one zone type
 * ([kotlinx.datetime.TimeZone]), so `toTimeZone` and `toZoneId` are identity functions with the
 * same null handling as upstream.
 *
 * @hutool cn.hutool.core.date.ZoneUtil
 */
object ZoneUtil {
    fun toTimeZone(zone: TimeZone?): TimeZone = zone ?: TimeZone.currentSystemDefault()

    fun toZoneId(timeZone: TimeZone?): TimeZone = timeZone ?: TimeZone.currentSystemDefault()
}
