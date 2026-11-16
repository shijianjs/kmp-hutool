package io.github.shijianjs.kmphutool.core

import io.github.shijianjs.kmphutool.core.map.MapProxy
import kotlin.test.Test
import kotlin.test.assertEquals

class MapProxyTest {
    @Test
    fun delegatesAndDefaults() {
        val proxy = MapProxy.create(mutableMapOf<Any?, Any?>("a" to 1))
        assertEquals(1, proxy["a"])
        assertEquals("x", proxy.getObj("missing", "x"))
        proxy["b"] = 2
        assertEquals(2, proxy["b"])
    }
}
