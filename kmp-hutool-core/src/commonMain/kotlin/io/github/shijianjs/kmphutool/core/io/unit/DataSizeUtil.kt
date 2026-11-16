package io.github.shijianjs.kmphutool.core.io.unit

import kotlin.math.log10
import kotlin.math.min
import kotlin.math.pow
import kotlin.math.roundToLong

/**
 * Parses and humanises data sizes.
 *
 * Port of `cn.hutool.core.io.unit.DataSizeUtil`. `java.text.DecimalFormat` is JVM-only, so the
 * formatting is hand-written: at most two decimals, trailing zeros removed, and thousands separators
 * in [format].
 *
 * @hutool cn.hutool.core.io.unit.DataSizeUtil
 */
object DataSizeUtil {
    /** Parses a size such as `12KB` and returns its byte count. */
    fun parse(text: String): Long = DataSize.parse(text).toBytes()

    /** Formats [size] into the largest unit that keeps the value >= 1, e.g. `1.5 KB`. */
    fun format(size: Long): String {
        if (size <= 0) return "0"
        val digitGroups = min(DataUnit.UNIT_NAMES.size - 1, (log10(size.toDouble()) / log10(1024.0)).toInt())
        return decimal(size / 1024.0.pow(digitGroups), grouping = true) + " " + DataUnit.UNIT_NAMES[digitGroups]
    }

    /** Formats [size] in [fileDataUnit], e.g. `1.5 MB`. */
    fun format(size: Long?, fileDataUnit: DataUnit): String {
        if (size == null || size <= 0) return "0"
        val groups = DataUnit.UNIT_NAMES.indexOf(fileDataUnit.getSuffix())
        return decimal(size / 1024.0.pow(groups), grouping = false) + " " + DataUnit.UNIT_NAMES[groups]
    }

    /** Rounds to two decimals, strips trailing zeros, and optionally inserts `,` every three digits. */
    private fun decimal(value: Double, grouping: Boolean): String {
        val scaled = (value * 100).roundToLong()
        val whole = scaled / 100
        val fraction = scaled % 100
        val wholeText = if (grouping) whole.toString().reversed().chunked(3).joinToString(",").reversed() else whole.toString()
        return when {
            fraction == 0L -> wholeText
            fraction % 10 == 0L -> "$wholeText.${fraction / 10}"
            else -> "$wholeText.${fraction.toString().padStart(2, '0')}"
        }
    }
}
