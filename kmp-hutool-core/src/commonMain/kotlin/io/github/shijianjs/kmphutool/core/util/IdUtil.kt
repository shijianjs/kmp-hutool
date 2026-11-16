package io.github.shijianjs.kmphutool.core.util

import kotlin.random.Random
import kotlin.time.Clock
import io.github.shijianjs.kmphutool.core.lang.Snowflake
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
    fun createSnowflake(workerId: Long, datacenterId: Long): Snowflake = Snowflake(workerId = workerId, dataCenterId = datacenterId)

    /** A shared snowflake generator; the first call initialises it from the current clock. */
    fun getSnowflake(workerId: Long, datacenterId: Long): Snowflake = sharedSnowflake(workerId = workerId, datacenterId = datacenterId)

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
            Snowflake.auto()
        } else {
            Snowflake(workerId = workerId, dataCenterId = datacenterId)
        }
        snowflake = created
        return created
    }

    private fun uuid(secure: Boolean): String {
        // commonMain has no CSPRNG; both paths use the shared source. Seeding per call (an earlier
        // version passed nowMillis()) made ids collide within the same millisecond.
        val random = Random.Default
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

