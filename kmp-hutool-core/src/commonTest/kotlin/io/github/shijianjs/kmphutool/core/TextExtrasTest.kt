package io.github.shijianjs.kmphutool.core

import io.github.shijianjs.kmphutool.core.text.ASCIIStrCache
import io.github.shijianjs.kmphutool.core.text.PasswdStrength
import kotlin.test.Test
import kotlin.test.assertContains
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class TextExtrasTest {
    @Test
    fun asciiCache() {
        assertEquals("A", ASCIIStrCache.toString('A'))
        assertEquals("\u0000", ASCIIStrCache.toString('\u0000'))
        assertEquals("汉", ASCIIStrCache.toString('汉'))
    }

    @Test
    fun weakPasswords() {
        assertEquals(0, PasswdStrength.check("aaa"))
        assertEquals(0, PasswdStrength.check("111111"))
        assertEquals(PasswdStrength.Level.EASY, PasswdStrength.getLevel("123456"))
        assertEquals(PasswdStrength.Level.EASY, PasswdStrength.getLevel("password"))
    }

    @Test
    fun strongPasswords() {
        val strong = PasswdStrength.check("Abc123!@#Xyz789")
        assertTrue(strong >= 7, "expected a strong score, got $strong")
        assertTrue(PasswdStrength.getLevel("Abc123!@#Xyz789") >= PasswdStrength.Level.STRONG)
    }
}
