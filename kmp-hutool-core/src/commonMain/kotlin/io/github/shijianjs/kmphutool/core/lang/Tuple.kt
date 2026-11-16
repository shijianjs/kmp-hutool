package io.github.shijianjs.kmphutool.core.lang

/**
 * An immutable ordered group of values of mixed types, the Hutool tuple.
 *
 * Port of `cn.hutool.core.lang.Tuple`. Kotlin already has [Pair] and [Triple] for two and three
 * values; [Tuple] exists for the general case and for code shared with Hutool. Upstream's
 * `Tuple1`..`Tuple3` subclasses are not ported: [Pair]/[Triple] cover them with less ceremony.
 *
 * @hutool cn.hutool.core.lang.Tuple
 */
class Tuple(vararg elements: Any?) : Iterable<Any?> {
    private val members: List<Any?> = elements.toList()

    /** The number of members. */
    val size: Int
        get() = members.size

    /** The member at [index]. */
    operator fun get(index: Int): Any? = members[index]

    /** The index of the first occurrence of [element], or `-1`. */
    fun indexOf(element: Any?): Int = members.indexOf(element)

    /** `true` when [element] is a member. */
    fun contains(element: Any?): Boolean = members.contains(element)

    /** The members as a list. */
    fun toList(): List<Any?> = members

    override fun iterator(): Iterator<Any?> = members.iterator()

    override fun equals(other: Any?): Boolean = other is Tuple && members == other.members

    override fun hashCode(): Int = members.hashCode()

    override fun toString(): String = members.joinToString(prefix = "(", postfix = ")", separator = ", ")

    companion object {
        /** A tuple of [elements]. */
        fun of(vararg elements: Any?): Tuple = Tuple(*elements)
    }
}
