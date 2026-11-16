package io.github.shijianjs.kmphutool.core

import io.github.shijianjs.kmphutool.core.text.AntPathMatcher
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class AntPathMatcherTest {
    private val matcher = AntPathMatcher()

    @Test
    fun matchesWildcards() {
        assertTrue(matcher.match("com/t?st.jsp", "com/test.jsp"))
        assertTrue(matcher.match("com/*.jsp", "com/test.jsp"))
        assertFalse(matcher.match("com/*.jsp", "com/a/test.jsp"))
        assertTrue(matcher.match("com/**/test.jsp", "com/a/b/test.jsp"))
        assertTrue(matcher.match("com/**/test.jsp", "com/test.jsp"))
        assertTrue(matcher.match("org/**/servlet/bla.jsp", "org/servlet/bla.jsp"))
    }

    @Test
    fun detectsPatternsAndPrefixes() {
        assertTrue(matcher.isPattern("a/*/b"))
        assertFalse(matcher.isPattern("a/b"))
        assertTrue(matcher.matchStart("/a/**", "/a/b/c"))
        assertFalse(matcher.match("/a/**", "/x/a/b"))
    }

    @Test
    fun capturesVariables() {
        assertEquals(mapOf("filename" to "test"), matcher.extractUriTemplateVariables("com/{filename:\\w+}.jsp", "com/test.jsp"))
    }

    @Test
    fun honoursCaseSensitivity() {
        assertFalse(AntPathMatcher().match("/Foo", "/foo"))
        assertTrue(AntPathMatcher().setCaseSensitive(false).match("/Foo", "/foo"))
    }
}
