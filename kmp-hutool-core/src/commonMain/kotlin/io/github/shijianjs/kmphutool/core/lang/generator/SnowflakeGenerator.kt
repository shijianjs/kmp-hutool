package io.github.shijianjs.kmphutool.core.lang.generator

import io.github.shijianjs.kmphutool.core.lang.Snowflake

/**
 * Generates snowflake ids. Keep one instance per worker: the generator is stateful and two instances
 * with the same ids can collide, exactly as upstream warns.
 *
 * Port of `cn.hutool.core.lang.generator.SnowflakeGenerator`.
 *
 * @hutool cn.hutool.core.lang.generator.SnowflakeGenerator
 */
class SnowflakeGenerator(workerId: Long = 0, datacenterId: Long = 0) : Generator<Long> {
    private val snowflake = Snowflake(workerId = workerId, dataCenterId = datacenterId)

    override fun next(): Long = snowflake.nextId()
}

