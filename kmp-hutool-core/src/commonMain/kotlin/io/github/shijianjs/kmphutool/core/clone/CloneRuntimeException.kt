package io.github.shijianjs.kmphutool.core.clone

/**
 * Thrown when a clone cannot be produced.
 *
 * Port of `cn.hutool.core.clone.CloneRuntimeException`, which wraps the checked
 * `CloneNotSupportedException` of the JVM.
 *
 * @hutool cn.hutool.core.clone.CloneRuntimeException
 */
class CloneRuntimeException : RuntimeException {
    /** A message-only exception. */
    constructor(message: String) : super(message)

    /** A message with the original cause. */
    constructor(message: String, cause: Throwable) : super(message, cause)
}
