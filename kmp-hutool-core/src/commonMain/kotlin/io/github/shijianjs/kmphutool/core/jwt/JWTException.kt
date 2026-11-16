package io.github.shijianjs.kmphutool.core.jwt

import io.github.shijianjs.kmphutool.core.util.StrUtil

/**
 * Exception raised by the JWT helpers.
 *
 * Port of `cn.hutool.jwt.JWTException`.
 *
 * @hutool cn.hutool.jwt.JWTException
 */
open class JWTException : RuntimeException {
    constructor(message: String?) : super(message)
    constructor(message: String?, cause: Throwable?) : super(message, cause)
    constructor(cause: Throwable?) : super(cause?.message, cause)
    constructor(messageTemplate: CharSequence, vararg params: Any?) : super(StrUtil.format(messageTemplate, *params))
}
