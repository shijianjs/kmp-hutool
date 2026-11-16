package io.github.shijianjs.kmphutool.core.convert

import io.github.shijianjs.kmphutool.core.util.StrUtil

/**
 * Thrown when a value cannot be converted.
 *
 * Port of `cn.hutool.core.convert.ConvertException`.
 *
 * @hutool cn.hutool.core.convert.ConvertException
 */
class ConvertException : RuntimeException {
    constructor(cause: Throwable) : super(cause.message ?: cause.toString(), cause)

    constructor(message: String) : super(message)

    constructor(messageTemplate: String, vararg params: Any?) : super(StrUtil.format(messageTemplate, *params))

    constructor(message: String, cause: Throwable) : super(message, cause)

    constructor(cause: Throwable, messageTemplate: String, vararg params: Any?) :
        super(StrUtil.format(messageTemplate, *params), cause)
}
