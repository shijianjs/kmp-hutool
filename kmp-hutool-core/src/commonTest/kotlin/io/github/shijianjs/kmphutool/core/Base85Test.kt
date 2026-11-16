package io.github.shijianjs.kmphutool.core

import io.github.shijianjs.kmphutool.core.codec.Base85
import io.github.shijianjs.kmphutool.core.codec.Base85Codec
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertTrue

class Base85Test {
    @Test
    fun encodesKnownAscii85Vector() {
        assertEquals("9jqo^", Base85.encode("Man "))
        assertEquals("", Base85.encode(""))
    }

    @Test
    fun roundTripsStandardAndZ85() {
        val text = "Hello, 世界! 0123456789"
        assertEquals(text, Base85.decodeStr(Base85.encode(text)))
        assertEquals(text, Base85.decodeStrZ85(Base85.encodeZ85(text)))
        assertTrue(Base85.encodeZ85(text).none { it in "\"\\" })
    }

    @Test
    fun handlesPartialBlocksAndRejectsBadInput() {
        val codec = Base85Codec.INSTANCE
        val bytes = byteArrayOf(1, 2, 3, 4, 5, 6)
        assertTrue(codec.decode(codec.encode(bytes)).contentEquals(bytes))
        assertFailsWith<IllegalArgumentException> { Base85.decode("ab~cd") }
    }
}
