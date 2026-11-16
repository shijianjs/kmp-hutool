package io.github.shijianjs.kmphutool.core.date

/**
 * Formats a duration as `XX天XX小时XX分XX秒XX毫秒`.
 *
 * Port of `cn.hutool.core.date.BetweenFormatter`. The java.util.function formatter hook becomes a
 * Kotlin function type.
 *
 * @hutool cn.hutool.core.date.BetweenFormatter
 */
class BetweenFormatter(
    private var betweenMs: Long,
    private var level: Level,
    private val levelMaxCount: Int = 0,
) {
    /** How the unit names are rendered; defaults to [Level.getName]. */
    var levelFormatter: (Level) -> String = { it.getName() }

    /** Separator inserted between non-zero units, empty by default. */
    var separator: String = ""

    /** Formats the duration. */
    fun format(): String {
        val sb = StringBuilder()
        if (betweenMs > 0) {
            val day = betweenMs / DateUnit.DAY.getMillis()
            val hour = betweenMs / DateUnit.HOUR.getMillis() - day * 24
            val minute = betweenMs / DateUnit.MINUTE.getMillis() - day * 24 * 60 - hour * 60
            val betweenOfSecond = ((day * 24 + hour) * 60 + minute) * 60
            val second = betweenMs / DateUnit.SECOND.getMillis() - betweenOfSecond
            val millisecond = betweenMs - (betweenOfSecond + second) * 1000

            val levelOrdinal = level.ordinal
            var levelCount = 0
            if (isLevelCountValid(levelCount) && day > 0) {
                sb.append(day).append(levelFormatter(Level.DAY)).append(separator)
                levelCount++
            }
            if (isLevelCountValid(levelCount) && hour != 0L && levelOrdinal >= Level.HOUR.ordinal) {
                sb.append(hour).append(levelFormatter(Level.HOUR)).append(separator)
                levelCount++
            }
            if (isLevelCountValid(levelCount) && minute != 0L && levelOrdinal >= Level.MINUTE.ordinal) {
                sb.append(minute).append(levelFormatter(Level.MINUTE)).append(separator)
                levelCount++
            }
            if (isLevelCountValid(levelCount) && second != 0L && levelOrdinal >= Level.SECOND.ordinal) {
                sb.append(second).append(levelFormatter(Level.SECOND)).append(separator)
                levelCount++
            }
            if (isLevelCountValid(levelCount) && millisecond != 0L && levelOrdinal >= Level.MILLISECOND.ordinal) {
                sb.append(millisecond).append(levelFormatter(Level.MILLISECOND)).append(separator)
            }
        }

        if (sb.isEmpty()) {
            sb.append(0).append(levelFormatter(level))
        } else if (separator.isNotEmpty()) {
            sb.setLength(sb.length - separator.length)
        }
        return sb.toString()
    }

    /** The duration in milliseconds. */
    fun getBetweenMs(): Long = betweenMs

    /** Replaces the duration in milliseconds. */
    fun setBetweenMs(betweenMs: Long) {
        this.betweenMs = betweenMs
    }

    /** The highest unit rendered. */
    fun getLevel(): Level = level

    /** Sets the highest unit rendered. */
    fun setLevel(level: Level) {
        this.level = level
    }

    /** Sets the unit-name renderer. */
    fun setLevelFormatter(formatter: (Level) -> String): BetweenFormatter {
        this.levelFormatter = formatter
        return this
    }

    /** Sets the separator between units. */
    fun setSeparator(separator: String?): BetweenFormatter {
        this.separator = separator ?: ""
        return this
    }

    override fun toString(): String = format()

    private fun isLevelCountValid(levelCount: Int): Boolean = levelMaxCount <= 0 || levelCount < levelMaxCount

    /** Units, from coarse to fine. */
    enum class Level(private val displayName: String) {
        DAY("天"),
        HOUR("小时"),
        MINUTE("分"),
        SECOND("秒"),
        MILLISECOND("毫秒");

        /** Display name. */
        fun getName(): String = displayName
    }
}
