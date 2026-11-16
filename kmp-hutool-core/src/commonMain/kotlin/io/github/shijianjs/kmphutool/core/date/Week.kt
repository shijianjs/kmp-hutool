package io.github.shijianjs.kmphutool.core.date

/**
 * Weekdays with `java.util.Calendar` numbers (Sunday = 1) and Chinese names.
 *
 * Port of `cn.hutool.core.date.Week`. `java.time.DayOfWeek` is replaced by ISO numbers `1..7`
 * (Monday = 1).
 *
 * @hutool cn.hutool.core.date.Week
 */
enum class Week(private val value: Int) {
    SUNDAY(1),
    MONDAY(2),
    TUESDAY(3),
    WEDNESDAY(4),
    THURSDAY(5),
    FRIDAY(6),
    SATURDAY(7);

    /** The `Calendar` number, 1 = Sunday. */
    fun getValue(): Int = value

    /** The ISO-8601 number, 1 = Monday .. 7 = Sunday. */
    fun getIso8601Value(): Int = if (value == 1) 7 else value - 1

    /** Chinese name with the default `星期` prefix. */
    fun toChinese(): String = toChinese("星期")

    /** Chinese name with [weekNamePre], e.g. `周` → `周一`. */
    fun toChinese(weekNamePre: String): String = weekNamePre + when (this) {
        SUNDAY -> "日"
        MONDAY -> "一"
        TUESDAY -> "二"
        WEDNESDAY -> "三"
        THURSDAY -> "四"
        FRIDAY -> "五"
        SATURDAY -> "六"
    }

    companion object {
        private val ALIASES = arrayOf("sun", "mon", "tue", "wed", "thu", "fri", "sat")

        /** Maps a `Calendar` weekday, or `null` when out of range. */
        fun of(calendarWeekIntValue: Int): Week? = entries.getOrNull(calendarWeekIntValue - 1)

        /** Maps an ISO weekday (1 = Monday .. 7 = Sunday). */
        fun ofIso(isoValue: Int): Week? = if (isoValue in 1..7) of(if (isoValue == 7) 1 else isoValue + 1) else null

        /** Parses `sun`, `MONDAY`, `星期一` or `周一`. */
        fun of(name: String): Week {
            require(name.isNotBlank()) { "name must not be blank" }
            if (name.startsWith("星期") || name.startsWith("周")) {
                return when (name.last()) {
                    '一' -> MONDAY
                    '二' -> TUESDAY
                    '三' -> WEDNESDAY
                    '四' -> THURSDAY
                    '五' -> FRIDAY
                    '六' -> SATURDAY
                    '日' -> SUNDAY
                    else -> throw IllegalArgumentException("Invalid week name: $name")
                }
            }
            val aliasIndex = ALIASES.indexOfFirst { it.equals(name, ignoreCase = true) }
            if (aliasIndex >= 0) return entries[aliasIndex]
            return entries.first { it.name.equals(name, ignoreCase = true) }
        }
    }
}
