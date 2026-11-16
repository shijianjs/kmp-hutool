package io.github.shijianjs.kmphutool.core.date

import io.github.shijianjs.kmphutool.core.util.StrUtil

/**
 * Signals a date/time conversion or range problem.
 *
 * Port of `cn.hutool.core.date.DateException`. Message templates use the same `{}` placeholders as
 * [StrUtil.format].
 *
 * @hutool cn.hutool.core.date.DateException
 */
class DateException : RuntimeException {
    constructor(throwable: Throwable) : super(throwable.message, throwable)

    constructor(message: String) : super(message)

    constructor(messageTemplate: String, vararg params: Any?) : super(StrUtil.format(messageTemplate, *params))

    constructor(message: String, throwable: Throwable) : super(message, throwable)

    constructor(throwable: Throwable, messageTemplate: String, vararg params: Any?) :
        super(StrUtil.format(messageTemplate, *params), throwable)
}
