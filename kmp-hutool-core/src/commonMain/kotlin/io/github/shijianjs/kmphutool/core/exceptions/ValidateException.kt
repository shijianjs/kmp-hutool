package io.github.shijianjs.kmphutool.core.exceptions

/**
 * Thrown by [io.github.shijianjs.kmphutool.core.lang.Assert] and
 * [io.github.shijianjs.kmphutool.core.lang.Validator] when a check fails.
 *
 * Hutool throws `IllegalArgumentException` from `Assert`; the port keeps this type because callers
 * switch on it, and it extends [IllegalArgumentException] so code that catches the JDK type still
 * works.
 *
 * @hutool cn.hutool.core.exceptions.ValidateException
 */
open class ValidateException : IllegalArgumentException {
    constructor() : super()

    constructor(message: String?) : super(message)

    constructor(message: String?, cause: Throwable?) : super(message, cause)

    constructor(cause: Throwable?) : super(cause)
}
