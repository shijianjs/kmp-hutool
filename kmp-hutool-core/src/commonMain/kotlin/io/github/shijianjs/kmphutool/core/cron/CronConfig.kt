package io.github.shijianjs.kmphutool.core.cron

import kotlinx.datetime.TimeZone

/**
 * Scheduler configuration.
 *
 * Port of `cn.hutool.cron.CronConfig`, with [kotlinx.datetime.TimeZone] replacing `java.util.TimeZone`
 * and Kotlin properties replacing the fluent getters/setters.
 *
 * @hutool cn.hutool.cron.CronConfig
 */
class CronConfig {
    var timeZone: TimeZone = TimeZone.currentSystemDefault()

    /** Whether the optional seconds field is matched. */
    var matchSecond: Boolean = false
}
