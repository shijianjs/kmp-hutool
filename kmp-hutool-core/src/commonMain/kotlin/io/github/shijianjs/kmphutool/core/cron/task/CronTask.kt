package io.github.shijianjs.kmphutool.core.cron.task

import io.github.shijianjs.kmphutool.core.cron.pattern.CronPattern

/**
 * A task together with its id and schedule.
 *
 * Port of `cn.hutool.cron.task.CronTask`.
 *
 * @hutool cn.hutool.cron.task.CronTask
 */
class CronTask(
    private val id: String,
    pattern: CronPattern,
    private val task: Task,
) : Task {
    var pattern: CronPattern = pattern
        private set

    override fun execute() = task.execute()

    fun getId(): String = id

    fun setPattern(pattern: CronPattern): CronTask = apply { this.pattern = pattern }

    /** The original task without the cron metadata. */
    fun getRaw(): Task = task
}
