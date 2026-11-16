package io.github.shijianjs.kmphutool.core.io

/**
 * Progress callbacks for a stream copy.
 *
 * Port of `cn.hutool.core.io.StreamProgress`. A `total` of `-1` or [Long.MAX_VALUE] means the
 * total size is unknown.
 *
 * @hutool cn.hutool.core.io.StreamProgress
 */
interface StreamProgress {
    /** Copy started. */
    fun start()

    /** Copy advanced to [progressSize] of [total]. */
    fun progress(total: Long, progressSize: Long)

    /** Copy finished. */
    fun finish()
}
