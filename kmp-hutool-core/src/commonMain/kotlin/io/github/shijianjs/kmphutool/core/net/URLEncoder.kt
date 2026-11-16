package io.github.shijianjs.kmphutool.core.net

import io.github.shijianjs.kmphutool.core.codec.PercentCodec

/**
 * URL encoder presets kept for API compatibility with `cn.hutool.core.net.URLEncoder`.
 *
 * Upstream marks the class deprecated in favour of [RFC3986]; the port keeps the same presets but
 * has no `Charset` parameter (UTF-8 only). New code should use [PercentCodec] directly.
 *
 * @hutool cn.hutool.core.net.URLEncoder
 */
@Deprecated("Use PercentCodec / RFC3986 presets instead")
class URLEncoder private constructor(private val codec: PercentCodec) {
    /** Marks [c] as safe (not encoded). */
    fun addSafeCharacter(c: Char) {
        codec.addSafe(c)
    }

    /** Removes [c] from the safe set. */
    fun removeSafeCharacter(c: Char) {
        codec.removeSafe(c)
    }

    /** When `true`, a space is encoded as `+` instead of `%20`. */
    fun setEncodeSpaceAsPlus(encodeSpaceAsPlus: Boolean) {
        codec.setEncodeSpaceAsPlus(encodeSpaceAsPlus)
    }

    /** Percent-encodes [path]. */
    fun encode(path: String?): String? = codec.encode(path)

    companion object {
        /** Path encoder: `pchar / "/"`. */
        val DEFAULT: URLEncoder = URLEncoder(
            PercentCodec.of("-._~").apply {
                addSubDelims(this)
                addSafe(':')
                addSafe('@')
                addSafe('/')
            },
        )

        /** A single non-empty path segment (no colon). */
        val PATH_SEGMENT: URLEncoder = URLEncoder(
            PercentCodec.of("-._~").apply {
                addSubDelims(this)
                addSafe('@')
            },
        )

        /** Fragment encoder: `pchar / "/" / "?"`. */
        val FRAGMENT: URLEncoder = URLEncoder(
            PercentCodec.of("-._~").apply {
                addSubDelims(this)
                addSafe(':')
                addSafe('@')
                addSafe('/')
                addSafe('?')
            },
        )

        /** Query encoder: spaces become `+`, `=` and `&` stay literal. */
        val QUERY: URLEncoder = URLEncoder(
            PercentCodec.of("*-._=&").setEncodeSpaceAsPlus(true),
        )

        /** Everything except `*`, `-`, `.`, `_` and alphanumerics is encoded. */
        val ALL: URLEncoder = URLEncoder(PercentCodec.of("*-._"))

        private fun addSubDelims(codec: PercentCodec) {
            codec.addSafe('!')
            codec.addSafe('$')
            codec.addSafe('&')
            codec.addSafe('\'')
            codec.addSafe('(')
            codec.addSafe(')')
            codec.addSafe('*')
            codec.addSafe('+')
            codec.addSafe(',')
            codec.addSafe(';')
            codec.addSafe('=')
        }
    }
}
