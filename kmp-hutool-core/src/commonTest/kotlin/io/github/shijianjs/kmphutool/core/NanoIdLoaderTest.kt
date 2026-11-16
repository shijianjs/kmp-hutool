package io.github.shijianjs.kmphutool.core

import io.github.shijianjs.kmphutool.core.lang.id.NanoId
import io.github.shijianjs.kmphutool.core.lang.loader.LazyFunLoader
import io.github.shijianjs.kmphutool.core.lang.loader.LazyLoader
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertSame
import kotlin.test.assertTrue

class NanoIdLoaderTest {
    @Test
    fun nanoIdHasRequestedShape() {
        val id = NanoId.randomNanoId()
        assertEquals(NanoId.DEFAULT_SIZE, id.length)
        val custom = NanoId.randomNanoId(alphabet = "ab".toCharArray(), size = 8)
        assertEquals(8, custom.length)
        assertTrue(custom.all { it == 'a' || it == 'b' })
    }

    @Test
    fun lazyLoadersInitializeOnce() {
        var calls = 0
        val loader = object : LazyLoader<String>() {
            override fun init(): String {
                calls++
                return "value"
            }
        }
        assertSame(loader.get(), loader.get())
        assertEquals(1, calls)

        var funCalls = 0
        val funLoader = LazyFunLoader.on {
            funCalls++
            "lazy"
        }
        assertFalse(funLoader.isInitialize())
        assertEquals("lazy", funLoader.get())
        assertTrue(funLoader.isInitialize())
        assertEquals(1, funCalls)
    }
}
