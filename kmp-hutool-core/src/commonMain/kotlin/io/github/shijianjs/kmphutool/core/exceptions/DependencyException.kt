package io.github.shijianjs.kmphutool.core.exceptions

import io.github.shijianjs.kmphutool.core.util.StrUtil

/**
 * A required dependency is missing or unusable.
 *
 * Port of `cn.hutool.core.exceptions.DependencyException`. The `enableSuppression`/`writableStackTrace`
 * constructor is omitted: Kotlin common code cannot control those `Throwable` flags.
 *
 * @hutool cn.hutool.core.exceptions.DependencyException
 */
class DependencyException : RuntimeException {
    constructor(e: Throwable) : super(e.message, e)

    constructor(message: String) : super(message)

    constructor(messageTemplate: String, vararg params: Any?) : super(StrUtil.format(messageTemplate, *params))

    constructor(message: String, throwable: Throwable?) : super(message, throwable)

    constructor(throwable: Throwable, messageTemplate: String, vararg params: Any?) :
        super(StrUtil.format(messageTemplate, *params), throwable)
}
