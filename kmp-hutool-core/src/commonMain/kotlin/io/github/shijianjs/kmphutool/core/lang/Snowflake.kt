package io.github.shijianjs.kmphutool.core.lang

import kotlin.time.Clock
import kotlin.time.ExperimentalTime

/**
 * Twitter's Snowflake id generator: sign(1) - timestamp(41) - datacenter(5) - worker(5) - sequence(12).
 *
 * Port of `cn.hutool.core.lang.Snowflake`. Kotlin common code has no `synchronized` and no
 * `SystemClock`, so:
 * - the epoch is a plain epoch-millis [Long] instead of `java.util.Date`;
 * - time comes from `kotlinx-datetime` [Clock];
 * - [nextId] is **not** internally synchronized — share one instance per thread, or wrap it
 *   externally where the target platform offers a lock.
 *
 * @hutool cn.hutool.core.lang.Snowflake
 */
class Snowflake(
    epochMillis: Long = DEFAULT_TWEPOCH,
    workerId: Long = DEFAULT_WORKER_ID,
    dataCenterId: Long = DEFAULT_DATA_CENTER_ID,
    private val useSystemClock: Boolean = true,
    private val timeOffset: Long = DEFAULT_TIME_OFFSET,
    private val randomSequenceLimit: Long = 0,
) {
    private val twepoch: Long = epochMillis
    val workerId: Long
    val dataCenterId: Long
    private var sequence = 0L
    private var lastTimestamp = -1L

    init {
        require(workerId in 0..MAX_WORKER_ID) { "workerId must be in [0, $MAX_WORKER_ID]" }
        require(dataCenterId in 0..MAX_DATA_CENTER_ID) { "dataCenterId must be in [0, $MAX_DATA_CENTER_ID]" }
        require(randomSequenceLimit in 0..SEQUENCE_MASK) { "randomSequenceLimit must be in [0, $SEQUENCE_MASK]" }
        this.workerId = workerId
        this.dataCenterId = dataCenterId
    }

    /** The worker id encoded in [id]. */
    fun getWorkerId(id: Long): Long = (id shr WORKER_ID_SHIFT.toInt()) and MAX_WORKER_ID

    /** The datacenter id encoded in [id]. */
    fun getDataCenterId(id: Long): Long = (id shr DATA_CENTER_ID_SHIFT.toInt()) and MAX_DATA_CENTER_ID

    /** The generation time of [id], in epoch millis. */
    fun getGenerateDateTime(id: Long): Long = ((id shr TIMESTAMP_LEFT_SHIFT.toInt()) and TIMESTAMP_MASK) + twepoch

    /** Alias of [getGenerateDateTime]. */
    fun timestamp(id: Long): Long = getGenerateDateTime(id)

    /** Id range covering `[timestampStart, timestampEnd]`, ignoring datacenter and worker bits. */
    fun getIdScopeByTimestamp(timestampStart: Long, timestampEnd: Long): Pair<Long, Long> =
        getIdScopeByTimestamp(timestampStart, timestampEnd, true)

    /** Id range covering `[timestampStart, timestampEnd]`. */
    fun getIdScopeByTimestamp(timestampStart: Long, timestampEnd: Long, ignoreCenterAndWorker: Boolean): Pair<Long, Long> {
        val startTimeMinId = (timestampStart - twepoch) shl TIMESTAMP_LEFT_SHIFT.toInt()
        val endTimeMinId = (timestampEnd - twepoch) shl TIMESTAMP_LEFT_SHIFT.toInt()
        return if (ignoreCenterAndWorker) {
            Pair(startTimeMinId, endTimeMinId or TIMESTAMP_MASK)
        } else {
            val base = (dataCenterId shl DATA_CENTER_ID_SHIFT.toInt()) or (workerId shl WORKER_ID_SHIFT.toInt())
            Pair(startTimeMinId or base, endTimeMinId or base or SEQUENCE_MASK)
        }
    }

    /** Generates the next id. */
    fun nextId(): Long {
        var timestamp = genTime()
        if (timestamp < lastTimestamp) {
            if (lastTimestamp - timestamp < timeOffset) {
                timestamp = lastTimestamp
            } else {
                throw IllegalStateException("Clock moved backwards. Refusing to generate id for ${lastTimestamp - timestamp}ms")
            }
        }
        if (timestamp == lastTimestamp) {
            val next = (sequence + 1) and SEQUENCE_MASK
            if (next == 0L) timestamp = tilNextMillis(lastTimestamp)
            sequence = next
        } else {
            sequence = if (randomSequenceLimit > 1) kotlin.random.Random.nextLong(randomSequenceLimit) else 0L
        }
        lastTimestamp = timestamp
        return ((timestamp - twepoch) shl TIMESTAMP_LEFT_SHIFT.toInt()) or
            (dataCenterId shl DATA_CENTER_ID_SHIFT.toInt()) or
            (workerId shl WORKER_ID_SHIFT.toInt()) or
            sequence
    }

    /** Generates the next id as a string. */
    fun nextIdStr(): String = nextId().toString()

    private fun tilNextMillis(lastTimestamp: Long): Long {
        var timestamp = genTime()
        while (timestamp == lastTimestamp) timestamp = genTime()
        if (timestamp < lastTimestamp) {
            throw IllegalStateException("Clock moved backwards. Refusing to generate id for ${lastTimestamp - timestamp}ms")
        }
        return timestamp
    }

    @OptIn(ExperimentalTime::class)
    private fun genTime(): Long = Clock.System.now().toEpochMilliseconds()

    companion object {
        /** Default epoch: Thu, 04 Nov 2010 01:42:54 GMT. */
        const val DEFAULT_TWEPOCH: Long = 1288834974657L
        /** Default tolerated clock rollback, 2 s. */
        const val DEFAULT_TIME_OFFSET: Long = 2000L
        private const val WORKER_ID_BITS = 5L
        const val MAX_WORKER_ID: Long = 31
        private const val DATA_CENTER_ID_BITS = 5L
        const val MAX_DATA_CENTER_ID: Long = 31
        private const val SEQUENCE_BITS = 12L
        private const val WORKER_ID_SHIFT = SEQUENCE_BITS
        private const val DATA_CENTER_ID_SHIFT = SEQUENCE_BITS + WORKER_ID_BITS
        private const val TIMESTAMP_LEFT_SHIFT = SEQUENCE_BITS + WORKER_ID_BITS + DATA_CENTER_ID_BITS
        private const val SEQUENCE_MASK = 4095L
        private const val TIMESTAMP_MASK = (1L shl 41) - 1
        const val DEFAULT_WORKER_ID: Long = 0
        const val DEFAULT_DATA_CENTER_ID: Long = 0

        /** Creates a generator with worker and datacenter ids derived from the current clock. */
        fun auto(): Snowflake {
            val dataCenterId = io.github.shijianjs.kmphutool.core.util.IdUtil.getDataCenterId(MAX_DATA_CENTER_ID)
            val workerId = io.github.shijianjs.kmphutool.core.util.IdUtil.getWorkerId(dataCenterId, MAX_WORKER_ID)
            return Snowflake(workerId = workerId, dataCenterId = dataCenterId)
        }
    }
}




