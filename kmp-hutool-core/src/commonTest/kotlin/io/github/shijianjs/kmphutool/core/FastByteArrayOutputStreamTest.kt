package io.github.shijianjs.kmphutool.core

import io.github.shijianjs.kmphutool.core.io.FastByteArrayOutputStream
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class FastByteArrayOutputStreamTest {
    @Test
    fun accumulatesAndResets() {
        val out = FastByteArrayOutputStream(2)
        out.write("ab".encodeToByteArray(), 0, 2)
        out.write('c'.code)
        assertEquals(3, out.size())
        assertEquals("abc", out.toString())
        out.reset()
        assertTrue(out.toByteArray().isEmpty())
    }
}
