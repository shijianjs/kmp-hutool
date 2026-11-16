package io.github.shijianjs.kmphutool.core.io

/**
 * Runtime wrapper for IO failures.
 *
 * Port of `cn.hutool.core.io.IORuntimeException`. `causeInstanceOf(Class)` is not ported because
 * commonMain has no runtime class checks; use `cause is SomeException`.
 *
 * @hutool cn.hutool.core.io.IORuntimeException
 */
class IORuntimeException : RuntimeException {
    constructor(e: Throwable) : super(e.message, e)

    constructor(message: String) : super(message)

    constructor(message: String, throwable: Throwable) : super(message, throwable)
}
