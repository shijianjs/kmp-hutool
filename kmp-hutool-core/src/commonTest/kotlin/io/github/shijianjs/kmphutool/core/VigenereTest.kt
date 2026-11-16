package io.github.shijianjs.kmphutool.core

import io.github.shijianjs.kmphutool.core.crypto.CipherMode
import io.github.shijianjs.kmphutool.core.crypto.Padding
import io.github.shijianjs.kmphutool.core.crypto.Vigenere
import kotlin.test.Test
import kotlin.test.assertEquals

class VigenereTest {
    @Test
    fun roundTripsPrintableText() {
        val encrypted = Vigenere.encrypt("Hello, World!", "key")
        assertEquals("Hello, World!", Vigenere.decrypt(encrypted, "key"))
    }

    @Test
    fun keepsCipherConstants() {
        assertEquals(1, CipherMode.encrypt.getValue())
        assertEquals(Padding.PKCS5Padding, Padding.valueOf("PKCS5Padding"))
    }
}
