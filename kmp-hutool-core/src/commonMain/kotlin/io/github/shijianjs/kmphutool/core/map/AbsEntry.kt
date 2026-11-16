package io.github.shijianjs.kmphutool.core.map

/**
 * Base class for read-only map entries with value semantics, adapted from Guava.
 *
 * Port of `cn.hutool.core.map.AbsEntry`. [setValue] throws [UnsupportedOperationException] unless a
 * subclass overrides it; `equals`, `hashCode` and `toString` follow `Map.Entry`'s contract.
 *
 * @hutool cn.hutool.core.map.AbsEntry
 */
abstract class AbsEntry<K, V> : MutableMap.MutableEntry<K, V> {
    override fun setValue(newValue: V): V =
        throw UnsupportedOperationException("Entry is read only.")

    override fun equals(other: Any?): Boolean =
        other is Map.Entry<*, *> && key == other.key && value == other.value

    override fun hashCode(): Int = (key?.hashCode() ?: 0) xor (value?.hashCode() ?: 0)

    override fun toString(): String = "$key=$value"
}
