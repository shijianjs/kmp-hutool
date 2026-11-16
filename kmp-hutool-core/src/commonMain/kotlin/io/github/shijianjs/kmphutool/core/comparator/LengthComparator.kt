package io.github.shijianjs.kmphutool.core.comparator

/**
 * Sorts by length first (shorter first), then lexicographically for equal lengths.
 *
 * Port of `cn.hutool.core.comparator.LengthComparator`.
 *
 * @hutool cn.hutool.core.comparator.LengthComparator
 */
object LengthComparator : Comparator<CharSequence> {
    override fun compare(a: CharSequence, b: CharSequence): Int {
        val byLength = a.length.compareTo(b.length)
        if (byLength != 0) return byLength
        return CompareUtil.compare(a.toString(), b.toString())
    }
}
