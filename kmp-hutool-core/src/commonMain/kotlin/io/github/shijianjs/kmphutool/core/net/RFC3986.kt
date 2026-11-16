package io.github.shijianjs.kmphutool.core.net

import io.github.shijianjs.kmphutool.core.codec.PercentCodec

/**
 * RFC 3986 percent-encoding presets.
 *
 * Port of `cn.hutool.core.net.RFC3986`. Each constant is a [PercentCodec] whose safe set matches the
 * grammar production named by the constant.
 *
 * @hutool cn.hutool.core.net.RFC3986
 */
object RFC3986 {
    /** `gen-delims = ":" / "/" / "?" / "#" / "[" / "]" / "@"`. */
    val GEN_DELIMS: PercentCodec = PercentCodec.of(":/?#[]@")

    /** `sub-delims = "!" / "$" / "&" / "'" / "(" / ")" / "*" / "+" / "," / ";" / "="`. */
    val SUB_DELIMS: PercentCodec = PercentCodec.of("!\$&'()*+,;=")

    /** `reserved = gen-delims / sub-delims`. */
    val RESERVED: PercentCodec = GEN_DELIMS.orNew(SUB_DELIMS)

    /** `unreserved = ALPHA / DIGIT / "-" / "." / "_" / "~"`. */
    val UNRESERVED: PercentCodec = PercentCodec.of(unreservedChars())

    /** `pchar = unreserved / pct-encoded / sub-delims / ":" / "@"`. */
    val PCHAR: PercentCodec = UNRESERVED.orNew(SUB_DELIMS).or(PercentCodec.of(":@"))

    /** `segment = pchar`. */
    val SEGMENT: PercentCodec = PCHAR

    /** `segment-nz-nc`: a segment without a colon. */
    val SEGMENT_NZ_NC: PercentCodec = PercentCodec.of(SEGMENT).removeSafe(':')

    /** `path = segment / "/"`. */
    val PATH: PercentCodec = SEGMENT.orNew(PercentCodec.of("/"))

    /** `query = pchar / "/" / "?"`. */
    val QUERY: PercentCodec = PCHAR.orNew(PercentCodec.of("/?"))

    /** `fragment = pchar / "/" / "?"`. */
    val FRAGMENT: PercentCodec = QUERY

    /** A query value: `&` is not allowed, `=` is. */
    val QUERY_PARAM_VALUE: PercentCodec = PercentCodec.of(QUERY).removeSafe('&')

    /** Strict query value encoder: unreserved only. */
    val QUERY_PARAM_VALUE_STRICT: PercentCodec = UNRESERVED

    /** A query key: neither `&` nor `=` is allowed. */
    val QUERY_PARAM_NAME: PercentCodec = PercentCodec.of(QUERY_PARAM_VALUE).removeSafe('=')

    /** Strict query key encoder: unreserved only. */
    val QUERY_PARAM_NAME_STRICT: PercentCodec = UNRESERVED

    private fun unreservedChars(): String = buildString {
        for (c in 'A'..'Z') append(c)
        for (c in 'a'..'z') append(c)
        for (c in '0'..'9') append(c)
        append("_.-~")
    }
}
