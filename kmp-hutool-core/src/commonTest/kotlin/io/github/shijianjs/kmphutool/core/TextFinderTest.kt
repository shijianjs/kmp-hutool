package io.github.shijianjs.kmphutool.core

import io.github.shijianjs.kmphutool.core.text.NamingCase
import io.github.shijianjs.kmphutool.core.text.finder.CharFinder
import io.github.shijianjs.kmphutool.core.text.finder.CharMatcherFinder
import io.github.shijianjs.kmphutool.core.text.finder.LengthFinder
import io.github.shijianjs.kmphutool.core.text.finder.PatternFinder
import io.github.shijianjs.kmphutool.core.text.finder.StrFinder
import io.github.shijianjs.kmphutool.core.text.replacer.LookupReplacer
import io.github.shijianjs.kmphutool.core.text.replacer.ReplacerChain
import kotlin.test.Test
import kotlin.test.assertEquals

class TextFinderTest {
    @Test
    fun namingCaseConversions() {
        assertEquals("hello_world", NamingCase.toUnderlineCase("HelloWorld"))
        assertEquals("hello_world", NamingCase.toUnderlineCase("Hello_World"))
        assertEquals("hello_world_test", NamingCase.toUnderlineCase("HelloWorld_test"))
        assertEquals("a_BC", NamingCase.toUnderlineCase("aBC"))
        assertEquals("A_bcc", NamingCase.toUnderlineCase("ABcc"))
        assertEquals("hello-world", NamingCase.toKebabCase("HelloWorld"))
        assertEquals("helloWorld", NamingCase.toCamelCase("hello_world"))
        assertEquals("HelloWorld", NamingCase.toPascalCase("hello_world"))
        assertEquals("helloWorld", NamingCase.toCamelCase("hello-world", '-'))
    }

    @Test
    fun findersLocateMatches() {
        val text = "abcABC"
        val ci = CharFinder('B', caseInsensitive = true).setText(text)
        assertEquals(1, ci.start(0))
        assertEquals(2, ci.end(1))

        val str = StrFinder("bc", caseInsensitive = false).setText(text)
        assertEquals(1, str.start(0))
        assertEquals(3, str.end(1))
        assertEquals(-1, str.start(3))

        val length = LengthFinder(3).setText("abcdefgh")
        assertEquals(5, length.start(2))
        assertEquals(5, length.end(5))

        val digits = CharMatcherFinder { it.isDigit() }.setText("ab123")
        assertEquals(2, digits.start(0))
        assertEquals(3, digits.end(2))

        val pattern = PatternFinder("\\d+").setText("ab123cd")
        assertEquals(2, pattern.start(0))
        assertEquals(5, pattern.end(2))
    }

    @Test
    fun lookupReplacementPrefersLongestKey() {
        val replacer = LookupReplacer(
            arrayOf("ab", "X"),
            arrayOf("abcd", "Y"),
        )
        assertEquals("YX", replacer.replace("abcdab").toString())
    }

    @Test
    fun replacerChainStopsAtFirstMatch() {
        val chain = ReplacerChain(
            LookupReplacer(arrayOf("ab", "X")),
            LookupReplacer(arrayOf("cd", "Z")),
        )
        assertEquals("XZ!", chain.replace("abcd!").toString())
    }
}
