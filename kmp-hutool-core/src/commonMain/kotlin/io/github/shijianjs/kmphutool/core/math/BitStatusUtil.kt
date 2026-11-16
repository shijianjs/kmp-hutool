package io.github.shijianjs.kmphutool.core.math

/**
 * Represents a set of independent flags as bits of one `Int`.
 *
 * Port of `cn.hutool.core.math.BitStatusUtil`. Every argument must be even and non-negative, since
 * each status occupies one bit and `0` marks "no status".
 *
 * @hutool cn.hutool.core.math.BitStatusUtil
 */
object BitStatusUtil {
    /** Adds a status. */
    fun add(states: Int, stat: Int): Int {
        check(states, stat)
        return states or stat
    }

    /** `true` when [stat] is present in [states]. */
    fun has(states: Int, stat: Int): Boolean {
        check(states, stat)
        return (states and stat) == stat
    }

    /** Removes a status, leaving [states] untouched when it is absent. */
    fun remove(states: Int, stat: Int): Int {
        check(states, stat)
        return if (has(states, stat)) states xor stat else states
    }

    /** The empty status set. */
    fun clear(): Int = 0

    private fun check(vararg args: Int) {
        for (arg in args) {
            require(arg >= 0) { "$arg 必须大于等于0" }
            require(arg and 1 == 0) { "$arg 不是偶数" }
        }
    }
}
