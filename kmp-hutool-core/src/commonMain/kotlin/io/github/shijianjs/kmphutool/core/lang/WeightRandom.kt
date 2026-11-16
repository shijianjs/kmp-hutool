package io.github.shijianjs.kmphutool.core.lang

import kotlin.math.max

/**
 * Random selection where each candidate carries a weight.
 *
 * Port of `cn.hutool.core.lang.WeightRandom`. The Java class asks for `Random`; the port uses a
 * [kotlin.random.Random] parameter so callers can seed a deterministic test.
 *
 * @hutool cn.hutool.core.lang.WeightRandom
 */
class WeightRandom<T> {
    private val weights = mutableListOf<WeightRandom.WeightObj<T>>()

    /** Adds [obj] with weight [weight]; a non-positive weight is ignored. */
    fun add(obj: T, weight: Int): WeightRandom<T> = apply {
        if (weight > 0) weights.add(WeightRandom.WeightObj(obj, weight))
    }

    /** Total weight of every candidate added so far. */
    fun totalWeight(): Int = weights.sumOf { it.weight }

    /** The number of candidates. */
    fun size(): Int = weights.size

    /** Picks a candidate with probability `weight / totalWeight`; `null` when nothing was added. */
    fun next(random: kotlin.random.Random = kotlin.random.Random.Default): T? {
        val total = totalWeight()
        if (total <= 0) return null
        var hit = random.nextInt(total)
        for (entry in weights) {
            hit -= entry.weight
            if (hit < 0) return entry.obj
        }
        return weights.lastOrNull()?.obj
    }

    /** Picks [count] candidates, each draw independent. */
    fun next(count: Int, random: kotlin.random.Random = kotlin.random.Random.Default): List<T> {
        if (count <= 0) return emptyList()
        return (0 until count).mapNotNull { next(random) }
    }

    /** Clears every candidate. */
    fun clear(): WeightRandom<T> = apply { weights.clear() }

    override fun toString(): String = "WeightRandom(weights=$weights)"

    /** One weighted candidate. */
    data class WeightObj<T>(
        /** The candidate. */
        val obj: T,
        /** Its relative weight. */
        val weight: Int,
    )

    companion object {
        /** A weight random over [weights]; non-positive weights are dropped. */
        fun <T> of(weights: Map<T, Int>): WeightRandom<T> = WeightRandom<T>().apply {
            weights.forEach { (obj, weight) -> add(obj, max(0, weight)) }
        }

        /** A weight random over the given candidates. */
        fun <T> of(vararg weights: WeightObj<T>): WeightRandom<T> = WeightRandom<T>().apply {
            weights.forEach { add(it.obj, it.weight) }
        }
    }
}
