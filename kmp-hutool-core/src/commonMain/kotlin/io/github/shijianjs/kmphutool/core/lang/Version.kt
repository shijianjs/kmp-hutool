package io.github.shijianjs.kmphutool.core.lang

import io.github.shijianjs.kmphutool.core.lang.Assert

/**
 * Parses a version string into comparable tokens.
 *
 * Port of `cn.hutool.core.lang.Version` (from `java.lang.module.ModuleDescriptor.Version`). Numeric
 * tokens compare numerically, string tokens lexicographically; a version without a pre-release part
 * sorts *after* one with it, so `1.0` > `1.0-rc1`.
 *
 * @hutool cn.hutool.core.lang.Version
 */
class Version(v: String) : Comparable<Version> {
    private val version: String = v
    private val sequence = ArrayList<Any>(4)
    private val pre = ArrayList<Any>(2)
    private val build = ArrayList<Any>(2)

    init {
        Assert.notNull(v, "Null version string")
        parse(v)
    }

    private fun parse(v: String) {
        val n = v.length
        if (n != 0) {
            var i = 0
            var c = v[0]
            i = takeNumber(v, i, sequence)
            while (i < n) {
                c = v[i]
                if (c == '.') {
                    i++
                    continue
                }
                if (c == '-' || c == '+') {
                    i++
                    break
                }
                i = if (c.isDigit()) takeNumber(v, i, sequence) else takeString(v, i, sequence)
            }
            if (c == '-' && i >= n) return

            // Pre-release part.
            while (i < n) {
                c = v[i]
                i = if (c in '0'..'9') takeNumber(v, i, pre) else takeString(v, i, pre)
                if (i >= n) break
                c = v[i]
                if (c == '.' || c == '-') {
                    i++
                    continue
                }
                if (c == '+') {
                    i++
                    break
                }
            }
            if (c == '+' && i >= n) return

            // Build metadata.
            while (i < n) {
                c = v[i]
                i = if (c in '0'..'9') takeNumber(v, i, build) else takeString(v, i, build)
                if (i >= n) break
                c = v[i]
                if (c == '.' || c == '-' || c == '+') i++
            }
        }
    }

    override fun compareTo(other: Version): Int {
        val seq = compareTokens(sequence, other.sequence)
        if (seq != 0) return seq
        if (pre.isEmpty()) {
            if (other.pre.isNotEmpty()) return 1
        } else if (other.pre.isEmpty()) {
            return -1
        }
        val preResult = compareTokens(pre, other.pre)
        if (preResult != 0) return preResult
        return compareTokens(build, other.build)
    }

    override fun equals(other: Any?): Boolean = other is Version && compareTo(other) == 0

    override fun hashCode(): Int = version.hashCode()

    override fun toString(): String = version

    private fun compareTokens(left: List<Any>, right: List<Any>): Int {
        val n = minOf(left.size, right.size)
        for (i in 0 until n) {
            val a = left[i]
            val b = right[i]
            val comparison = when {
                a is Int && b is Int -> a.compareTo(b)
                a is String && b is String -> a.compareTo(b)
                // Mixed types compare in string form.
                else -> a.toString().compareTo(b.toString())
            }
            if (comparison != 0) return comparison
        }
        val rest = if (left.size > right.size) left else right
        for (i in n until rest.size) {
            val token = rest[i]
            if (token is Int && token == 0) continue
            return left.size - right.size
        }
        return 0
    }

    companion object {
        /** Parses [v] into a [Version]. */
        fun of(v: String): Version = Version(v)

        private fun takeNumber(s: String, start: Int, acc: MutableList<Any>): Int {
            var i = start
            var d = s[i] - '0'
            val n = s.length
            while (++i < n) {
                val c = s[i]
                if (c in '0'..'9') {
                    d = d * 10 + (c - '0')
                    continue
                }
                break
            }
            acc.add(d)
            return i
        }

        private fun takeString(s: String, start: Int, acc: MutableList<Any>): Int {
            val b = start
            var i = start
            val n = s.length
            while (++i < n) {
                val c = s[i]
                if (c != '.' && c != '-' && c != '+' && c !in '0'..'9') continue
                break
            }
            acc.add(s.substring(b, i))
            return i
        }
    }
}
