package io.github.shijianjs.kmphutool.core

import io.github.shijianjs.kmphutool.core.date.format.GlobalCustomFormat
import kotlinx.datetime.Instant
import kotlin.test.Test
import kotlin.test.assertEquals

class GlobalCustomFormatTest {
    @Test
    fun formatsAndParsesEpochValues() {
        val instant = Instant.fromEpochMilliseconds(1_700_000_000_123)
        assertEquals("1700000000123", GlobalCustomFormat.format(instant, GlobalCustomFormat.FORMAT_MILLISECONDS))
        assertEquals(instant, GlobalCustomFormat.parse("1700000000123", GlobalCustomFormat.FORMAT_MILLISECONDS))
        assertEquals("1700000000", GlobalCustomFormat.format(instant, GlobalCustomFormat.FORMAT_SECONDS))
    }
}
