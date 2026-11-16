package io.github.shijianjs.kmphutool.core.cron.task

/**
 * A unit of work run by the scheduler.
 *
 * Port of `cn.hutool.cron.task.Task`. Coroutine users can wrap their suspend function in an
 * implementation that launches it on their scope.
 *
 * @hutool cn.hutool.cron.task.Task
 */
fun interface Task {
    fun execute()
}
