package io.github.shijianjs.kmphutool.core.exceptions

/**
 * Runtime wrapper for any failure inside a utility function.
 *
 * Hutool throws [UtilException] where a checked exception cannot be declared, which is the same role
 * a plain [RuntimeException] plays in Kotlin; the type stays so that `catch (e: UtilException)`
 * keeps working across the port.
 *
 * Port of `cn.hutool.core.exceptions.UtilException`.
 *
 * @hutool cn.hutool.core.exceptions.UtilException
 */
open class UtilException : RuntimeException {
    constructor() : super()

    constructor(message: String?) : super(message)

    constructor(message: String?, cause: Throwable?) : super(message, cause)

    constructor(cause: Throwable?) : super(cause)
}
