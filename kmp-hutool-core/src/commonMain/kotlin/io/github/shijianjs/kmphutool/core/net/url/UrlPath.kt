package io.github.shijianjs.kmphutool.core.net.url

import io.github.shijianjs.kmphutool.core.net.RFC3986
import io.github.shijianjs.kmphutool.core.net.URLDecoder

/**
 * The path portion of a URL, as decoded segments that can be rebuilt with percent-encoding.
 *
 * Port of `cn.hutool.core.net.url.UrlPath`. The Java overloads take a `Charset`; `commonMain` text is
 * UTF-8, so it disappears here and [URLDecoder]/[RFC3986] always work on UTF-8 bytes.
 *
 * @hutool cn.hutool.core.net.url.UrlPath
 */
class UrlPath private constructor() {
    private var segments: MutableList<String>? = null
    private var withEndTag = false

    /** Whether [build] appends a trailing `/`. */
    fun setWithEndTag(withEndTag: Boolean): UrlPath {
        this.withEndTag = withEndTag
        return this
    }

    /** The decoded segments, never null. */
    fun getSegments(): List<String> = segments ?: emptyList()

    /** The segment at [index], or null when out of range. */
    fun getSegment(index: Int): String? = segments?.getOrNull(index)

    /** Appends a segment. */
    fun add(segment: CharSequence): UrlPath {
        addInternal(fixPath(segment), before = false)
        return this
    }

    /** Prepends a segment. */
    fun addBefore(segment: CharSequence): UrlPath {
        addInternal(fixPath(segment), before = true)
        return this
    }

    /** Parses `aaa/bb/ccc` or `/aaa/bbb/ccc`; a trailing slash is preserved in [build]. */
    fun parse(path: CharSequence): UrlPath {
        if (path.isNotEmpty()) {
            if (path.last() == '/') withEndTag = true
            val fixed = fixPath(path)
            if (fixed.isNotEmpty()) {
                for (seg in fixed.split('/')) {
                    addInternal(URLDecoder.decodeForPath(seg) ?: "", before = false)
                }
            }
        }
        return this
    }

    /** Builds the path with a leading `/`; returns `""` when empty and no trailing slash is requested. */
    fun build(): String = build(encodePercent = true)

    /** Builds the path, optionally leaving `%` unencoded. */
    fun build(encodePercent: Boolean): String {
        val segments = this.segments
        if (segments.isNullOrEmpty()) return if (withEndTag) "/" else ""
        val safe = if (encodePercent) charArrayOf() else charArrayOf('%')
        val builder = StringBuilder()
        for (segment in segments) {
            builder.append('/').append(RFC3986.SEGMENT.encode(segment, *safe))
        }
        if (withEndTag && !builder.endsWith('/')) builder.append('/')
        return builder.toString()
    }

    override fun toString(): String = build()

    private fun addInternal(segment: String, before: Boolean) {
        val list = segments ?: mutableListOf<String>().also { segments = it }
        if (before) list.add(0, segment) else list.add(segment)
    }

    companion object {
        /** Parses [pathStr] into a [UrlPath]. */
        fun of(pathStr: CharSequence): UrlPath = UrlPath().parse(pathStr)

        private fun fixPath(path: CharSequence): String {
            if (path == "/") return ""
            var segment = path.toString().trim()
            segment = segment.removePrefix("/").removeSuffix("/").trim()
            return segment
        }
    }
}


