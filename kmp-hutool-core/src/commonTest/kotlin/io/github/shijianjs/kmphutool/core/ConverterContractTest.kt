package io.github.shijianjs.kmphutool.core

import io.github.shijianjs.kmphutool.core.convert.CastUtil
import io.github.shijianjs.kmphutool.core.convert.Converter
import io.github.shijianjs.kmphutool.core.convert.TypeConverter
import io.github.shijianjs.kmphutool.core.lang.copier.Copier
import io.github.shijianjs.kmphutool.core.lang.intern.Interner
import kotlin.reflect.KClass
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class ConverterContractTest {
    @Test
    fun converterCanFailQuietly() {
        val converter = Converter<Int> { value, _ -> (value as String).toInt() }
        assertEquals(42, converter.convert("42", 0))
        assertEquals(0, converter.convertWithCheck("x", 0, quietly = true))
    }

    @Test
    fun typeConverterSeesTargetType() {
        val converter = TypeConverter { type: KClass<*>, value -> "$type:$value" }
        assertTrue((converter.convert(Int::class, 1) as String).endsWith(":1"))
    }

    @Test
    fun copierAndInternerAreFunctional() {
        val copier = Copier { "copy" }
        assertEquals("copy", copier.copy())
        val interner = object : Interner<String> {
            private val pool = mutableMapOf<String, String>()
            override fun intern(sample: String): String = pool.getOrPut(sample) { sample }
        }
        assertTrue(interner.intern("a") === interner.intern("a"))
    }

    @Test
    fun castUtilPassesCollectionsThrough() {
        val list = listOf(1, 2)
        assertEquals(list, CastUtil.castUp(list))
    }
}
