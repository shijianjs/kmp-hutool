package io.github.shijianjs.kmphutool.core

import io.github.shijianjs.kmphutool.core.exceptions.ValidateException
import io.github.shijianjs.kmphutool.core.lang.Assert
import io.github.shijianjs.kmphutool.core.lang.Dict
import io.github.shijianjs.kmphutool.core.lang.Validator
import io.github.shijianjs.kmphutool.core.util.IdUtil
import io.github.shijianjs.kmphutool.core.util.NumberUtil
import io.github.shijianjs.kmphutool.core.util.RandomUtil
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertTrue

class LangUtilTest {
    @Test
    fun dictBasics() {
        val dict = Dict.of("name", "张三", "age", 30)
        assertEquals("张三", dict.getStr("name"))
        assertEquals(30, dict.getInt("age"))
        assertEquals(30L, dict.getLong("age"))
        assertEquals(true, Dict.of("flag", "true").getBool("flag"))
        assertEquals(listOf("1", "2"), Dict.of("list", "1,2").getList("list"))
        assertEquals("missing", dict.getStr("missing") ?: "missing")
    }

    @Test
    fun dictNesting() {
        val inner = Dict.of("c", 1)
        val outer = Dict.of("a", Dict.of("b", inner))
        assertEquals(1, outer.getByPath("a.b.c"))
        assertEquals(1, outer.getDict("a")?.getDict("b")?.getInt("c"))
        assertNull(outer.getByPath("a.x.c"))
    }

    @Test
    fun dictFilterAndRemove() {
        val dict = Dict.ofPairs("a" to 1, "b" to 2, "c" to 3)
        assertEquals(listOf("a", "b"), dict.filter("a", "b", "zz").keys.toList())
        dict.removeEqual(Dict.ofPairs("a" to 1, "b" to 9), "c")
        assertEquals(setOf("b", "c"), dict.keys)
    }

    @Test
    fun validatorPredicates() {
        assertTrue(Validator.isEmail("someone@example.com"))
        assertFalse(Validator.isEmail("not-an-email"))
        assertTrue(Validator.isMobile("13812345678"))
        assertFalse(Validator.isMobile("12345"))
        assertTrue(Validator.isIpv4("192.168.1.1"))
        assertFalse(Validator.isIpv4("192.168.1.300"))
        assertTrue(Validator.isUrl("https://example.com/path"))
        assertTrue(Validator.isWord("abc123"))
        assertFalse(Validator.isWord("abc 123"))
        assertTrue(Validator.isNumber("12.5"))
        assertTrue(Validator.isUUID(IdUtil.randomUUID()))
        assertTrue(Validator.hasChinese("abc中文"))
        assertTrue(Validator.isChinese("中文"))
        assertTrue(Validator.isHex("1a2b"))
        assertTrue(Validator.isBetween(5, 1, 10))
    }

    @Test
    fun validatorThrows() {
        val error = assertFailsWith<ValidateException> { Validator.validateEmail("bad") }
        assertTrue(error.message!!.contains("bad"))
        Validator.validateEmail("ok@example.com")
    }

    @Test
    fun assertions() {
        assertEquals("value", Assert.notNull("value"))
        assertTrue(Assert.assertTrue(true))
        Assert.isNull(null)
        assertFailsWith<ValidateException> { Assert.notNull(null) }
        val error = assertFailsWith<ValidateException> { Assert.isNull("x", "must be null, got {}", "x") }
        assertEquals("must be null, got x", error.message)
        assertFailsWith<ValidateException> { Assert.notEmpty("") }
        assertFailsWith<ValidateException> { Assert.notBlank("  ") }
        assertFailsWith<ValidateException> { Assert.equals(1, 2) }
        assertFailsWith<ValidateException> { Assert.checkIndex(5, 3) }
    }

    @Test
    fun ids() {
        val uuid = IdUtil.randomUUID()
        assertEquals(36, uuid.length)
        assertEquals('-', uuid[8])
        assertEquals('4', uuid[14])
        assertEquals(32, IdUtil.simpleUUID().length)
        assertEquals(24, IdUtil.objectId().length)
        assertEquals(21, IdUtil.nanoId().length)
        assertEquals(8, IdUtil.nanoId(8).length)

        val snowflake = IdUtil.createSnowflake(1, 1)
        val first = snowflake.nextId()
        val second = snowflake.nextId()
        assertTrue(second > first)
        assertTrue(snowflake.timestamp(first) > 0)
    }

    @Test
    fun numbers() {
        assertTrue(NumberUtil.isNumber("123"))
        assertTrue(NumberUtil.isNumber("-1.5"))
        assertFalse(NumberUtil.isNumber("abc"))
        assertTrue(NumberUtil.isInteger("12"))
        assertTrue(NumberUtil.isDouble("1.5"))
        assertEquals(5, NumberUtil.parseInt("5"))
        assertEquals(7, NumberUtil.parseInt("abc", 7))
        assertEquals(5L, NumberUtil.parseLong("5"))
        assertEquals(1.5, NumberUtil.parseDouble("1.5"))
        assertEquals(3.0, NumberUtil.max(1, 2, 3).toDouble())
        assertEquals(1.0, NumberUtil.min(1, 2, 3).toDouble())
        assertTrue(NumberUtil.isEven(4))
        assertTrue(NumberUtil.isOdd(5))
        assertTrue(NumberUtil.isPowerOfTwo(8))
        assertTrue(NumberUtil.isPrimes(7))
        assertFalse(NumberUtil.isPrimes(9))
        assertEquals(120L, NumberUtil.factorial(5))
        assertEquals(3.14, NumberUtil.round(3.14159, 2))
        assertEquals("3.14", NumberUtil.roundStr(3.14159, 2))
        assertEquals(3.14, NumberUtil.roundDown(3.149, 2))
        assertEquals(2.0, NumberUtil.roundHalfEven(2.5, 0))
        assertEquals(4L, NumberUtil.ceil(3.2))
        assertEquals(3L, NumberUtil.floor(3.2))
        assertEquals(2L, NumberUtil.ceilDiv(5L, 3L))
        assertEquals(5.0, NumberUtil.add(2, 3))
        assertEquals(6.0, NumberUtil.mul(2, 3))
        assertTrue(NumberUtil.isGreater(5, 4))
        assertTrue(NumberUtil.isIn(5, 1, 10))
        assertEquals(5, NumberUtil.binaryToInt("101"))
        assertEquals("101", NumberUtil.getBinaryStr(5))
        assertEquals(0L, NumberUtil.null2Zero(null))
        assertEquals(1L, NumberUtil.zero2One(0))
        assertEquals(listOf(0, 1, 2), NumberUtil.range(3))
        assertEquals(2, NumberUtil.toInt(2L))
        assertEquals(2L, NumberUtil.toLong(2))
        assertEquals(2.0, NumberUtil.toDouble(2))
    }

    @Test
    fun random() {
        repeat(20) {
            val value = RandomUtil.randomInt(1, 10)
            assertTrue(value in 1..9)
        }
        assertEquals(4, RandomUtil.randomNumber(4).length)
        assertEquals(6, RandomUtil.randomString(6).length)
        assertEquals(6, RandomUtil.randomStringUpper(6).length)
        assertEquals(6, RandomUtil.randomStringLower(6).length)
        assertTrue(RandomUtil.randomEle(listOf("a", "b")) in listOf("a", "b"))
        assertNull(RandomUtil.randomEle(emptyList<String>()))
        assertNull(RandomUtil.weightRandom(emptyMap<String, Int>()))
        assertEquals(2, RandomUtil.randomEles(listOf(1, 2, 3), 2).size)
    }
}
