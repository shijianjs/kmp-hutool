package io.github.shijianjs.kmphutool.core

import io.github.shijianjs.kmphutool.core.codec.Base16Codec
import io.github.shijianjs.kmphutool.core.codec.Base58
import io.github.shijianjs.kmphutool.core.codec.Base62
import io.github.shijianjs.kmphutool.core.codec.Rot
import kotlin.test.Test
import kotlin.test.assertContentEquals
import kotlin.test.assertEquals

class CodecBaseTest {
    @Test
    fun base16RoundTrip() {
        val bytes = byteArrayOf(0x12, 0x34, 0xAB.toByte(), 0x0F)
        assertEquals("1234ab0f", Base16Codec().encode(bytes))
        assertEquals("1234AB0F", Base16Codec(false).encode(bytes))
        assertContentEquals(bytes, Base16Codec().decode("12 34 ab 0f"))
        assertContentEquals(byteArrayOf(0x0A), Base16Codec().decode("a"))
    }

    @Test
    fun base58RoundTrip() {
        val text = "Hello Base58!"
        val encoded = Base58.encode(text.encodeToByteArray())
        assertEquals(text, Base58.decode(encoded).decodeToString())
        // Leading zero bytes must survive.
        val zeroLed = byteArrayOf(0, 0, 1, 2)
        assertContentEquals(zeroLed, Base58.decode(Base58.encode(zeroLed)))
    }

    @Test
    fun base62RoundTrip() {
        val text = "https://example.com/some/long/path"
        assertEquals(text, Base62.decodeStr(Base62.encode(text)))
        assertEquals(text, Base62.decodeStrInverted(Base62.encodeInverted(text)))
    }

    @Test
    fun rot13RoundTrip() {
        val encoded = Rot.encode13("Hello, World! 123")
        assertEquals("Uryyb, Jbeyq! 456", encoded)
        assertEquals("Hello, World! 123", Rot.decode13(encoded))
        // Without digits the numbers stay put.
        assertEquals("Uryyb 123", Rot.encode13("Hello 123", false))
    }
}


