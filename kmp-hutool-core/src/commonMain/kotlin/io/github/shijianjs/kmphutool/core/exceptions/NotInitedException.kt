package io.github.shijianjs.kmphutool.core.exceptions

import io.github.shijianjs.kmphutool.core.util.StrUtil

/**
 * Signals that a component was used before initialization.
 *
 * Port of `cn.hutool.core.exceptions.NotInitedException`; the suppression/stack-trace constructor is
 * omitted because Kotlin common `Throwable` does not expose those flags.
 *
 * @hutool cn.hutool.core.exceptions.NotInitedException
 */
class NotInitedException : RuntimeException {
    constructor(e: Throwable) : super(e)

    constructor(message: String) : super(message)

    constructor(messageTemplate: String, vararg params: Any?) : super(StrUtil.format(messageTemplate, *params))

    constructor(message: String, throwable: Throwable?) : super(message, throwable)

    constructor(throwable: Throwable, messageTemplate: String, vararg params: Any?) :
        super(StrUtil.format(messageTemplate, *params), throwable)
}
