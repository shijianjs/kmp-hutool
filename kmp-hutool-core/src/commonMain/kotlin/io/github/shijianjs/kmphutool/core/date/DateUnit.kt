package io.github.shijianjs.kmphutool.core.date

import kotlin.time.DurationUnit

/**
 * Date/time units expressed in milliseconds.
 *
 * Port of `cn.hutool.core.date.DateUnit`. `java.time.ChronoUnit` is replaced by Kotlin's
 * [DurationUnit].
 *
 * @hutool cn.hutool.core.date.DateUnit
 */
enum class DateUnit(private val millis: Long) {
    MS(1),
    SECOND(1000),
    MINUTE(SECOND.millis * 60),
    HOUR(MINUTE.millis * 60),
    DAY(HOUR.millis * 24),
    WEEK(DAY.millis * 7);

    /** Milliseconds in this unit. */
    fun getMillis(): Long = millis

    /** The matching Kotlin [DurationUnit]. */
    fun toDurationUnit(): DurationUnit = when (this) {
        MS -> DurationUnit.MILLISECONDS
        SECOND -> DurationUnit.SECONDS
        MINUTE -> DurationUnit.MINUTES
        HOUR -> DurationUnit.HOURS
        DAY -> DurationUnit.DAYS
        WEEK -> DurationUnit.DAYS
    }

    companion object {
        /** Maps a [DurationUnit] to the closest supported date unit, or `null`. */
        fun of(unit: DurationUnit): DateUnit? = when (unit) {
            DurationUnit.MILLISECONDS -> MS
            DurationUnit.SECONDS -> SECOND
            DurationUnit.MINUTES -> MINUTE
            DurationUnit.HOURS -> HOUR
            DurationUnit.DAYS -> DAY
            else -> null
        }
    }
}
