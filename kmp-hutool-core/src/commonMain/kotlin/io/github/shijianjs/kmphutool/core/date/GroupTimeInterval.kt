package io.github.shijianjs.kmphutool.core.date

import kotlin.time.ExperimentalTime
import kotlin.time.TimeSource

/**
 * A stopwatch with named groups.
 *
 * Port of `cn.hutool.core.date.GroupTimeInterval`. Wall-clock timing uses
 * [kotlin.time.Clock], nanosecond timing a monotonic [TimeSource]; the Java
 * `System.nanoTime()` equivalent is the same monotonic source.
 *
 * @hutool cn.hutool.core.date.GroupTimeInterval
 */
@OptIn(ExperimentalTime::class)
open class GroupTimeInterval(private val isNano: Boolean) {
    /** The recorded start times, keyed by group id. */
    protected val groupMap: MutableMap<String, Long> = mutableMapOf()

    private val nanoMark = TimeSource.Monotonic.markNow()

    /** Removes all recorded groups. */
    fun clear(): GroupTimeInterval {
        groupMap.clear()
        return this
    }

    /** Starts a group and returns the current time. */
    fun start(id: String): Long {
        val time = getTime()
        groupMap[id] = time
        return time
    }

    /** Records a new start and returns the elapsed time since the previous one. */
    fun intervalRestart(id: String): Long {
        val now = getTime()
        val previous = groupMap.put(id, now)
        return now - (previous ?: now)
    }

    /** Elapsed time in the timer's base unit (milliseconds or nanoseconds). */
    fun interval(id: String): Long {
        val last = groupMap[id] ?: return 0
        return getTime() - last
    }

    /** Elapsed time converted to [dateUnit]; nanoseconds are first reduced to milliseconds. */
    fun interval(id: String, dateUnit: DateUnit): Long {
        val intervalMs = if (isNano) interval(id) / 1_000_000L else interval(id)
        if (dateUnit == DateUnit.MS) return intervalMs
        return intervalMs / dateUnit.getMillis()
    }

    /** Elapsed milliseconds. */
    fun intervalMs(id: String): Long = interval(id, DateUnit.MS)

    /** Elapsed seconds. */
    fun intervalSecond(id: String): Long = interval(id, DateUnit.SECOND)

    /** Elapsed minutes. */
    fun intervalMinute(id: String): Long = interval(id, DateUnit.MINUTE)

    /** Elapsed hours. */
    fun intervalHour(id: String): Long = interval(id, DateUnit.HOUR)

    /** Elapsed days. */
    fun intervalDay(id: String): Long = interval(id, DateUnit.DAY)

    /** Elapsed weeks. */
    fun intervalWeek(id: String): Long = interval(id, DateUnit.WEEK)

    /** Elapsed time rendered as `XX天XX小时XX分XX秒`. */
    fun intervalPretty(id: String): String =
        BetweenFormatter(intervalMs(id), BetweenFormatter.Level.SECOND).format()

    private fun getTime(): Long =
        if (isNano) nanoMark.elapsedNow().inWholeNanoseconds else kotlin.time.Clock.System.now().toEpochMilliseconds()
}

/**
 * A single stopwatch.
 *
 * Port of `cn.hutool.core.date.TimeInterval`.
 *
 * @hutool cn.hutool.core.date.TimeInterval
 */
@OptIn(ExperimentalTime::class)
class TimeInterval(isNano: Boolean = false) : GroupTimeInterval(isNano) {
    private companion object {
        const val DEFAULT_ID = ""
    }

    init {
        start()
    }

    /** Starts (or restarts) the stopwatch and returns the current time. */
    fun start(): Long = start(DEFAULT_ID)

    /** Restarts the stopwatch and returns the previous elapsed time. */
    fun intervalRestart(): Long = intervalRestart(DEFAULT_ID)

    /** Restarts the stopwatch. */
    fun restart(): TimeInterval {
        start(DEFAULT_ID)
        return this
    }

    /** Elapsed time in the timer's base unit. */
    fun interval(): Long = interval(DEFAULT_ID)

    /** Elapsed time rendered as `XX天XX小时XX分XX秒`. */
    fun intervalPretty(): String = intervalPretty(DEFAULT_ID)

    fun intervalMs(): Long = intervalMs(DEFAULT_ID)

    fun intervalSecond(): Long = intervalSecond(DEFAULT_ID)

    fun intervalMinute(): Long = intervalMinute(DEFAULT_ID)

    fun intervalHour(): Long = intervalHour(DEFAULT_ID)

    fun intervalDay(): Long = intervalDay(DEFAULT_ID)

    fun intervalWeek(): Long = intervalWeek(DEFAULT_ID)
}
