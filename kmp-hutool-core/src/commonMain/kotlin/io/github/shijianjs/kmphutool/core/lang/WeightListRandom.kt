package io.github.shijianjs.kmphutool.core.lang

/**
 * A weighted random pool backed by a list of cumulative weights.
 *
 * Port of `cn.hutool.core.lang.WeightListRandom`; `ThreadLocalRandom` is replaced by Kotlin's
 * `Random`, which is the only randomness source common code has.
 *
 * @hutool cn.hutool.core.lang.WeightListRandom
 */
class WeightListRandom<E> {
    private class Entry<E>(val element: E, var sumWeight: Double)

    private val randomPool: MutableList<Entry<E>>

    constructor() {
        randomPool = ArrayList()
    }

    constructor(poolSize: Int) {
        randomPool = ArrayList(poolSize)
    }

    /** Adds [element] with a positive [weight]. */
    fun add(element: E, weight: Double) {
        require(weight > 0) { "权重必须大于0！" }
        randomPool.add(Entry(element, sumWeight() + weight))
    }

    /** Removes the first occurrence of [element] and shifts the cumulative weights after it. */
    fun remove(element: E): Boolean {
        var removed = false
        var weight = 0.0
        var index = 0
        val iterator = randomPool.iterator()
        while (iterator.hasNext()) {
            val entry = iterator.next()
            if (!removed && entry.element == element) {
                iterator.remove()
                weight = entry.sumWeight - (if (index == 0) 0.0 else randomPool[index - 1].sumWeight)
                removed = true
            }
            if (removed) entry.sumWeight -= weight
            index++
        }
        return removed
    }

    /** Picks one element by weight. */
    fun next(): E {
        checkEmptyPool()
        if (randomPool.size == 1) return randomPool[0].element
        val randValue = kotlin.random.Random.nextDouble() * sumWeight()
        return binarySearch(randValue)
    }

    /** Picks one element, requiring the caller's [weight] to cover the whole pool. */
    fun randomByWeight(weight: Double): E? {
        require(weight >= sumWeight()) { "权重必须大于当前总权重！" }
        val randValue = kotlin.random.Random.nextDouble() * sumWeight()
        if (randValue > sumWeight()) return null
        return binarySearch(randValue)
    }

    /** Whether the pool has no element. */
    fun isEmpty(): Boolean = randomPool.isEmpty()

    /** Finds the element whose cumulative weight is the largest value `<= key`. */
    private fun binarySearch(key: Double): E {
        var low = 0
        var high = randomPool.size - 1
        while (low <= high) {
            val mid = (low + high) ushr 1
            val midValue = randomPool[mid].sumWeight
            when {
                midValue < key -> low = mid + 1
                midValue > key -> high = mid - 1
                else -> return randomPool[mid].element
            }
        }
        return randomPool[low].element
    }

    private fun sumWeight(): Double = if (randomPool.isEmpty()) 0.0 else randomPool[randomPool.size - 1].sumWeight

    private fun checkEmptyPool() {
        if (isEmpty()) throw IllegalArgumentException("随机池为空！")
    }
}
