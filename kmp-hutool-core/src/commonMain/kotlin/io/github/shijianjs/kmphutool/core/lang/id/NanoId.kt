package io.github.shijianjs.kmphutool.core.lang.id

import io.github.shijianjs.kmphutool.core.util.RandomUtil
import kotlin.random.Random

/**
 * NanoId — a compact, URL-safe unique id generator.
 *
 * Port of `cn.hutool.core.lang.id.NanoId` (ai/nanoid lineage). Uses Kotlin's [Random] instead of
 * `SecureRandom`; pass your own generator when cryptographic strength matters on your platform.
 *
 * @hutool cn.hutool.core.lang.id.NanoId
 */
object NanoId {
    /** Default id length. */
    const val DEFAULT_SIZE: Int = 21

    /** URL-safe Base64 alphabet. */
    private val DEFAULT_ALPHABET =
        "_-0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ".toCharArray()

    /** A NanoId of [DEFAULT_SIZE] characters. */
    fun randomNanoId(): String = randomNanoId(DEFAULT_SIZE)

    /** A NanoId of [size] characters. */
    fun randomNanoId(size: Int): String = randomNanoId(null, null, size)

    /**
     * A NanoId of [size] characters drawn from [alphabet] with [random].
     *
     * [alphabet] must hold 1..255 symbols; indices that fall outside it are skipped, exactly like
     * upstream.
     */
    fun randomNanoId(random: Random? = null, alphabet: CharArray? = null, size: Int = DEFAULT_SIZE): String {
        val rnd = random ?: RandomUtil.getRandom()
        val chars = alphabet ?: DEFAULT_ALPHABET
        require(chars.isNotEmpty() && chars.size < 256) { "Alphabet must contain between 1 and 255 symbols." }
        require(size > 0) { "Size must be greater than zero." }

        val mask = (2 shl (kotlin.math.floor(kotlin.math.log2(chars.size - 1.toDouble())).toInt())) - 1
        val step = kotlin.math.ceil(1.6 * mask * size / chars.size).toInt()

        val idBuilder = StringBuilder()
        while (true) {
            for (i in 0 until step) {
                val alphabetIndex = rnd.nextInt(256) and mask
                if (alphabetIndex < chars.size) {
                    idBuilder.append(chars[alphabetIndex])
                    if (idBuilder.length == size) return idBuilder.toString()
                }
            }
        }
    }
}
