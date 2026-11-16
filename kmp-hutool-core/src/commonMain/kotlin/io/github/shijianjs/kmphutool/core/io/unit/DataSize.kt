package io.github.shijianjs.kmphutool.core.io.unit

/**
 * A byte count that can be built from or converted to KB/MB/GB/TB and parsed from text like `12MB`.
 *
 * Port of `cn.hutool.core.io.unit.DataSize`. `BigDecimal` is not available in commonMain, so the
 * fractional factory takes a [Double] and truncates to bytes the same way `BigDecimal.longValue()`
 * does.
 *
 * @hutool cn.hutool.core.io.unit.DataSize
 */
data class DataSize private constructor(private val bytes: Long) : Comparable<DataSize> {
    /** Whether the size is negative. */
    fun isNegative(): Boolean = bytes < 0

    /** Size in bytes. */
    fun toBytes(): Long = bytes

    /** Size in whole kilobytes (truncating). */
    fun toKilobytes(): Long = bytes / BYTES_PER_KB

    /** Size in whole megabytes (truncating). */
    fun toMegabytes(): Long = bytes / BYTES_PER_MB

    /** Size in whole gigabytes (truncating). */
    fun toGigabytes(): Long = bytes / BYTES_PER_GB

    /** Size in whole terabytes (truncating). */
    fun toTerabytes(): Long = bytes / BYTES_PER_TB

    override fun compareTo(other: DataSize): Int = bytes.compareTo(other.bytes)

    override fun toString(): String = "${bytes}B"

    companion object {
        private const val BYTES_PER_KB = 1024L
        private const val BYTES_PER_MB = BYTES_PER_KB * 1024
        private const val BYTES_PER_GB = BYTES_PER_MB * 1024
        private const val BYTES_PER_TB = BYTES_PER_GB * 1024
        private val PATTERN = Regex("^([+-]?\\d+(\\.\\d+)?)([a-zA-Z]{0,3})$")

        /** A size of [bytes] bytes. */
        fun ofBytes(bytes: Long): DataSize = DataSize(bytes)

        /** A size of [kilobytes] KB. */
        fun ofKilobytes(kilobytes: Long): DataSize = DataSize(kilobytes * BYTES_PER_KB)

        /** A size of [megabytes] MB. */
        fun ofMegabytes(megabytes: Long): DataSize = DataSize(megabytes * BYTES_PER_MB)

        /** A size of [gigabytes] GB. */
        fun ofGigabytes(gigabytes: Long): DataSize = DataSize(gigabytes * BYTES_PER_GB)

        /** A size of [terabytes] TB. */
        fun ofTerabytes(terabytes: Long): DataSize = DataSize(terabytes * BYTES_PER_TB)

        /** [amount] of [unit]; a `null` unit means bytes. */
        fun of(amount: Long, unit: DataUnit?): DataSize = DataSize(amount * (unit ?: DataUnit.BYTES).size().toBytes())

        /** [amount] of [unit], truncating fractional bytes; a `null` unit means bytes. */
        fun of(amount: Double, unit: DataUnit?): DataSize =
            DataSize((amount * (unit ?: DataUnit.BYTES).size().toBytes()).toLong())

        /** Parses text such as `12KB`, `5MB` or `20` (bytes). */
        fun parse(text: CharSequence): DataSize = parse(text, null)

        /** Parses text, using [defaultUnit] when it has no suffix. */
        fun parse(text: CharSequence, defaultUnit: DataUnit?): DataSize {
            val cleaned = text.toString().filterNot { it.isWhitespace() || it == '\u3000' }
            val match = PATTERN.find(cleaned)
                ?: throw IllegalArgumentException("'$text' is not a valid data size")
            val suffix = match.groups[3]!!.value
            val unit = if (suffix.isNotEmpty()) DataUnit.fromSuffix(suffix) else (defaultUnit ?: DataUnit.BYTES)
            return of(match.groups[1]!!.value.toDouble(), unit)
        }
    }
}
