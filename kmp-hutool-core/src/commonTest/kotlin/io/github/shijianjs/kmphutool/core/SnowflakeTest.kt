package io.github.shijianjs.kmphutool.core

import io.github.shijianjs.kmphutool.core.lang.Snowflake
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class SnowflakeTest {
    @Test
    fun encodesWorkerAndDatacenter() {
        val snowflake = Snowflake(workerId = 1, dataCenterId = 2)
        val id1 = snowflake.nextId()
        val id2 = snowflake.nextId()
        assertTrue(id2 > id1)
        assertEquals(1L, snowflake.getWorkerId(id1))
        assertEquals(2L, snowflake.getDataCenterId(id1))
        assertTrue(snowflake.getGenerateDateTime(id1) > Snowflake.DEFAULT_TWEPOCH)
    }

    @Test
    fun rejectsOutOfRangeIds() {
        val failure = runCatching { Snowflake(workerId = 32) }
        assertTrue(failure.isFailure)
    }

    @Test
    fun timestampScopeCoversIds() {
        val snowflake = Snowflake(workerId = 0, dataCenterId = 0)
        val now = snowflake.nextId()
        val time = snowflake.getGenerateDateTime(now)
        val (start, end) = snowflake.getIdScopeByTimestamp(time, time)
        assertTrue(now in start..end)
    }
}
