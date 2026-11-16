package io.github.shijianjs.kmphutool.core.lang.id

import io.github.shijianjs.kmphutool.core.lang.Snowflake
import io.github.shijianjs.kmphutool.core.util.IdUtil

/**
 * Default Snowflake ids derived from the clock.
 *
 * Port of `cn.hutool.core.lang.id.IdConstants`. Upstream mixes the MAC address and process id;
 * commonMain has neither, so the defaults are derived from the current time exactly like
 * [IdUtil.getDataCenterId] / [IdUtil.getWorkerId].
 *
 * @hutool cn.hutool.core.lang.id.IdConstants
 */
object IdConstants {
    /** The default datacenter id, within `0..Snowflake.MAX_DATA_CENTER_ID`. */
    val DEFAULT_DATACENTER_ID: Long = IdUtil.getDataCenterId(Snowflake.MAX_DATA_CENTER_ID)

    /** The default worker id, within `0..Snowflake.MAX_WORKER_ID`. */
    val DEFAULT_WORKER_ID: Long = IdUtil.getWorkerId(DEFAULT_DATACENTER_ID, Snowflake.MAX_WORKER_ID)

    /** A shared generator using the defaults. */
    val DEFAULT_SNOWFLAKE: Snowflake = Snowflake(
        workerId = DEFAULT_WORKER_ID,
        dataCenterId = DEFAULT_DATACENTER_ID,
    )
}
