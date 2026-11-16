package io.github.shijianjs.kmphutool.core.net.url

import io.github.shijianjs.kmphutool.core.net.RFC3986

/**
 * Builds a URL from scheme, host, port, path, query and fragment, each settable independently.
 *
 * Port of `cn.hutool.core.net.url.UrlBuilder`. `java.net.URL`/`URI` and `Charset` are JVM-only, so this
 * port parses and renders strings directly (always UTF-8), and `toURL`/`toURI` are not provided —
 * [build] returns the same text those methods would have produced.
 *
 * @hutool cn.hutool.core.net.url.UrlBuilder
 */
class UrlBuilder {
    private var scheme: String? = null
    private var host: String? = null
    private var port: Int = -1
    private var path: UrlPath? = null
    private var query: UrlQuery? = null
    private var fragment: String? = null

    /** The configured scheme, possibly null. */
    fun getScheme(): String? = scheme

    /** The scheme, defaulting to `http` when unset. */
    fun getSchemeWithDefault(): String = scheme?.takeIf { it.isNotEmpty() } ?: DEFAULT_SCHEME

    fun setScheme(scheme: String?): UrlBuilder {
        this.scheme = scheme
        return this
    }

    /** The configured host, possibly null. */
    fun getHost(): String? = host

    fun setHost(host: String?): UrlBuilder {
        this.host = host
        return this
    }

    /** The configured port, `-1` when unset. */
    fun getPort(): Int = port

    /** The port, falling back to the scheme default. */
    fun getPortWithDefault(): Int {
        if (port > 0) return port
        return if (getSchemeWithDefault().equals("https", ignoreCase = true)) 443 else 80
    }

    fun setPort(port: Int): UrlBuilder {
        this.port = port
        return this
    }

    /** `host[:port]`. */
    fun getAuthority(): String = if (port < 0) host.orEmpty() else "${host.orEmpty()}:$port"

    /** The [UrlPath], possibly null. */
    fun getPath(): UrlPath? = path

    /** The rendered path, `/` when none was set. */
    fun getPathStr(): String = path?.build() ?: "/"

    /** Makes [build] append a trailing `/` to the path. */
    fun setWithEndTag(withEndTag: Boolean): UrlBuilder {
        val path = this.path ?: UrlPath.of("").also { this.path = it }
        path.setWithEndTag(withEndTag)
        return this
    }

    fun setPath(path: UrlPath?): UrlBuilder {
        this.path = path
        return this
    }

    /** Appends every segment of [path] to the current path. */
    fun addPath(path: CharSequence): UrlBuilder {
        UrlPath.of(path).getSegments().forEach { addPathSegment(it) }
        return this
    }

    /** Appends one segment. */
    fun addPathSegment(segment: CharSequence): UrlBuilder {
        if (segment.isEmpty()) return this
        val path = this.path ?: UrlPath.of("").also { this.path = it }
        path.add(segment)
        return this
    }

    /** The [UrlQuery], possibly null. */
    fun getQuery(): UrlQuery? = query

    /** The rendered query, or null when no query was set. */
    fun getQueryStr(): String? = query?.build()

    fun setQuery(query: UrlQuery?): UrlBuilder {
        this.query = query
        return this
    }

    /** Adds one query pair, keeping duplicate keys. */
    fun addQuery(key: String?, value: Any?): UrlBuilder {
        if (key.isNullOrEmpty()) return this
        val query = this.query ?: UrlQuery().also { this.query = it }
        query.add(key, value)
        return this
    }

    /** The fragment without its leading `#`. */
    fun getFragment(): String? = fragment

    /** The percent-encoded fragment. */
    fun getFragmentEncoded(): String = RFC3986.FRAGMENT.encode(fragment ?: "")

    fun setFragment(fragment: String?): UrlBuilder {
        this.fragment = fragment?.removePrefix("#")?.takeIf { it.isNotEmpty() }
        return this
    }

    /** Renders the URL as `scheme://host[:port]/path[?query][#fragment]`. */
    fun build(): String {
        val builder = StringBuilder()
        builder.append(getSchemeWithDefault()).append("://").append(getAuthority())
        builder.append(getPathStr())
        val query = getQueryStr()
        if (!query.isNullOrBlank()) builder.append('?').append(query)
        if (!fragment.isNullOrBlank()) builder.append('#').append(getFragmentEncoded())
        return builder.toString()
    }

    override fun toString(): String = build()

    companion object {
        private const val DEFAULT_SCHEME = "http"

        private val URL_PATTERN = Regex("^([a-zA-Z][a-zA-Z0-9+.-]*):(?://([^/?#:]*)(?::(\\d+))?)?([^?#]*)(?:\\?([^#]*))?(?:#(.*))?$")

        /** An empty builder. */
        fun of(): UrlBuilder = UrlBuilder()

        /** A builder from the individual parts. */
        fun of(
            scheme: String?,
            host: String?,
            port: Int,
            path: CharSequence?,
            query: String?,
            fragment: String? = null,
        ): UrlBuilder = UrlBuilder().apply {
            setScheme(scheme)
            setHost(host)
            setPort(port)
            path?.let { setPath(UrlPath.of(it)) }
            query?.let { setQuery(UrlQuery().parse(it, autoRemovePath = false)) }
            setFragment(fragment)
        }

        /** Parses [url], keeping any percent-escapes intact. */
        fun of(url: String): UrlBuilder {
            require(url.isNotBlank()) { "Url must be not blank!" }
            val text = url.trim()
            val match = URL_PATTERN.matchEntire(text)
                ?: return UrlBuilder().apply { setHost(text) }
            return UrlBuilder().apply {
                setScheme(match.groupValues[1])
                setHost(match.groupValues[2].ifEmpty { null })
                setPort(match.groupValues[3].takeIf { it.isNotEmpty() }?.toInt() ?: -1)
                setPath(UrlPath.of(match.groupValues[4]))
                val query = match.groupValues[5]
                if (query.isNotEmpty()) setQuery(UrlQuery().parse(query, autoRemovePath = false))
                setFragment(match.groupValues[6].ifEmpty { null })
            }
        }

        /** Parses [httpUrl], prepending `http://` when it has no scheme. */
        fun ofHttp(httpUrl: String): UrlBuilder {
            require(httpUrl.isNotBlank()) { "Http url must be not blank!" }
            val trimmed = httpUrl.trimStart()
            val hasScheme = trimmed.startsWith("http://", ignoreCase = true) ||
                trimmed.startsWith("https://", ignoreCase = true)
            return of(if (hasScheme) trimmed else "http://$trimmed")
        }
    }
}

