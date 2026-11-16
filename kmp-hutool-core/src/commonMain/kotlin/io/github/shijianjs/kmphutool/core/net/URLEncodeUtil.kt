package io.github.shijianjs.kmphutool.core.net

/**
 * URL encoding helpers, UTF-8 only (no `Charset` parameter in `commonMain`).
 *
 * Port of `cn.hutool.core.net.URLEncodeUtil`; upstream deprecates it in favour of [RFC3986] presets.
 *
 * @hutool cn.hutool.core.net.URLEncodeUtil
 */
object URLEncodeUtil {
    /** Encodes everything outside the unreserved set. */
    fun encodeAll(url: String?): String? = RFC3986.UNRESERVED.encode(url)

    /** Encodes a path: `/` survives, reserved delimiters do not. */
    fun encode(url: String?): String? = RFC3986.PATH.encode(url)

    /** Encodes a query string. */
    fun encodeQuery(url: String?): String? = RFC3986.QUERY.encode(url)

    /** Encodes one path segment (a colon is encoded). */
    fun encodePathSegment(url: String?): String? = RFC3986.SEGMENT.encode(url)

    /** Encodes a fragment. */
    fun encodeFragment(url: String?): String? = RFC3986.FRAGMENT.encode(url)
}
