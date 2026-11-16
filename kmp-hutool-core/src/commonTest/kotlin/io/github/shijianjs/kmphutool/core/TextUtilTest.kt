package io.github.shijianjs.kmphutool.core

import io.github.shijianjs.kmphutool.core.text.StrFormatter
import io.github.shijianjs.kmphutool.core.text.StrJoiner
import io.github.shijianjs.kmphutool.core.text.StrSplitter
import io.github.shijianjs.kmphutool.core.util.CharUtil
import io.github.shijianjs.kmphutool.core.util.StrUtil
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertTrue

class TextUtilTest {
    @Test
    fun charPredicates() {
        assertTrue(CharUtil.isAscii('a'))
        assertFalse(CharUtil.isAscii('中'))
        assertTrue(CharUtil.isAsciiPrintable(' '))
        assertFalse(CharUtil.isAsciiPrintable('\n'))
        assertTrue(CharUtil.isAsciiControl('\u0000'))
        assertTrue(CharUtil.isAsciiControl('\u007F'))
        assertTrue(CharUtil.isLetter('Q'))
        assertFalse(CharUtil.isLetter('1'))
        assertTrue(CharUtil.isNumber('7'))
        assertTrue(CharUtil.isHexChar('f'))
        assertFalse(CharUtil.isHexChar('g'))
        assertTrue(CharUtil.isBlankChar('\u3000'))
        assertTrue(CharUtil.isBlankChar('\uFEFF'))
        assertFalse(CharUtil.isBlankChar('x'))
        assertTrue(CharUtil.isFileSeparator('\\'))
        assertTrue(CharUtil.equals('a', 'A', caseInsensitive = true))
        assertFalse(CharUtil.equals('a', 'A', caseInsensitive = false))
    }

    @Test
    fun enclosedCharacters() {
        assertEquals('①', CharUtil.toCloseChar('1'))
        assertEquals('Ⓐ', CharUtil.toCloseChar('A'))
        assertEquals('ⓐ', CharUtil.toCloseChar('a'))
        assertEquals('#', CharUtil.toCloseChar('#'))
        assertEquals('⑳', CharUtil.toCloseByNumber(20))
    }

    @Test
    fun emptiness() {
        assertTrue(StrUtil.isEmpty(null))
        assertTrue(StrUtil.isEmpty(""))
        assertFalse(StrUtil.isEmpty(" "))
        assertTrue(StrUtil.isBlank(" \t\u3000"))
        assertFalse(StrUtil.isBlank(" a "))
        assertTrue(StrUtil.isAllBlank(null, "", " "))
        assertTrue(StrUtil.isAllNotBlank("a", "b"))
        assertTrue(StrUtil.hasBlank("a", null))
        assertTrue(StrUtil.isBlankIfStr(123))
        assertFalse(StrUtil.isBlankIfStr("x"))
    }

    @Test
    fun defaults() {
        assertNull(StrUtil.nullToDefault(null, null))
        assertEquals("d", StrUtil.blankToDefault(" ", "d"))
        assertEquals("v", StrUtil.blankToDefault("v", "d"))
        assertNull(StrUtil.emptyToNull(""))
        assertEquals("", StrUtil.emptyIfNull(null))
        assertEquals("first", StrUtil.firstNonBlank(null, "  ", "first"))
    }

    @Test
    fun trimmingAndCleaning() {
        assertEquals("abc", StrUtil.trim("  abc  "))
        assertNull(StrUtil.trim(null))
        assertEquals("abc", StrUtil.cleanBlank(" a\tb c\n"))
        assertEquals("a b c", StrUtil.strip("  a   b\tc  "))
        assertEquals("核心", StrUtil.stripAll("**核心**", "**"))
    }

    @Test
    fun lengthAndBytes() {
        assertEquals(0, StrUtil.length(null))
        assertEquals(4, StrUtil.length("abcd"))
        assertEquals(6, StrUtil.byteLength("中文"))
        assertEquals(2, StrUtil.utf8Bytes("ab").size)
        assertEquals(7, StrUtil.totalLength("ab", null, "cdefg"))
    }

    @Test
    fun substrings() {
        assertEquals("bcd", StrUtil.sub("abcdef", 1, 4))
        assertEquals("abcdef", StrUtil.sub("abcdef", 0, 100))
        assertEquals("", StrUtil.sub(null, 0, 3))
        assertEquals("ef", StrUtil.subSuf("abcdef", 2))
        assertEquals("ab", StrUtil.subPre("abcdef", 2))
        assertEquals("bcd", StrUtil.subWithLength("abcdef", 1, 3))
        assertEquals("ab", StrUtil.subBetween("[ab]", "[", "]"))
        assertEquals(listOf("a", "b"), StrUtil.subBetweenAll("{a}{b}", "{", "}"))
        assertEquals("a", StrUtil.subBefore("a.b.c", "."))
        assertEquals("b.c", StrUtil.subAfter("a.b.c", "."))
        assertEquals("a.b", StrUtil.subBefore("a.b.c", ".", isLastSeparator = true))
        assertEquals("c", StrUtil.subAfter("a.b.c", ".", isLastSeparator = true))
    }

    @Test
    fun splitting() {
        assertEquals(listOf("a", "b", "c"), StrUtil.split("a,b,c", ","))
        assertEquals(listOf("a b"), StrUtil.split("a b", ","))
        assertEquals(listOf("a", "b"), StrUtil.splitTrim(" a , b ", ","))
        assertEquals(2, StrUtil.split("a,b,c", ",", limit = 2).size)
        assertEquals(listOf("a", "b", "c"), StrUtil.split("a|b|c", '|'))
        assertEquals(listOf("a"), StrUtil.split("a", ","))
        assertEquals(listOf("a", "b", "c"), StrSplitter.splitByBlank(" a  b\tc "))
        assertEquals(listOf("ab", "cd", "ef"), StrSplitter.splitByLength("abcdef", 2).toList())
    }

    @Test
    fun joining() {
        assertEquals("a,b", StrUtil.join(listOf("a", "b"), ","))
        assertEquals("a-b-c", StrJoiner.of("-").append("a").append("b").append("c").toString())
        assertEquals("[1],[2]", StrJoiner.of(",", "[", "]").setWrapElement(true).append(1).append(2).toString())
        assertEquals("", StrJoiner.of(",").toString())
        assertEquals("empty", StrJoiner.of(",").setEmptyResult("empty").toString())
    }

    @Test
    fun formatting() {
        assertEquals("a=1 b=2", StrUtil.format("a={} b={}", 1, 2))
        assertEquals("no placeholders", StrUtil.format("no placeholders"))
        assertEquals("a=1", StrFormatter.format("a={}", 1))
        assertEquals("a=1 b=2", StrFormatter.formatWith("a={} b={}", "{}", 1, 2))
        assertEquals("name: 张三", StrFormatter.format("name: {name}", mapOf("name" to "张三")))
    }

    @Test
    fun prefixAndSuffix() {
        assertEquals("abc", StrUtil.removePrefix("abcabc", "abc"))
        assertEquals("ab", StrUtil.removeSuffix("abab", "ab"))
        assertEquals("Abc", StrUtil.removePrefixIgnoreCase("abcAbc", "abc"))
        assertEquals("prefix", StrUtil.addPrefixIfNot("prefix", "pre"))
        assertEquals("prevalue", StrUtil.addPrefixIfNot("value", "pre"))
        assertEquals("value!", StrUtil.addSuffixIfNot("value", "!"))
        assertEquals("a-b", StrUtil.removeAll("a-b-c", "-c"))
        assertEquals("ab", StrUtil.removeAllLineBreaks("a\r\nb"))
        assertEquals("[x]", StrUtil.wrap("x", "[", "]"))
        assertEquals("x", StrUtil.unWrap("[x]", "[", "]"))
        assertEquals("[x]", StrUtil.wrapIfMissing("[x]", "[", "]"))
        assertEquals("[x]", StrUtil.wrapAll("x", "[", "]"))
    }

    @Test
    fun caseConversion() {
        assertEquals("Abc", StrUtil.upperFirst("abc"))
        assertEquals("abc", StrUtil.lowerFirst("Abc"))
        assertEquals("someName", StrUtil.toCamelCase("some_name"))
        assertEquals("SomeName", StrUtil.toCamelCase("SOME_NAME", lowerCaseFirst = false))
        assertEquals("some_name", StrUtil.toUnderlineCase("someName"))
        assertEquals("ABC", StrUtil.toUpperCase("abc"))
        assertEquals("abc", StrUtil.toLowerCase("ABC"))
        assertEquals("AbC", StrUtil.swapCase("aBc"))
    }

    @Test
    fun comparisonAndSearch() {
        assertTrue(StrUtil.equals("a", "a"))
        assertFalse(StrUtil.equals("a", null))
        assertTrue(StrUtil.equalsIgnoreCase("a", "A"))
        assertTrue(StrUtil.equalsAny("b", "a", "b"))
        assertEquals(-1, StrUtil.compareVersion("1.9", "1.10"))
        assertEquals(0, StrUtil.compare("abc", "abc"))
        assertEquals(3, StrUtil.indexOf("abcdef", 'd'))
        assertEquals(3, StrUtil.indexOf("abcdef", "d"))
        assertEquals(3, StrUtil.indexOfIgnoreCase("ABCDEF", "d"))
        assertEquals(3, StrUtil.ordinalIndexOf("1.2.3.4", ".", 2))
        assertTrue(StrUtil.contains("abcdef", "cd"))
        assertTrue(StrUtil.containsAny("abcdef", "zz", "ef"))
        assertTrue(StrUtil.containsAll("abcdef", "ab", "ef"))
        assertTrue(StrUtil.containsIgnoreCase("ABCDEF", "de"))
        assertEquals(2, StrUtil.count("aaaa", "aa"))
        assertTrue(StrUtil.startWith("abc", "ab"))
        assertTrue(StrUtil.endWith("abc", "bc"))
        assertTrue(StrUtil.startWithAny("abc", "zz", "ab"))
        assertTrue(StrUtil.isWrap("[a]", "[", "]"))
    }

    @Test
    fun maskingAndPadding() {
        assertEquals("13*******35", StrUtil.desensitized("13812345635", 2, 2))
        assertEquals("abc", StrUtil.maxLength("abcdef", 3))
        assertEquals("abc...", StrUtil.brief("abcdefgh", 6))
        assertEquals("  x  ", StrUtil.center("x", 5))
        assertEquals("00x", StrUtil.padPre("x", 3, '0'))
        assertEquals("x00", StrUtil.padAfter("x", 3, '0'))
        assertEquals("x000", StrUtil.fixLength("x", 4, '0'))
    }

    @Test
    fun predicates() {
        assertTrue(StrUtil.isNumeric("123"))
        assertFalse(StrUtil.isNumeric("12a"))
        assertTrue(StrUtil.isLowerCase("abc"))
        assertTrue(StrUtil.isUpperCase("ABC"))
        assertTrue(StrUtil.isAllCharMatch("aaa") { it == 'a' })
        assertTrue(StrUtil.containsBlank("a b"))
        assertTrue(StrUtil.containsOnly("aab", "ab"))
        assertEquals("abc", StrUtil.concat("a", null, "bc"))
        assertEquals("aaa", StrUtil.repeat("a", 3))
        assertEquals("ab", StrUtil.reverse("ba"))
    }
}
