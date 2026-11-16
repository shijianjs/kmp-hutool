package io.github.shijianjs.kmphutool.core

import io.github.shijianjs.kmphutool.core.lang.Opt
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class OptTest {
    @Test
    fun wrapsValuesAndEmpties() {
        assertEquals(3, Opt.of(3).get())
        assertTrue(Opt.ofNullable<String>(null).isEmpty())
        assertTrue(Opt.ofBlankAble("  ").isEmpty())
        assertFalse(Opt.ofEmptyAble(listOf(1)).isEmpty())
        assertEquals(1, Opt.ofNullable(0).map { it + 1 }.filter { it == 1 }.orElse(0))
    }

    @Test
    fun mapsAndDefaults() {
        assertEquals("3!", Opt.of(3).map { "$it!" }.get())
        assertEquals(7, Opt.empty<Int>().orElseGet { 7 })
        assertEquals("fallback", Opt.ofNullable<String>(null).orElse("fallback"))
    }

    @Test
    fun capturesFailures() {
        val failed = Opt.ofTry<String> { error("boom") }
        assertTrue(failed.isFail())
        assertEquals("ok", failed.exceptionOrElse("ok"))
    }
}
