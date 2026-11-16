package io.github.shijianjs.kmphutool.core.io.unit

/**
 * Data size unit: bytes, kilobytes, megabytes, gigabytes and terabytes.
 *
 * Port of `cn.hutool.core.io.unit.DataUnit` (itself from Spring). The suffix lookup also accepts
 * IEC spellings such as `KiB`, `MiB` and `GiB`.
 *
 * @hutool cn.hutool.core.io.unit.DataUnit
 */
enum class DataUnit(private val suffixValue: String, internal val size: DataSize) {
    BYTES("B", DataSize.ofBytes(1)),
    KILOBYTES("KB", DataSize.ofKilobytes(1)),
    MEGABYTES("MB", DataSize.ofMegabytes(1)),
    GIGABYTES("GB", DataSize.ofGigabytes(1)),
    TERABYTES("TB", DataSize.ofTerabytes(1));

    /** The unit's suffix, e.g. `KB`. */
    fun getSuffix(): String = suffixValue

    internal fun size(): DataSize = size

    companion object {
        /** Suffixes ordered from smallest to largest, including units without a [DataUnit] constant. */
        val UNIT_NAMES: Array<String> = arrayOf("B", "KB", "MB", "GB", "TB", "PB", "EB")

        /** Resolves a suffix such as `KB`, `M`, `m` or `GiB`. */
        fun fromSuffix(suffix: String): DataUnit {
            var text = suffix
            if (text.length == 3 && text[1].equals('i', ignoreCase = true)) {
                text = "${text[0]}${text[2]}"
            }
            for (candidate in entries) {
                if (candidate.suffixValue.startsWith(text, ignoreCase = true)) return candidate
            }
            throw IllegalArgumentException("Unknown data unit suffix '$suffix'")
        }
    }
}

