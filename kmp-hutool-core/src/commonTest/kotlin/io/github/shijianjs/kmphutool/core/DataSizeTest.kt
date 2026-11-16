package io.github.shijianjs.kmphutool.core

import io.github.shijianjs.kmphutool.core.io.unit.DataSize
import io.github.shijianjs.kmphutool.core.io.unit.DataSizeUtil
import io.github.shijianjs.kmphutool.core.io.unit.DataUnit
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class DataSizeTest {
    @Test
    fun parsesAndConverts() {
        assertEquals(12L * 1024, DataSizeUtil.parse("12KB"))
        assertEquals(5L * 1024 * 1024, DataSize.parse("5 MB").toBytes())
        assertEquals(20L, DataSize.parse("20").toBytes())
        assertEquals(1024L, DataSize.of(1, DataUnit.KILOBYTES).toBytes())
        assertEquals(1536L, DataSize.of(1.5, DataUnit.KILOBYTES).toBytes())
        assertEquals(1L, DataSize.ofMegabytes(1).toMegabytes())
        assertEquals(DataUnit.KILOBYTES, DataUnit.fromSuffix("KiB"))
        assertEquals(DataUnit.MEGABYTES, DataUnit.fromSuffix("m"))
    }

    @Test
    fun formatsReadableValues() {
        assertEquals("0", DataSizeUtil.format(0))
        assertEquals("1 KB", DataSizeUtil.format(1024))
        assertEquals("1.5 KB", DataSizeUtil.format(1536))
        assertEquals("1 MB", DataSizeUtil.format(1024 * 1024))
        assertEquals("0 MB", DataSizeUtil.format(1536, DataUnit.MEGABYTES))
    }

    @Test
    fun rejectsInvalidText() {
        assertTrue(runCatching { DataSize.parse("abc") }.isFailure)
    }
    @Test
    fun formatsThousands() {
        assertTrue(DataSizeUtil.format(1024L * 1024 * 1024).startsWith("1"))
    }
}

