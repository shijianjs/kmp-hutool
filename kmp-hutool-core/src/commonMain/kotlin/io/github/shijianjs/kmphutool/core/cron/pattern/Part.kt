package io.github.shijianjs.kmphutool.core.cron.pattern

import io.github.shijianjs.kmphutool.core.cron.CronException
import io.github.shijianjs.kmphutool.core.date.Month
import io.github.shijianjs.kmphutool.core.date.Week

/**
 * Position and legal range of one cron field.
 *
 * Port of `cn.hutool.cron.pattern.Part`, without the `java.util.Calendar` field numbers because
 * matching is done on [kotlinx.datetime.LocalDateTime] fields.
 *
 * @hutool cn.hutool.cron.pattern.Part
 */
enum class Part(val min: Int, val max: Int) {
    SECOND(0, 59),
    MINUTE(0, 59),
    HOUR(0, 23),
    DAY_OF_MONTH(1, 32),
    MONTH(Month.JANUARY.getValueBaseOne(), Month.DECEMBER.getValueBaseOne()),
    DAY_OF_WEEK(Week.SUNDAY.ordinal, Week.SATURDAY.ordinal),
    YEAR(1970, 2099);

    fun checkValue(value: Int): Int {
        if (value !in min..max) {
            throw CronException("{} value {} out of range: [{} , {}]", name, value, min, max)
        }
        return value
    }

    companion object {
        fun of(i: Int): Part = entries[i]
    }
}
