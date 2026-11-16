package io.github.shijianjs.kmphutool.core

import io.github.shijianjs.kmphutool.core.net.url.UrlPath
import io.github.shijianjs.kmphutool.core.net.url.UrlQuery
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class UrlPartsTest {
    @Test
    fun buildsPathsWithEncodingAndTrailingSlash() {
        assertEquals("/a/b/", UrlPath.of("/a/b/").build())
        assertEquals("/a/b", UrlPath.of("/a/b").build())
        assertEquals("/a%20b", UrlPath.of("/a%20b").build())
        val path = UrlPath.of("a/b")
        path.addBefore("x")
        path.add("c")
        assertEquals(listOf("x", "a", "b", "c"), path.getSegments())
        assertEquals("/x/a/b/c", path.build())
        assertEquals("c", path.getSegment(3))
        assertNull(path.getSegment(4))
        assertEquals("/", UrlPath.of("/").build())
        assertEquals("", UrlPath.of("").build())
    }

    @Test
    fun parsesAndRebuildsQueries() {
        val query = UrlQuery.of("http://example.com/path?a=1&b=2&b=3")
        assertEquals("1", query.get("a"))
        assertEquals("a=1&b=2&b=3", query.build())
        assertEquals("a=1&b=2&b=3", query.toString())

        query.remove("b")
        assertEquals("a=1", query.build())
        query.add("c", listOf(1, 2))
        assertEquals("a=1&c=1,2", query.build())
    }

    @Test
    fun distinguishesFormAndStrictEncoding() {
        assertEquals("q=a+b", UrlQuery(true).add("q", "a b").build())
        assertEquals("q=a%20b", UrlQuery().add("q", "a b").build())
        assertEquals("q=a%20b", UrlQuery().setStrict(true).add("q", "a b").build())
        assertEquals("", UrlQuery().build())
    }

    @Test
    fun parsesPercentEncodedNamesAndValues() {
        val query = UrlQuery.of("a%20b=c%20d")
        assertEquals("c d", query.get("a b"))
        assertEquals("a%20b=c%20d", query.build())
    }
}
