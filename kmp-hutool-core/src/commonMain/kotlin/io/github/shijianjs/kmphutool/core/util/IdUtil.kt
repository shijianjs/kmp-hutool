package io.github.shijianjs.kmphutool.core.util

import kotlin.random.Random
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

/**
 * Generators for the identifiers Hutool provides: UUIDs, Mongo-style object ids, snowflake ids and
 * short random ids.
 *
 * Port of `cn.hutool.core.util.IdUtil`. The Java class seeds `java.security.SecureRandom`; commonMain
 * has no CSPRNG, so [randomUUID] uses [Random.Default] and the KDoc says so — use a platform
 * cryptographic random when the id is a secret rather than a handle.
 *
 * @hutool cn.hutool.core.util.IdUtil
 */
object IdUtil {
    /** The `-` separated UUID text: 8-4-4-4-12 hex groups, version 4. */
    fun randomUUID(): String = uuid(secure = true)

    /** A UUID without the dashes: 32 hex characters. */
    fun simpleUUID(): String = uuid(secure = true).replace("-", "")

    /**
     * A UUID text built from a fast random source. The version/variant bits are still set, so the
     * result is a valid v4 UUID.
     */
    fun fastUUID(): String = uuid(secure = false)

    /** A dash-free UUID from the fast random source. */
    fun fastSimpleUUID(): String = uuid(secure = false).replace("-", "")

    /**
     * A 24-character hexadecimal object id: a 4-byte timestamp, 5 random bytes and a 3-byte counter,
     * the same layout MongoDB uses.
     */
    fun objectId(): String {
        val timestamp = nowSeconds()
        val machine = Random.nextLong(0xFFFFFFFFFFL)
        val counter = nextCounter()
        val builder = StringBuilder(24)
        appendHex(builder, timestamp, 8)
        appendHex(builder, machine, 10)
        appendHex(builder, counter, 6)
        return builder.toString()
    }

    /** A snowflake id generator with the given worker and datacenter ids. */
    fun createSnowflake(workerId: Long, datacenterId: Long): Snowflake = Snowflake(workerId, datacenterId)

    /** A shared snowflake generator; the first call initialises it from the current clock. */
    fun getSnowflake(workerId: Long, datacenterId: Long): Snowflake = sharedSnowflake(workerId, datacenterId)

    /** A shared snowflake generator with a derived datacenter id. */
    fun getSnowflake(workerId: Long): Snowflake = sharedSnowflake(workerId, getDataCenterId(MAX_DATACENTER_ID))

    /** The shared snowflake generator with ids derived from the clock. */
    fun getSnowflake(): Snowflake = sharedSnowflake(null, null)

    /** A datacenter id in `0..maxDatacenterId` derived from the current time. */
    fun getDataCenterId(maxDatacenterId: Long): Long {
        require(maxDatacenterId > 0) { "maxDatacenterId must be greater than 0" }
        val id = (nowMillis() % (maxDatacenterId + 1))
        return id
    }

    /** A worker id in `0..maxWorkerId` derived from [datacenterId] and the current time. */
    fun getWorkerId(datacenterId: Long, maxWorkerId: Long): Long {
        require(maxWorkerId > 0) { "maxWorkerId must be greater than 0" }
        val id = ((datacenterId shl 5) or (nowMillis() % 32)) % (maxWorkerId + 1)
        return id
    }

    /** The next id from the shared snowflake generator. */
    fun getSnowflakeNextId(): Long = getSnowflake().nextId()

    /** The next id from the shared snowflake generator, as text. */
    fun getSnowflakeNextIdStr(): String = getSnowflakeNextId().toString()

    /** A short random id of the default length (21). */
    fun nanoId(): String = nanoId(21)

    /**
     * A random id of [size] characters from the URL-safe alphabet NanoID uses
     * (`A-Z a-z 0-9 _ -`).
     */
    fun nanoId(size: Int): String {
        require(size > 0) { "Size must be greater than 0" }
        val alphabet = "_-0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ"
        val builder = StringBuilder(size)
        repeat(size) { builder.append(alphabet[Random.nextInt(alphabet.length)]) }
        return builder.toString()
    }

    // ---------------------------------------------------------------------------------------------

    private const val MAX_DATACENTER_ID = 31L

    private var snowflake: Snowflake? = null

    private fun sharedSnowflake(workerId: Long?, datacenterId: Long?): Snowflake {
        val existing = snowflake
        if (existing != null && (workerId == null || existing.workerId == workerId)) return existing
        val created = if (workerId == null || datacenterId == null) {
            Snowflake.createAuto()
        } else {
            Snowflake(workerId, datacenterId)
        }
        snowflake = created
        return created
    }

    private fun uuid(secure: Boolean): String {
        val random = if (secure) Random else Random(nowMillis())
        val high = random.nextLong()
        val low = random.nextLong()
        // version 4 (random) in the high word, the RFC 4122 variant in the low word.
        val versionedHigh = (high and 0xF000L.inv()) or 0x4000L
        val variantLow = (low and 0x3FFFFFFFFFFFFFFFL) or Long.MIN_VALUE
        return formatUuid(versionedHigh, variantLow)
    }

    private fun formatUuid(high: Long, low: Long): String {
        val hex = high.toULong().toString(16).padStart(16, '0') + low.toULong().toString(16).padStart(16, '0')
        return buildString(36) {
            append(hex, 0, 8).append('-')
            append(hex, 8, 12).append('-')
            append(hex, 12, 16).append('-')
            append(hex, 16, 20).append('-')
            append(hex, 20, 32)
        }
    }

    private fun appendHex(builder: StringBuilder, value: Long, width: Int) {
        val hex = value.toULong().toString(16).takeLast(width).padStart(width, '0')
        builder.append(hex)
    }

    private fun appendHex(builder: StringBuilder, value: Int, width: Int) = appendHex(builder, value.toLong(), width)

    private var counter = Random.nextInt(0xFFFFFF)

    private fun nextCounter(): Int {
        counter = (counter + 1) and 0xFFFFFF
        return counter
    }

    @OptIn(ExperimentalTime::class)
    private fun nowMillis(): Long = Clock.System.now().toEpochMilliseconds()

    private fun nowSeconds(): Long = nowMillis() / 1000
}

/**
 * A Twitter-style snowflake generator: 41 bits of timestamp, 5 bits of datacenter id, 5 bits of
 * worker id and 12 bits of sequence.
 *
 * Port of `cn.hutool.core.util.IdUtil`'s inner `Snowflake` class. The clock is read through
 * `kotlin.time.Clock`, and waiting for the next millisecond is a busy loop bounded by the sequence
 * width — the same behaviour as upstream.
 */
class Snowflake(val workerId: Long, val datacenterId: Long) {
    private var sequence = 0L
    private var lastTimestamp = -1L

    init {
        require(workerId in 0..MAX_WORKER_ID) { "workerId must be in 0..$MAX_WORKER_ID" }
        require(datacenterId in 0..MAX_DATACENTER_ID) { "datacenterId must be in 0..$MAX_DATACENTER_ID" }
    }

    /** The next id; blocks (busy-waits) when more than 4096 ids are requested in one millisecond. */
    fun nextId(): Long {
        var timestamp = currentMillis()
        if (timestamp < lastTimestamp) {
            // The clock moved backwards; wait it out instead of emitting an out-of-order id.
            timestamp = waitUntil(lastTimestamp)
        }
        if (timestamp == lastTimestamp) {
            sequence = (sequence + 1) and SEQUENCE_MASK
            if (sequence == 0L) timestamp = waitUntil(lastTimestamp + 1)
        } else {
            sequence = 0L
        }
        lastTimestamp = timestamp
        return ((timestamp - START_EPOCH) shl TIMESTAMP_SHIFT) or
            (datacenterId shl DATACENTER_SHIFT) or
            (workerId shl WORKER_SHIFT) or
            sequence
    }

    /** The timestamp encoded in [id]. */
    fun timestamp(id: Long): Long = (id shr TIMESTAMP_SHIFT) + START_EPOCH

    private fun waitUntil(minTimestamp: Long): Long {
        var timestamp = currentMillis()
        while (timestamp < minTimestamp) timestamp = currentMillis()
        return timestamp
    }

    private fun currentMillis(): Long = IdUtil.currentTimeMillisForSnowflake()

    companion object {
        private const val TIMESTAMP_SHIFT = 22
        private const val DATACENTER_SHIFT = 17
        private const val WORKER_SHIFT = 12
        private const val SEQUENCE_MASK = 0xFFFL
        private const val MAX_WORKER_ID = 31L
        private const val MAX_DATACENTER_ID = 31L

        /** 2010-11-04T09:42:54.657Z, the epoch Hutool's snowflake uses. */
        internal const val START_EPOCH = 1288834974657L

        /** Creates a generator with worker and datacenter ids derived from the clock. */
        fun createAuto(): Snowflake {
            val datacenterId = IdUtil.getDataCenterId(MAX_DATACENTER_ID)
            val workerId = IdUtil.getWorkerId(datacenterId, MAX_WORKER_ID)
            return Snowflake(workerId, datacenterId)
        }
    }
}

@OptIn(ExperimentalTime::class)
internal fun IdUtil.currentTimeMillisForSnowflake(): Long = Clock.System.now().toEpochMilliseconds()
