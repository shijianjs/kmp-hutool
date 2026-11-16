package io.github.shijianjs.kmphutool.core.lang

/**
 * A 128-bit UUID value with Hutool's extra `isSimple` text form.
 *
 * Port of `cn.hutool.core.lang.UUID`. `java.security.SecureRandom` and `MessageDigest` do not exist
 * in commonMain, so [randomUUID] uses Kotlin's `Random` and [nameUUIDFromBytes] throws:
 * a name-based UUID must be produced with a platform MD5 digest.
 *
 * @hutool cn.hutool.core.lang.UUID
 */
class UUID private constructor(
    private val mostSigBits: Long,
    private val leastSigBits: Long,
) : Comparable<UUID> {
    /** The most significant 64 bits. */
    fun getMostSignificantBits(): Long = mostSigBits

    /** The least significant 64 bits. */
    fun getLeastSignificantBits(): Long = leastSigBits

    /** Version nibble (1 time-based, 3 name-based, 4 random). */
    fun version(): Int = ((mostSigBits shr 12) and 0x0f).toInt()

    /** Variant number. */
    fun variant(): Int = ((leastSigBits ushr (64 - (leastSigBits ushr 62)).toInt()) and (leastSigBits shr 63)).toInt()

    /** The 60-bit timestamp, only meaningful for version 1. */
    fun timestamp(): Long {
        checkTimeBase()
        return ((mostSigBits and 0x0FFFL) shl 48) or (((mostSigBits shr 16) and 0x0FFFFL) shl 32) or (mostSigBits ushr 32)
    }

    /** The 14-bit clock sequence, only meaningful for version 1. */
    fun clockSequence(): Int {
        checkTimeBase()
        return ((leastSigBits and 0x3FFF000000000000L) ushr 48).toInt()
    }

    /** The 48-bit node value, only meaningful for version 1. */
    fun node(): Long {
        checkTimeBase()
        return leastSigBits and 0x0000FFFFFFFFFFFFL
    }

    override fun toString(): String = toString(false)

    /** `8-4-4-4-12` groups, or the 32 hex characters without dashes when [isSimple]. */
    fun toString(isSimple: Boolean): String {
        val builder = StringBuilder(if (isSimple) 32 else 36)
        builder.append(digits(mostSigBits shr 32, 8))
        if (!isSimple) builder.append('-')
        builder.append(digits(mostSigBits shr 16, 4))
        if (!isSimple) builder.append('-')
        builder.append(digits(mostSigBits, 4))
        if (!isSimple) builder.append('-')
        builder.append(digits(leastSigBits shr 48, 4))
        if (!isSimple) builder.append('-')
        builder.append(digits(leastSigBits, 12))
        return builder.toString()
    }

    override fun hashCode(): Int {
        val hilo = mostSigBits xor leastSigBits
        return ((hilo shr 32).toInt()) xor hilo.toInt()
    }

    override fun equals(other: Any?): Boolean =
        other is UUID && mostSigBits == other.mostSigBits && leastSigBits == other.leastSigBits

    override fun compareTo(other: UUID): Int {
        val first = mostSigBits.compareTo(other.mostSigBits)
        return if (first != 0) first else leastSigBits.compareTo(other.leastSigBits)
    }

    private fun checkTimeBase() {
        if (version() != 1) throw UnsupportedOperationException("Not a time-based UUID")
    }

    companion object {
        /** Random version-4 UUID. */
        fun randomUUID(): UUID = randomUUID(true)

        /** Random version-4 UUID; `isSecure` is accepted for API parity but both paths use Kotlin's `Random`. */
        fun randomUUID(isSecure: Boolean): UUID {
            val randomBytes = ByteArray(16)
            kotlin.random.Random.nextBytes(randomBytes)
            randomBytes[6] = ((randomBytes[6].toInt() and 0x0f) or 0x40).toByte()
            randomBytes[8] = ((randomBytes[8].toInt() and 0x3f) or 0x80).toByte()
            return fromBytes(randomBytes)
        }

        /** A fast version-4 UUID. */
        fun fastUUID(): UUID = randomUUID(false)

        /** Name-based UUIDs need MD5, which commonMain does not provide. */
        fun nameUUIDFromBytes(name: ByteArray): UUID =
            throw UnsupportedOperationException("MD5 is not available in commonMain")

        /** Parses `xxxxxxxx-xxxx-xxxx-xxxx-xxxxxxxxxxxx`. */
        fun fromString(name: String): UUID {
            val components = name.split('-')
            require(components.size == 5) { "Invalid UUID string: $name" }
            val most = (components[0].toULong(16).toLong() shl 32) or
                (components[1].toULong(16).toLong() shl 16) or
                components[2].toULong(16).toLong()
            val least = (components[3].toULong(16).toLong() shl 48) or components[4].toULong(16).toLong()
            return UUID(most, least)
        }

        private fun fromBytes(data: ByteArray): UUID {
            require(data.size == 16) { "data must be 16 bytes in length" }
            var most = 0L
            var least = 0L
            for (i in 0 until 8) most = (most shl 8) or (data[i].toLong() and 0xff)
            for (i in 8 until 16) least = (least shl 8) or (data[i].toLong() and 0xff)
            return UUID(most, least)
        }

        private fun digits(value: Long, digits: Int): String {
            val hi = 1L shl (digits * 4)
            val masked = (hi or (value and (hi - 1))).toString(16)
            return masked.substring(1)
        }
    }
}

