package io.github.shijianjs.kmphutool.core.codec

import io.github.shijianjs.kmphutool.core.exceptions.UtilException

/**
 * Punycode (RFC 3492): converts internationalized domain labels to their `xn--` ASCII form.
 *
 * Port of `cn.hutool.core.codec.PunyCode`; the algorithm is kept as-is because it is a bit-level
 * standard, with Kotlin `Char` arithmetic and explicit range checks replacing `Assert.checkBetween`.
 *
 * @hutool cn.hutool.core.codec.PunyCode
 */
object PunyCode {
    private const val TMIN = 1
    private const val TMAX = 26
    private const val BASE = 36
    private const val INITIAL_N = 128
    private const val INITIAL_BIAS = 72
    private const val DAMP = 700
    private const val SKEW = 38
    private const val DELIMITER = '-'

    const val PUNY_CODE_PREFIX: String = "xn--"

    /** Encodes every label of [domain]. */
    fun encodeDomain(domain: String): String =
        domain.split('.').joinToString(".") { encode(it, withPrefix = true) }

    /** Encodes [input] without the `xn--` prefix. */
    fun encode(input: CharSequence): String = encode(input, withPrefix = false)

    /** Encodes [input], optionally prefixing `xn--`. */
    fun encode(input: CharSequence, withPrefix: Boolean): String {
        var n = INITIAL_N
        var delta = 0
        var bias = INITIAL_BIAS
        val output = StringBuilder()
        val length = input.length
        var b = 0
        for (index in 0 until length) {
            val c = input[index]
            if (isBasic(c)) {
                output.append(c)
                b++
            }
        }
        if (b > 0) {
            if (b == length) return output.toString()
            output.append(DELIMITER)
        }
        var h = b
        while (h < length) {
            var m = Int.MAX_VALUE
            for (index in 0 until length) {
                val c = input[index].code
                if (c >= n && c < m) m = c
            }
            if (m - n > (Int.MAX_VALUE - delta) / (h + 1)) throw UtilException("OVERFLOW")
            delta += (m - n) * (h + 1)
            n = m
            for (index in 0 until length) {
                val c = input[index].code
                if (c < n) {
                    delta++
                    if (delta == 0) throw UtilException("OVERFLOW")
                }
                if (c == n) {
                    var q = delta
                    var k = BASE
                    while (true) {
                        val t = when {
                            k <= bias -> TMIN
                            k >= bias + TMAX -> TMAX
                            else -> k - bias
                        }
                        if (q < t) break
                        output.append(digit2codepoint(t + (q - t) % (BASE - t)).toChar())
                        q = (q - t) / (BASE - t)
                        k += BASE
                    }
                    output.append(digit2codepoint(q).toChar())
                    bias = adapt(delta, h + 1, h == b)
                    delta = 0
                    h++
                }
            }
            delta++
            n++
        }
        if (withPrefix) output.insert(0, PUNY_CODE_PREFIX)
        return output.toString()
    }

    /** Decodes every label of [domain], leaving non-`xn--` labels untouched. */
    fun decodeDomain(domain: String): String =
        domain.split('.').joinToString(".") { if (it.startsWith(PUNY_CODE_PREFIX, ignoreCase = true)) decode(it) else it }

    /** Decodes a Punycode label, with or without the `xn--` prefix. */
    fun decode(input: String): String {
        var text = input.removePrefixIgnoreCase(PUNY_CODE_PREFIX)
        var n = INITIAL_N
        var i = 0
        var bias = INITIAL_BIAS
        val output = StringBuilder()
        var d = text.lastIndexOf(DELIMITER)
        if (d > 0) {
            for (j in 0 until d) {
                val c = text[j]
                if (isBasic(c)) output.append(c)
            }
            d++
        } else {
            d = 0
        }
        val length = text.length
        while (d < length) {
            val oldi = i
            var w = 1
            var k = BASE
            while (true) {
                if (d == length) throw UtilException("BAD_INPUT")
                val digit = codepoint2digit(text[d++].code)
                if (digit > (Int.MAX_VALUE - i) / w) throw UtilException("OVERFLOW")
                i += digit * w
                val t = when {
                    k <= bias -> TMIN
                    k >= bias + TMAX -> TMAX
                    else -> k - bias
                }
                if (digit < t) break
                w *= BASE - t
                k += BASE
            }
            bias = adapt(i - oldi, output.length + 1, oldi == 0)
            if (i / (output.length + 1) > Int.MAX_VALUE - n) throw UtilException("OVERFLOW")
            n += i / (output.length + 1)
            i %= output.length + 1
            output.insert(i, n.toChar())
            i++
        }
        return output.toString()
    }

    private fun adapt(delta: Int, numpoints: Int, first: Boolean): Int {
        var value = if (first) delta / DAMP else delta / 2
        value += value / numpoints
        var k = 0
        while (value > (BASE - TMIN) * TMAX / 2) {
            value /= BASE - TMIN
            k += BASE
        }
        return k + (BASE - TMIN + 1) * value / (value + SKEW)
    }

    private fun isBasic(c: Char): Boolean = c.code < 0x80

    private fun digit2codepoint(d: Int): Int {
        require(d in 0..35) { "BAD_INPUT" }
        return when {
            d < 26 -> d + 'a'.code
            d < 36 -> d - 26 + '0'.code
            else -> throw UtilException("BAD_INPUT")
        }
    }

    private fun codepoint2digit(c: Int): Int = when {
        c - '0'.code < 10 -> c - '0'.code + 26
        c - 'a'.code < 26 -> c - 'a'.code
        else -> throw UtilException("BAD_INPUT")
    }

    private fun String.removePrefixIgnoreCase(prefix: String): String =
        if (startsWith(prefix, ignoreCase = true)) substring(prefix.length) else this
}
