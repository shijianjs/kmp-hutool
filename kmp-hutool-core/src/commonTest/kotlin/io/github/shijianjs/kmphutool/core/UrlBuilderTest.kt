package io.github.shijianjs.kmphutool.core

import io.github.shijianjs.kmphutool.core.net.url.UrlBuilder
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class UrlBuilderTest {
    @Test
    fun parsesAndRendersFullUrl() {
        val url = UrlBuilder.of("https://example.com:8080/a/b?x=1&y=2#frag")
        assertEquals("https", url.getScheme())
        assertEquals("example.com", url.getHost())
        assertEquals(8080, url.getPort())
        assertEquals("/a/b", url.getPathStr())
        assertEquals("frag", url.getFragment())
        assertEquals("https://example.com:8080/a/b?x=1&y=2#frag", url.build())
    }

    @Test
    fun assemblesFromParts() {
        val url = UrlBuilder.of("http", "localhost", -1, "/api", "q=a b")
        assertEquals(80, url.getPortWithDefault())
        assertEquals("localhost", url.getAuthority())
        assertEquals("http://localhost/api?q=a%20b", url.build())

        url.setPort(9090).setFragment("#top").addQuery("k", "v")
        assertEquals("http://localhost:9090/api?q=a%20b&k=v#top", url.build())
    }

    @Test
    fun defaultsToHttpAndAddsSegments() {
        val url = UrlBuilder.ofHttp("example.com/start")
        assertEquals("http", url.getSchemeWithDefault())
        assertEquals("/start", url.getPathStr())
        url.addPath("a/b").setWithEndTag(true)
        assertEquals("/start/a/b/", url.getPathStr())
        assertEquals("http://example.com/start/a/b/", url.build())
        url.setQuery(null)
        assertNull(url.getQueryStr())
    }
}
