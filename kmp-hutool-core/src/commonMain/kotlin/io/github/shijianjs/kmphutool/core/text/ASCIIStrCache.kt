package io.github.shijianjs.kmphutool.core.text

/**
 * Caches the one-character strings of the ASCII range, so `char`-to-`String` conversions in string
 * building do not allocate.
 *
 * Port of `cn.hutool.core.text.ASCIIStrCache`. The cache is filled on first use; non-ASCII characters
 * fall through to `Char.toString()`.
 *
 * @hutool cn.hutool.core.text.ASCIIStrCache
 */
object ASCIIStrCache {
    private const val ASCII_LENGTH = 128

    private val CACHE: Array<String> = Array(ASCII_LENGTH) { it.toChar().toString() }

    /** [c] as a string, from the cache when it is ASCII. */
    fun toString(c: Char): String = if (c.code < ASCII_LENGTH) CACHE[c.code] else c.toString()
}
