package io.github.shijianjs.kmphutool.core.cron

import io.github.shijianjs.kmphutool.core.util.StrUtil

/**
 * Raised when a cron expression is invalid or a task cannot run.
 *
 * Port of `cn.hutool.cron.CronException`.
 *
 * @hutool cn.hutool.cron.CronException
 */
open class CronException : RuntimeException {
    constructor(message: String?) : super(message)
    constructor(message: String?, cause: Throwable?) : super(message, cause)
    constructor(cause: Throwable?) : super(cause?.message, cause)
    constructor(messageTemplate: CharSequence, vararg params: Any?) : super(StrUtil.format(messageTemplate, *params))
}
