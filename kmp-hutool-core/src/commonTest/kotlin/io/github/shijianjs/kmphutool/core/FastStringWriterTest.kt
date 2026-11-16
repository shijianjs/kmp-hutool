package io.github.shijianjs.kmphutool.core

import io.github.shijianjs.kmphutool.core.io.FastStringWriter
import kotlin.test.Test
import kotlin.test.assertEquals

class FastStringWriterTest {
    @Test
    fun writesAllOverloads() {
        val writer = FastStringWriter()
        writer.write("abc")
        writer.write("defg", 1, 2)
        writer.write(' '.code)
        writer.write(charArrayOf('x', 'y', 'z'), 1, 1)
        assertEquals("abcef y", writer.toString())
    }
}
