package io.github.shijianjs.kmphutool.core

import io.github.shijianjs.kmphutool.core.builder.CompareToBuilder
import io.github.shijianjs.kmphutool.core.builder.EqualsBuilder
import io.github.shijianjs.kmphutool.core.builder.GenericBuilder
import io.github.shijianjs.kmphutool.core.builder.HashCodeBuilder
import io.github.shijianjs.kmphutool.core.lang.func.Supplier2
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class BuilderTest {
    private data class Box(var id: Long = 0, var title: String = "")

    @Test
    fun genericBuilderAppliesModifiers() {
        val box = GenericBuilder.of { Box() }
            .with({ b: Box -> b.id = 7 })
            .with({ b: Box, t: String -> b.title = t }, "hello")
            .build()
        assertEquals(7L, box.id)
        assertEquals("hello", box.title)

        val withArgs = GenericBuilder.of(Supplier2 { id: Long, title: String -> Box(id, title) }, 1L, "x").build()
        assertEquals(Box(1L, "x"), withArgs)
    }

    @Test
    fun equalsAndHashBuildersAgree() {
        val a = Box(1, "a")
        val b = Box(1, "a")
        val equals = EqualsBuilder().append(a.id, b.id).append(a.title, b.title).isEquals()
        assertTrue(equals)

        val different = EqualsBuilder().append(a.id, 2L).isEquals()
        assertFalse(different)

        val hashA = HashCodeBuilder().append(a.id).append(a.title).toHashCode()
        val hashB = HashCodeBuilder().append(b.id).append(b.title).toHashCode()
        assertEquals(hashA, hashB)

        val compare = CompareToBuilder().append(a.id, b.id).append(a.title, b.title).toComparison()
        assertEquals(0, compare)
        assertTrue(CompareToBuilder().append("a", "b").toComparison() < 0)
        assertTrue(CompareToBuilder().append(null, "b").toComparison() < 0)
    }
}
