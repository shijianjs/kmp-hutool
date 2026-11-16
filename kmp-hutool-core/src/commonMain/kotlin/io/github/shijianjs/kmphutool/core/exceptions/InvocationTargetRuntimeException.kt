package io.github.shijianjs.kmphutool.core.exceptions

import io.github.shijianjs.kmphutool.core.util.StrUtil

/**
 * Runtime wrapper around an invocation failure.
 *
 * Port of `cn.hutool.core.exceptions.InvocationTargetRuntimeException`; there is no
 * `java.lang.reflect.InvocationTargetException` in common code, but the type is kept so callers that
 * catch it keep compiling.
 *
 * @hutool cn.hutool.core.exceptions.InvocationTargetRuntimeException
 */
class InvocationTargetRuntimeException : UtilException {
    constructor(e: Throwable) : super(e)

    constructor(message: String) : super(message)

    constructor(messageTemplate: String, vararg params: Any?) : super(StrUtil.format(messageTemplate, *params))

    constructor(message: String, throwable: Throwable?) : super(message, throwable)

    constructor(throwable: Throwable, messageTemplate: String, vararg params: Any?) :
        super(StrUtil.format(messageTemplate, *params), throwable)
}
