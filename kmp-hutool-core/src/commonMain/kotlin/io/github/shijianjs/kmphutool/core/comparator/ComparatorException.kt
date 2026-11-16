package io.github.shijianjs.kmphutool.core.comparator

/**
 * Thrown when a comparison cannot be completed (for example an incomparable pair).
 *
 * Port of `cn.hutool.core.comparator.ComparatorException`.
 *
 * @hutool cn.hutool.core.comparator.ComparatorException
 */
class ComparatorException : RuntimeException {
    /** A message-only exception. */
    constructor(message: String) : super(message)

    /** A message with the original cause. */
    constructor(message: String, cause: Throwable) : super(message, cause)
}
