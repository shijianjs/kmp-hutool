package io.github.shijianjs.kmphutool.core

import io.github.shijianjs.kmphutool.core.io.NullOutputStream
import kotlin.test.Test

class NullOutputStreamTest {
    @Test
    fun acceptsEverything() {
        val out = NullOutputStream()
        out.write(1)
        out.write(byteArrayOf(1, 2), 0, 2)
        out.flush()
        out.close()
    }
}
