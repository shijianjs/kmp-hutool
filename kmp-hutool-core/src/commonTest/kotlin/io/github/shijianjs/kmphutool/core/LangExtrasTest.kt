package io.github.shijianjs.kmphutool.core

import io.github.shijianjs.kmphutool.core.lang.generator.ObjectIdGenerator
import io.github.shijianjs.kmphutool.core.lang.generator.SnowflakeGenerator
import io.github.shijianjs.kmphutool.core.lang.generator.UUIDGenerator
import io.github.shijianjs.kmphutool.core.lang.mutable.MutableBool
import io.github.shijianjs.kmphutool.core.lang.mutable.MutableDouble
import io.github.shijianjs.kmphutool.core.lang.mutable.MutableInt
import io.github.shijianjs.kmphutool.core.lang.mutable.MutableLong
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals
import kotlin.test.assertTrue

class LangExtrasTest {
    @Test
    fun mutableNumericHolders() {
        val i = MutableInt(2)
        assertEquals(3, i.increment().get())
        assertEquals(2, i.decrement().get())
        assertEquals(7, i.add(5).get())
        assertEquals(4, i.subtract(3).get())
        assertEquals(0, MutableInt(4).compareTo(MutableInt(4)))

        val l = MutableLong(10L)
        assertEquals(20L, l.add(10L).get())
        assertEquals(20.0, MutableDouble(10.0).add(10.0).get())
        assertTrue(MutableBool().toggle().get())
    }

    @Test
    fun generatorsProduceDistinctValues() {
        val uuid = UUIDGenerator()
        assertNotEquals(uuid.next(), uuid.next())

        val objectId = ObjectIdGenerator()
        assertEquals(24, objectId.next().length)

        val snowflake = SnowflakeGenerator(1, 1)
        val first = snowflake.next()
        val second = snowflake.next()
        assertTrue(second > first)
    }
}
