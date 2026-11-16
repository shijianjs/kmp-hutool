package io.github.shijianjs.kmphutool.core.comparator

/**
 * Compares version strings such as `1.0.1`, `1.0.1-SNAPSHOT`, `2.0.0-rc1`.
 *
 * Port of `cn.hutool.core.comparator.VersionComparator`. Behaviour kept from upstream:
 *
 * * a version is split into runs of digits and runs of letters, and every other character acts as a
 *   separator;
 * * digits are compared as numbers, and a version that ends earlier is compared as if it continued
 *   with zeros (`1.0` equals `1.0.0`);
 * * letters are compared case-insensitively, and a letter run sorts *before* the empty run, so
 *   `1.0-SNAPSHOT` is lower than `1.0`;
 * * a `null` version sorts below everything.
 *
 * @hutool cn.hutool.core.comparator.VersionComparator
 */
class VersionComparator : Comparator<String?> {
    override fun compare(v1: String?, v2: String?): Int {
        if (v1 === v2) return 0
        if (v1 == null) return -1
        if (v2 == null) return 1
        val parts1 = split(v1)
        val parts2 = split(v2)
        val size = maxOf(parts1.size, parts2.size)
        for (index in 0 until size) {
            val left = parts1.getOrNull(index) ?: ZERO
            val right = parts2.getOrNull(index) ?: ZERO
            val result = comparePart(left, right)
            if (result != 0) return result
        }
        return 0
    }

    private fun comparePart(left: String, right: String): Int {
        if (left == right) return 0
        val leftNumeric = left.all { it.isDigit() }
        val rightNumeric = right.all { it.isDigit() }
        if (leftNumeric && rightNumeric) {
            val a = left.trimStart('0').ifEmpty { "0" }
            val b = right.trimStart('0').ifEmpty { "0" }
            return when {
                a.length != b.length -> a.length - b.length
                else -> a.compareTo(b)
            }
        }
        if (leftNumeric) return 1
        if (rightNumeric) return -1
        // A letter run sorts before the empty run ("1.0-SNAPSHOT" < "1.0"), then alphabetically.
        if (left.isEmpty()) return 1
        if (right.isEmpty()) return -1
        return left.lowercase().compareTo(right.lowercase())
    }

    private fun split(version: String): List<String> {
        val parts = mutableListOf<String>()
        var index = 0
        while (index < version.length) {
            if (!version[index].isLetterOrDigit()) {
                index++
                continue
            }
            val letterRun = version[index].isLetter()
            val start = index
            while (index < version.length && version[index].isLetterOrDigit() && version[index].isLetter() == letterRun) {
                index++
            }
            parts.add(version.substring(start, index))
        }
        return parts
    }

    companion object {
        private const val ZERO = "0"

        /** A shared instance, since the comparator is stateless. */
        val INSTANCE: VersionComparator = VersionComparator()
    }
}
