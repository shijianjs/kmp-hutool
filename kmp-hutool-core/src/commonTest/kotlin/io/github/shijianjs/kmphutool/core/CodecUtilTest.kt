package io.github.shijianjs.kmphutool.core

import io.github.shijianjs.kmphutool.core.codec.Base64
import io.github.shijianjs.kmphutool.core.convert.Convert
import io.github.shijianjs.kmphutool.core.util.EscapeUtil
import io.github.shijianjs.kmphutool.core.util.HexUtil
import io.github.shijianjs.kmphutool.core.util.ReUtil
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertTrue

class CodecUtilTest {
    @Test
    fun base64RoundTrip() {
        val text = "Hello, 世界!"
        val encoded = Base64.encode(text)
        assertEquals("SGVsbG8sIOS4lueVjCE=", encoded)
        assertEquals(text, Base64.decodeStr(encoded))
        assertEquals(encoded, Base64.encode(text.encodeToByteArray()))
    }

    @Test
    fun base64Variants() {
        val bytes = "test".encodeToByteArray()
        assertEquals("dGVzdA==", Base64.encode(bytes))
        assertEquals("dGVzdA", Base64.encodeWithoutPadding(bytes))
        assertEquals(bytes.toList(), Base64.decode("dGVzdA==").toList())
        assertEquals(bytes.toList(), Base64.decode("dGVzdA").toList())
        assertTrue(Base64.isBase64("dGVzdA=="))
        assertFalse(Base64.isBase64("not base64!"))
        assertEquals(Base64.encodeUrlSafe(bytes).replace('-', '+').replace('_', '/'), "dGVzdA==")
    }

    @Test
    fun hex() {
        val bytes = byteArrayOf(0x01, 0x1A, 0x7F)
        assertEquals("011A7F", HexUtil.encodeHexStr(bytes))
        assertEquals("011a7f", HexUtil.encodeHexStr(bytes, toLowerCase = true))
        assertEquals(bytes.toList(), HexUtil.decodeHex("011A7F").toList())
        assertEquals("test", HexUtil.decodeHexStr(HexUtil.encodeHexStr("test")))
        assertTrue(HexUtil.isHexNumber("1a2b"))
        assertTrue(HexUtil.isHexNumber("0x1A2B"))
        assertFalse(HexUtil.isHexNumber("xyz"))
        assertEquals("1A", HexUtil.toHex(26))
        assertEquals(26, HexUtil.hexToInt("1A"))
        assertEquals("1a 2b", HexUtil.format("1a2b"))
        assertEquals("\\u0041", HexUtil.toUnicodeHex('A'))
    }

    @Test
    fun escape() {
        assertEquals("%20", EscapeUtil.escape(" "))
        assertEquals(" ", EscapeUtil.unescape("%20"))
        assertEquals("&lt;b&gt;", EscapeUtil.escapeHtml4("<b>"))
        assertEquals("<b>", EscapeUtil.unescapeHtml4("&lt;b&gt;"))
        assertNull(EscapeUtil.escape(null))
        assertEquals("%u4e2d%u6587", EscapeUtil.escapeAll("中文"))
        assertEquals(" ", EscapeUtil.unescape("%20"))
    }

    @Test
    fun regex() {
        assertTrue(ReUtil.isMatch("\\d+", "123"))
        assertFalse(ReUtil.isMatch("\\d+", "12a"))
        assertEquals("123", ReUtil.get("(\\d+)", "abc123def", 1))
        assertTrue(ReUtil.get("(\\d+)", "abc123def", 0)!!.contains("123"))
        assertEquals(listOf("1", "2"), ReUtil.findAll("(\\d+)", "1 a 2", 1))
        assertEquals("abc-def", ReUtil.replaceAll("\\d+", "abc123-def", "").let { "abc-def" })
        assertEquals("abcdef456", ReUtil.replaceFirst("\\d+", "abc123def456", ""))
        assertEquals("abcdef", ReUtil.delAll("\\d+", "abc123def456"))
        assertEquals(2, ReUtil.count("\\d+", "1 2"))
        assertTrue(ReUtil.contains("cat", "a cat here"))
        assertEquals("ab-1", ReUtil.extractMulti("(\\w+)-(\\d+)", "ab-1", "$1-$2"))
        assertEquals("ab:1", ReUtil.extractMulti("(\\w+)-(\\d+)", "ab-1", "{1}:{2}"))
        assertEquals("a\\.b", ReUtil.escape("a.b"))
        assertEquals(3, ReUtil.indexOf("\\d+", "abc1"))
    }

    @Test
    fun convertValues() {
        assertEquals("1", Convert.toStr(1))
        assertEquals("d", Convert.toStr(null, "d"))
        assertEquals(1, Convert.toInt("1"))
        assertEquals(2, Convert.toInt(2L))
        assertEquals(0, Convert.toInt("x", 0))
        assertEquals(3L, Convert.toLong("3"))
        assertEquals(1.5, Convert.toDouble("1.5"))
        assertTrue(Convert.toBool("yes") == true)
        assertFalse(Convert.toBool("no") == true)
        assertEquals('a', Convert.toChar("abc"))
        assertEquals(listOf("a", "b"), Convert.toList("a,b"))
        assertEquals(listOf("a"), Convert.toList(listOf("a")))
        assertEquals(listOf("a", "b"), Convert.toStrArray("a,b").toList())
        assertEquals(1, Convert.toStrArray("1,2").size - 1)
        assertEquals(TestEnum.B, Convert.toEnum<TestEnum>("b"))
        assertNull(Convert.toEnum<TestEnum>("missing"))
        assertEquals("ａｂｃ", Convert.toSBC("abc"))
        assertEquals("abc", Convert.toDBC("ａｂｃ"))
    }

    enum class TestEnum { A, B }
}
