package io.github.shijianjs.kmphutool.core

import io.github.shijianjs.kmphutool.core.lang.Pair
import io.github.shijianjs.kmphutool.core.lang.mutable.MutablePair
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class MutablePairTest {
    @Test
    fun mutatesInPlace() {
        val pair = MutablePair("a", 1)
        pair.setKey("b").setValue(2)
        assertEquals(Pair("b", 2), pair.get())
        assertTrue(pair == Pair("b", 2))

        pair.set(Pair("c", 3))
        assertEquals("c", pair.key)
        assertEquals(3, pair.value)
    }
}
