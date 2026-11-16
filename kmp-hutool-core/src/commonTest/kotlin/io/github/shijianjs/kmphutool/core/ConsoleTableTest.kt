package io.github.shijianjs.kmphutool.core

import io.github.shijianjs.kmphutool.core.lang.ConsoleTable
import kotlin.test.Test
import kotlin.test.assertTrue

class ConsoleTableTest {
    @Test
    fun rendersBordersAndRows() {
        val table = ConsoleTable().setSBCMode(false).addHeader("name", "value").addBody("a", "1")
        val text = table.toString()
        assertTrue(text.contains("name"))
        assertTrue(text.contains("value"))
        assertTrue(text.startsWith("+"))
        assertTrue(text.count { it == '\n' } == 5)
    }
}
