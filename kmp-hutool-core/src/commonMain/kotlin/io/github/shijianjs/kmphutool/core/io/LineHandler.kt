package io.github.shijianjs.kmphutool.core.io

/**
 * Receives one line at a time.
 *
 * Port of `cn.hutool.core.io.LineHandler`.
 *
 * @hutool cn.hutool.core.io.LineHandler
 */
fun interface LineHandler {
    /** Handles one line, possibly edited before it is stored elsewhere. */
    fun handle(line: String)
}
