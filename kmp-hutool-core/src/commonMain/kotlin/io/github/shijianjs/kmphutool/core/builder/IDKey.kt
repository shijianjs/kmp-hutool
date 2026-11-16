package io.github.shijianjs.kmphutool.core.builder

/**
 * Wraps an object so only the same instance compares equal, disambiguating hash collisions.
 *
 * Port of `cn.hutool.core.builder.IDKey` (Commons Lang lineage). Java uses
 * `System.identityHashCode`; `commonMain` has no such call, so [Any.hashCode] stands in and the
 * identity check in [equals] still rules out collisions.
 *
 * @hutool cn.hutool.core.builder.IDKey
 */
internal class IDKey(private val value: Any) {
    private val id: Int = value.hashCode()

    override fun hashCode(): Int = id

    override fun equals(other: Any?): Boolean =
        other is IDKey && id == other.id && value === other.value
}
