package io.github.shijianjs.kmphutool.core.date

import kotlin.time.TimeMark
import kotlin.time.TimeSource

/**
 * A simple stopwatch that times named tasks one after another, the Hutool `StopWatch`.
 *
 * Port of `cn.hutool.core.date.StopWatch`. Hutool wraps Apache Commons Lang's stopwatch, whose
 * times are `java.util.concurrent.TimeUnit`; the port uses [kotlin.time.Duration] backed by
 * [TimeSource.Monotonic], so it works on every target and never goes backwards when the wall clock
 * changes. `getTotal(TimeUnit)` is therefore replaced by [totalNanos]/[totalMillis]/[totalSeconds].
 *
 * @hutool cn.hutool.core.date.StopWatch
 */
class StopWatch(private val id: String = "") {
    private var startMark: TimeMark? = null
    private var runningTaskName: String? = null
    private var totalNanos: Long = 0
    private var lastTask: TaskInfo? = null
    private val taskList = mutableListOf<TaskInfo>()

    /** The stopwatch id, empty when not given. */
    fun getId(): String = id

    /** `true` between [start] and [stop]. */
    fun isRunning(): Boolean = startMark != null

    /** The name of the running task, `null` when the stopwatch is idle. */
    fun currentTaskName(): String? = runningTaskName

    /** Starts a task; throws [IllegalStateException] when one is already running. */
    fun start() {
        start("")
    }

    /** Starts the task named [taskName]. */
    fun start(taskName: String) {
        check(startMark == null) { "Can't start StopWatch: it's already running" }
        startMark = TimeSource.Monotonic.markNow()
        runningTaskName = taskName
    }

    /** Stops the running task and records its duration. */
    fun stop() {
        val mark = checkNotNull(startMark) { "Can't stop StopWatch: it's not running" }
        val elapsed = mark.elapsedNow().inWholeNanoseconds
        totalNanos += elapsed
        val name = runningTaskName ?: ""
        lastTask = TaskInfo(name, elapsed)
        taskList.add(TaskInfo(name, elapsed))
        startMark = null
        runningTaskName = null
    }

    /** `true` when at least one task has been timed. */
    fun hasTasks(): Boolean = taskList.isNotEmpty()

    /** The name of the last finished task, or `null`. */
    fun getLastTaskName(): String? = lastTask?.taskName

    /** The duration of the last finished task in milliseconds. */
    fun getLastTaskTimeMillis(): Long = lastTask?.timeNanos?.div(1_000_000) ?: 0

    /** The duration of the last finished task in nanoseconds. */
    fun getLastTaskTimeNanos(): Long = lastTask?.timeNanos ?: 0

    /** The last finished task, or `null`. */
    fun getLastTaskInfo(): TaskInfo? = lastTask

    /** Total time of every finished task, in nanoseconds. */
    fun totalNanos(): Long = totalNanos

    /** Total time of every finished task, in milliseconds. */
    fun totalMillis(): Long = totalNanos / 1_000_000

    /** Total time of every finished task, in seconds. */
    fun totalSeconds(): Double = totalNanos.toDouble() / 1_000_000_000.0

    /** The number of finished tasks. */
    fun getTaskCount(): Int = taskList.size

    /** Every finished task in order. */
    fun getTaskInfo(): List<TaskInfo> = taskList.toList()

    /** `StopWatch '<id>': running time = <ms> ms`. */
    fun shortSummary(): String = "StopWatch '$id': running time = ${totalMillis()} ms"

    /** One line per task plus the total, each in milliseconds. */
    fun prettyPrint(): String {
        val builder = StringBuilder(shortSummary())
        builder.append('\n')
        for (task in taskList) {
            builder.append("ms     ").append(task.timeNanos / 1_000_000).append('\t').append(task.taskName).append('\n')
        }
        builder.append("ms     ").append(totalMillis()).append("\ttotal\n")
        return builder.toString()
    }

    /** Resets every recorded task and the total. */
    fun reset() {
        startMark = null
        runningTaskName = null
        lastTask = null
        totalNanos = 0
        taskList.clear()
    }

    override fun toString(): String = shortSummary()

    /** One timed task. */
    data class TaskInfo(
        /** The task name, possibly empty. */
        val taskName: String,
        /** The task duration in nanoseconds. */
        val timeNanos: Long,
    ) {
        /** The task duration in milliseconds. */
        val timeMillis: Long
            get() = timeNanos / 1_000_000

        /** The task duration in seconds. */
        val timeSeconds: Double
            get() = timeNanos.toDouble() / 1_000_000_000.0
    }

    companion object {
        /** A stopwatch with [id]. */
        fun create(id: String = ""): StopWatch = StopWatch(id)
    }
}
