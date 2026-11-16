package io.github.shijianjs.kmphutool.core.util

import kotlin.random.Random

/**
 * Random values for tests, demo data and placeholders.
 *
 * Port of `cn.hutool.core.util.RandomUtil`. The Java class offers `SecureRandom` factories and random
 * dates; commonMain has neither a CSPRNG nor `java.util.Date`, so [createSecureRandom] / `randomDate`
 * and the `weightRandom` overloads typed on upstream collections are not ported.
 *
 * @hutool cn.hutool.core.util.RandomUtil
 */
object RandomUtil {
    /** A general-purpose random source. */
    fun getRandom(): Random = Random.Default

    /** A random int in `[min, max)`. */
    fun randomInt(min: Int, max: Int): Int {
        require(max > min) { "max must be greater than min" }
        return Random.nextInt(min, max)
    }

    /** A non-negative random int. */
    fun randomInt(): Int = Random.nextInt()

    /** A random long in `[0, Long.MAX_VALUE)`; Hutool's `randomLong` is always non-negative. */
    fun randomLong(): Long = Random.nextLong(0L, Long.MAX_VALUE)

    /** A random double in `[0, 1)`. */
    fun randomDouble(): Double = Random.nextDouble()

    /** A random double in `[min, max)`. */
    fun randomDouble(min: Double, max: Double): Double {
        require(max > min) { "max must be greater than min" }
        return min + Random.nextDouble() * (max - min)
    }

    /** A random float in `[0, 1)`. */
    fun randomFloat(): Float = Random.nextFloat()

    /** A random float in `[min, max)`. */
    fun randomFloat(min: Float, max: Float): Float {
        require(max > min) { "max must be greater than min" }
        return min + Random.nextFloat() * (max - min)
    }

    /** `true` or `false`. */
    fun randomBoolean(): Boolean = Random.nextBoolean()

    /** [length] random bytes. */
    fun randomBytes(length: Int): ByteArray = Random.nextBytes(length)

    /** A random string of [length] characters from `0-9a-zA-Z`. */
    fun randomString(length: Int): String = buildString(length) {
        repeat(length) { append(BASE_CHARS[Random.nextInt(BASE_CHARS.length)]) }
    }

    /** A random string of [length] upper-case letters. */
    fun randomStringUpper(length: Int): String = buildString(length) {
        repeat(length) { append(UPPER_CHARS[Random.nextInt(UPPER_CHARS.length)]) }
    }

    /** A random string of [length] lower-case letters. */
    fun randomStringLower(length: Int): String = buildString(length) {
        repeat(length) { append(LOWER_CHARS[Random.nextInt(LOWER_CHARS.length)]) }
    }

    /** A random string of [length] characters, avoiding every character in [exclude]. */
    fun randomStringWithoutStr(length: Int, exclude: String): String {
        val charset = BASE_CHARS.filter { !exclude.contains(it) }
        require(charset.isNotEmpty()) { "The excluded characters cover the whole alphabet" }
        return buildString(length) { repeat(length) { append(charset[Random.nextInt(charset.length)]) } }
    }

    /** A random number with [length] digits, no leading zero. */
    fun randomNumber(length: Int): String {
        require(length > 0) { "Length must be greater than 0" }
        return buildString(length) {
            append(randomInt(1, 10))
            repeat(length - 1) { append(randomInt(0, 10)) }
        }
    }

    /** [length] random digits, each in `0..9`. */
    fun randomNumbers(length: Int): List<Int> = (0 until length).map { randomInt(0, 10) }

    /** A random element of [collection], `null` when it is empty. */
    fun <T> randomEle(collection: Collection<T>?): T? {
        if (collection == null || collection.isEmpty()) return null
        val index = Random.nextInt(collection.size)
        return collection.elementAt(index)
    }

    /** [count] random elements of [collection], possibly repeated. */
    fun <T> randomEleList(collection: Collection<T>?, count: Int): List<T> {
        if (collection == null || collection.isEmpty() || count <= 0) return emptyList()
        return (0 until count).mapNotNull { randomEle(collection) }
    }

    /** [count] distinct random elements of [collection], capped at its size. */
    fun <T> randomEles(collection: Collection<T>?, count: Int): Set<T> {
        if (collection == null || collection.isEmpty() || count <= 0) return emptySet()
        val pool = collection.shuffled().take(count)
        return pool.toSet()
    }

    /** Picks a random key from [weights] with probability proportional to its value. */
    fun <K> weightRandom(weights: Map<K, Int>): K? {
        if (weights.isEmpty()) return null
        val total = weights.values.sum()
        if (total <= 0) return null
        var target = Random.nextInt(total)
        for ((key, weight) in weights) {
            target -= weight
            if (target < 0) return key
        }
        return weights.keys.lastOrNull()
    }

    private const val BASE_CHARS = "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ"
    private const val UPPER_CHARS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ"
    private const val LOWER_CHARS = "abcdefghijklmnopqrstuvwxyz"
}
