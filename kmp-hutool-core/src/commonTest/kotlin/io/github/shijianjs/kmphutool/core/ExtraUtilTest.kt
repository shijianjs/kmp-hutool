package io.github.shijianjs.kmphutool.core

import io.github.shijianjs.kmphutool.core.collection.Partition
import io.github.shijianjs.kmphutool.core.comparator.CompareUtil
import io.github.shijianjs.kmphutool.core.comparator.FuncComparator
import io.github.shijianjs.kmphutool.core.comparator.VersionComparator
import io.github.shijianjs.kmphutool.core.date.StopWatch
import io.github.shijianjs.kmphutool.core.lang.Holder
import io.github.shijianjs.kmphutool.core.lang.MutableObj
import io.github.shijianjs.kmphutool.core.lang.Range
import io.github.shijianjs.kmphutool.core.lang.Tuple
import io.github.shijianjs.kmphutool.core.lang.WeightRandom
import io.github.shijianjs.kmphutool.core.map.CaseInsensitiveMap
import io.github.shijianjs.kmphutool.core.math.MathUtil
import io.github.shijianjs.kmphutool.core.text.StrBuilder
import io.github.shijianjs.kmphutool.core.text.StrMatcher
import io.github.shijianjs.kmphutool.core.text.UnicodeUtil
import io.github.shijianjs.kmphutool.core.util.CreditCodeUtil
import io.github.shijianjs.kmphutool.core.util.DesensitizedUtil
import io.github.shijianjs.kmphutool.core.util.EnumUtil
import io.github.shijianjs.kmphutool.core.util.IdcardUtil
import io.github.shijianjs.kmphutool.core.util.PageUtil
import io.github.shijianjs.kmphutool.core.util.PhoneUtil
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertTrue

class ExtraUtilTest {
    @Test
    fun paging() {
        assertEquals(4, PageUtil.totalPage(10, 3))
        assertEquals(0, PageUtil.totalPage(0, 3))
        assertEquals(3, PageUtil.pageNo(6, 3))
        assertEquals(6, PageUtil.offset(3, 3))
        assertEquals(2, PageUtil.firstPageNo(3, 3))
        assertEquals(1, PageUtil.firstPageNo(1, 3))
        assertEquals(4, PageUtil.lastPageNo(3, 3, 10))
        assertEquals(4, PageUtil.lastPageNo(4, 3, 10))
        assertTrue(PageUtil.isValidPageNo(2, 3, 10))
        assertEquals(1, PageUtil.fixPageNo(0, 3, 10))
    }

    @Test
    fun langBoxes() {
        assertEquals("1 ~ 10", Range.of(1, 10).toString())
        assertTrue(Range.of(1, 10).contains(5))
        assertFalse(Range.of(1, 10).contains(11))
        assertTrue(Range.of(2, 9).let { Range.of(1, 10).containsAll(it) })

        val tuple = Tuple.of(1, "a", null)
        assertEquals(3, tuple.size)
        assertEquals("a", tuple[1])
        assertEquals(0, tuple.indexOf(1))
        assertEquals("(1, a, null)", tuple.toString())

        val holder = Holder("x")
        assertEquals("x", holder.value)
        val mutable = MutableObj(1)
        mutable.set(2)
        assertEquals(2, mutable.get())
        assertEquals(0, MutableObj<Int>().getOrDefault(0))

        val weighted = WeightRandom.of(mapOf("a" to 1, "b" to 1))
        assertTrue(weighted.next() in listOf("a", "b"))
        assertEquals(2, weighted.next(2).size)
        assertNull(WeightRandom.of(emptyMap<String, Int>()).next())
    }

    @Test
    fun strBuilderAndUnicode() {
        val builder = StrBuilder("abc").append('d').append(null).append("ef", 0, 1)
        assertEquals("abcde", builder.toString())
        builder.insert(0, ">")
        assertEquals(">abcde", builder.toString())
        builder.delete(0, 1)
        assertEquals("abcde", builder.toString())
        builder.replace(0, 1, "X")
        assertEquals("Xbcde", builder.toString())
        builder.reverse()
        assertEquals("edcbX", builder.toString())

        assertEquals("\\u4e2d", UnicodeUtil.toUnicode('中'))
        assertEquals("\\u4e2d\\u6587", UnicodeUtil.toUnicode("中文"))
        assertEquals("中文", UnicodeUtil.decodeUnicode("\\u4e2d\\u6587"))
        assertEquals("plain", UnicodeUtil.decodeUnicode("plain"))
    }

    @Test
    fun comparators() {
        assertEquals(-1, CompareUtil.compare(null, 1))
        assertEquals(1, CompareUtil.compare(null, 1, nullGreater = true))
        assertEquals(-1, CompareUtil.compare(1, 2))
        assertEquals(0, CompareUtil.compare(1, 1))
        assertEquals(2, CompareUtil.max<Int>(null, 2))
        assertEquals(1, CompareUtil.min<Int>(1, null))

        val versions = VersionComparator()
        assertEquals(0, versions.compare("1.0", "1.0.0"))
        assertEquals(1, versions.compare("1.10", "1.9"))
        assertEquals(-1, versions.compare("1.0-SNAPSHOT", "1.0"))
        assertEquals(1, versions.compare("2.0", null))
        assertEquals(-1, versions.compare(null, "2.0"))
    }

    @Test
    fun stopWatch() {
        val watch = StopWatch.create("test")
        assertEquals("test", watch.getId())
        assertFalse(watch.isRunning())
        watch.start("task")
        assertTrue(watch.isRunning())
        assertEquals("task", watch.currentTaskName())
        watch.stop()
        assertFalse(watch.isRunning())
        assertEquals(1, watch.getTaskCount())
        assertEquals("task", watch.getLastTaskName())
        assertTrue(watch.totalMillis() >= 0)
        assertTrue(watch.prettyPrint().contains("task"))
        watch.reset()
        assertEquals(0, watch.getTaskCount())
    }

    @Test
    fun enums() {
        assertEquals(listOf("A", "B"), EnumUtil.getNames<Sample>())
        assertTrue(EnumUtil.contains<Sample>("b"))
        assertFalse(EnumUtil.contains<Sample>("missing"))
        assertEquals(Sample.B, EnumUtil.fromString<Sample>("b"))
        assertNull(EnumUtil.fromString<Sample>("missing"))
    }

    @Test
    fun idCards() {
        assertTrue(IdcardUtil.isValidCard18("11010519491231002X"))
        assertTrue(IdcardUtil.isValidCard("11010519491231002X"))
        assertFalse(IdcardUtil.isValidCard18("110105194912310021"))
        assertEquals("11010519491231002X", IdcardUtil.convert15To18("110105491231002"))
        assertEquals("110105491231002", IdcardUtil.convert18To15("11010519491231002X"))
        assertEquals("19491231", IdcardUtil.getBirthByIdCard("11010519491231002X"))
        assertEquals(1949, IdcardUtil.getYearByIdCard("11010519491231002X"))
        assertEquals(12, IdcardUtil.getMonthByIdCard("11010519491231002X"))
        assertEquals(31, IdcardUtil.getDayByIdCard("11010519491231002X"))
        assertEquals(0, IdcardUtil.getGenderByIdCard("11010519491231002X"))
        assertEquals("北京", IdcardUtil.getProvinceByIdCard("11010519491231002X"))
        assertTrue(IdcardUtil.getAgeByIdCard("11010519491231002X") > 0)
        assertNull(IdcardUtil.getBirthByIdCard("not-a-card"))
    }

    @Test
    fun creditCodes() {
        val code = CreditCodeUtil.randomCreditCode()
        assertEquals(18, code.length)
        assertTrue(CreditCodeUtil.isCreditCode(code))
        assertFalse(CreditCodeUtil.isCreditCode("91350100M000100Y4X"))
        assertFalse(CreditCodeUtil.isCreditCodeSimple("short"))
    }

    @Test
    fun phonesAndMasks() {
        assertTrue(PhoneUtil.isMobile("13812345678"))
        assertFalse(PhoneUtil.isMobile("12345"))
        assertTrue(PhoneUtil.isTel("0571-88888888"))
        assertTrue(PhoneUtil.isTel400800("400-800-8888"))
        assertTrue(PhoneUtil.isPhone("13812345678"))
        assertEquals("138****5678", PhoneUtil.hideBetween("13812345678"))
        assertEquals("138********", PhoneUtil.hideAfter("13812345678"))
        assertEquals("*******5678", PhoneUtil.hideBefore("13812345678"))

        assertEquals("138****5678", DesensitizedUtil.mobilePhone("13812345678"))
        assertEquals("张**", DesensitizedUtil.chineseName("张三丰"))
        assertEquals("1" + "*".repeat(15) + "23", DesensitizedUtil.idCardNum("110105194912310023", 1, 2))
        assertEquals("a***@example.com", DesensitizedUtil.email("alice@example.com"))
        assertEquals("******", DesensitizedUtil.password("secret"))
        assertEquals("6222 **** **** **** 123", DesensitizedUtil.bankCard("6222021234567890123"))
        assertEquals("6222 **** **** 7890", DesensitizedUtil.bankCard("6222 0212 3456 7890"))
        assertEquals("京A1***5", DesensitizedUtil.carLicense("京A12345"))
    }

    @Test
    fun mathCounts() {
        assertEquals(20L, MathUtil.arrangementCount(5, 2))
        assertEquals(120L, MathUtil.arrangementCount(5))
        assertEquals(10L, MathUtil.combinationCount(5, 2))
        assertEquals(6, MathUtil.arrangementSelect(arrayOf("a", "b", "c"), 2).size)
        assertEquals(3, MathUtil.combinationSelect(arrayOf("a", "b", "c"), 2).size)
        assertEquals(1234L, MathUtil.yuanToCent(12.34))
        assertEquals(12.34, MathUtil.centToYuan(1234))
    }

    @Test
    fun partitionAndCaseInsensitiveMap() {
        assertEquals(
            listOf(listOf(1, 2), listOf(3, 4), listOf(5)),
            Partition.partitions(listOf(1, 2, 3, 4, 5), 2),
        )
        assertEquals(3, Partition.of(listOf(1, 2, 3, 4, 5), 2).count())

        val map = CaseInsensitiveMap<Int>()
        map["Name"] = 1
        assertEquals(1, map["name"])
        assertEquals(1, map["NAME"])
        assertTrue(map.containsKey("nAmE"))
        map.remove("NAME")
        assertTrue(map.isEmpty())
    }

    @Test
    fun wildcards() {
        assertTrue(StrMatcher.isMatch("*.kt", "StrUtil.kt"))
        assertTrue(StrMatcher.isMatch("Str?til.kt", "StrUtil.kt"))
        assertFalse(StrMatcher.isMatch("*.java", "StrUtil.kt"))
        assertTrue(StrMatcher.isMatch("*", "anything"))
        assertTrue(StrMatcher.isMatch("STR*", "StrUtil.kt", isIgnoreCase = true))
        assertFalse(StrMatcher.isMatch("a*", "b"))
    }

    @Test
    fun functionComparator() {
        val byLength = FuncComparator<String>(compareFunc = { a, b -> a.length - b.length })
        assertTrue(byLength.compare(null, "x") < 0)
        assertTrue(byLength.compare("x", null) > 0)
        assertEquals(0, byLength.compare("aa", "bb"))
        assertEquals(-1, byLength.compare("a", "bb"))
    }

    enum class Sample { A, B }
}
