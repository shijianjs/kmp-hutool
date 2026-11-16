package io.github.shijianjs.kmphutool.core.net.url

import io.github.shijianjs.kmphutool.core.codec.PercentCodec
import io.github.shijianjs.kmphutool.core.net.FormUrlencoded
import io.github.shijianjs.kmphutool.core.net.RFC3986
import io.github.shijianjs.kmphutool.core.net.URLDecoder

/**
 * The query-string portion of a URL: parsing `key1=v1&key2=&key3` and rebuilding it with encoding.
 *
 * Port of `cn.hutool.core.net.url.UrlQuery`. `TableMap` (a JVM-only multi-value map) and `Charset` do
 * not exist in `commonMain`; the entries are kept as an ordered list of key/value pairs, which preserves
 * duplicate keys, and all encoding is UTF-8. `Convert`/`MapUtil` helpers are inlined.
 *
 * @hutool cn.hutool.core.net.url.UrlQuery
 */
class UrlQuery {
    private val query = ArrayList<Pair<CharSequence, CharSequence?>>()
    private val isFormUrlEncoded: Boolean
    private var strict: Boolean = false

    /** An empty query, standard encoding. */
    constructor() : this(null, false)

    /** An empty query, form-urlencoded when [isFormUrlEncoded]. */
    constructor(isFormUrlEncoded: Boolean) : this(null, isFormUrlEncoded)

    /** A query seeded from [queryMap]. */
    constructor(queryMap: Map<out CharSequence, *>?) : this(queryMap, false)

    /** A query seeded from [queryMap]. */
    constructor(queryMap: Map<out CharSequence, *>?, isFormUrlEncoded: Boolean) {
        this.isFormUrlEncoded = isFormUrlEncoded
        if (!queryMap.isNullOrEmpty()) addAll(queryMap)
    }

    /** In strict mode both names and values are limited to unreserved characters. */
    fun setStrict(strict: Boolean): UrlQuery {
        this.strict = strict
        return this
    }

    /** Adds one pair; collections become comma-separated values. */
    fun add(key: CharSequence, value: Any?): UrlQuery {
        query.add(key to toStr(value))
        return this
    }

    /** Adds every entry of [queryMap]. */
    fun addAll(queryMap: Map<out CharSequence, *>?): UrlQuery {
        queryMap?.forEach { (key, value) -> add(key, value) }
        return this
    }

    /** Removes every pair with [key]. */
    fun remove(key: CharSequence): UrlQuery {
        query.removeAll { it.first == key }
        return this
    }

    /** Parses a query string, dropping the path before `?` by default. */
    fun parse(queryStr: String?): UrlQuery = parse(queryStr, autoRemovePath = true)

    /** Parses a query string; when [autoRemovePath], everything before the first `?` is dropped. */
    fun parse(queryStr: String?, autoRemovePath: Boolean): UrlQuery {
        var text = queryStr ?: return this
        if (text.isBlank()) return this
        if (autoRemovePath) {
            val pathEnd = text.indexOf('?')
            if (pathEnd > -1) {
                text = text.substring(pathEnd + 1)
                if (text.isBlank()) return this
            } else if (text.startsWith("http://") || text.startsWith("https://")) {
                return this
            }
        }
        return doParse(text)
    }

    /** A read-only view of the decoded pairs. */
    fun getQueryMap(): Map<CharSequence, CharSequence?> = query.associate { it.first to it.second }

    /** The first value for [key], or null. */
    fun get(key: CharSequence): CharSequence? = query.firstOrNull { it.first == key }?.second

    /** Builds the query string with the configured encoding. */
    fun build(): String = build(encodePercent = true)

    /** Builds the query string, optionally leaving `%` unencoded. */
    fun build(encodePercent: Boolean): String = when {
        isFormUrlEncoded -> build(FormUrlencoded.ALL, FormUrlencoded.ALL, encodePercent)
        strict -> build(RFC3986.QUERY_PARAM_NAME_STRICT, RFC3986.QUERY_PARAM_VALUE_STRICT, encodePercent)
        else -> build(RFC3986.QUERY_PARAM_NAME, RFC3986.QUERY_PARAM_VALUE, encodePercent)
    }

    /** Builds the query string with explicit coders. */
    fun build(keyCoder: PercentCodec, valueCoder: PercentCodec): String = build(keyCoder, valueCoder, true)

    /** Builds the query string with explicit coders. */
    fun build(keyCoder: PercentCodec, valueCoder: PercentCodec, encodePercent: Boolean): String {
        if (query.isEmpty()) return ""
        val safe = if (encodePercent) charArrayOf() else charArrayOf('%')
        val builder = StringBuilder()
        for ((name, value) in query) {
            if (builder.isNotEmpty()) builder.append('&')
            builder.append(keyCoder.encode(name, *safe))
            if (value != null) builder.append('=').append(valueCoder.encode(value, *safe))
        }
        return builder.toString()
    }

    override fun toString(): String = build()

    private fun doParse(queryStr: String): UrlQuery {
        var name: String? = null
        var pos = 0
        var index = 0
        val length = queryStr.length
        while (index < length) {
            when (queryStr[index]) {
                '=' -> if (name == null) {
                    name = queryStr.substring(pos, index)
                    pos = index + 1
                }
                '&' -> {
                    addParam(name, queryStr.substring(pos, index))
                    name = null
                    if (index + 5 <= length && queryStr.startsWith("amp;", index + 1)) {
                        index += 4
                    }
                    pos = index + 1
                }
            }
            index++
        }
        addParam(name, queryStr.substring(pos))
        return this
    }

    private fun addParam(key: String?, value: String) {
        if (key != null) {
            query.add(URLDecoder.decode(key, isFormUrlEncoded)!! to (URLDecoder.decode(value, isFormUrlEncoded) ?: ""))
        } else if (value.isNotEmpty()) {
            query.add(URLDecoder.decode(value, isFormUrlEncoded)!! to null)
        }
    }

    companion object {
        /** A query seeded from [queryMap]. */
        fun of(queryMap: Map<out CharSequence, *>): UrlQuery = UrlQuery(queryMap)

        /** A query seeded from [queryMap]. */
        fun of(queryMap: Map<out CharSequence, *>, isFormUrlEncoded: Boolean): UrlQuery = UrlQuery(queryMap, isFormUrlEncoded)

        /** Parses [queryStr]. */
        fun of(queryStr: String?): UrlQuery = UrlQuery().parse(queryStr)

        /** Parses [queryStr]. */
        fun of(queryStr: String?, autoRemovePath: Boolean): UrlQuery = UrlQuery().parse(queryStr, autoRemovePath)

        /** Parses [queryStr] as form-urlencoded when [isFormUrlEncoded]. */
        fun of(queryStr: String?, autoRemovePath: Boolean, isFormUrlEncoded: Boolean): UrlQuery =
            UrlQuery(isFormUrlEncoded).parse(queryStr, autoRemovePath)

        @Suppress("UNCHECKED_CAST")
        private fun toStr(value: Any?): String? = when (value) {
            null -> null
            is Iterable<*> -> value.joinToString(",") { it.toString() }
            is Iterator<*> -> buildList {
                while (value.hasNext()) add(value.next().toString())
            }.joinToString(",")
            else -> value.toString()
        }
    }
}
