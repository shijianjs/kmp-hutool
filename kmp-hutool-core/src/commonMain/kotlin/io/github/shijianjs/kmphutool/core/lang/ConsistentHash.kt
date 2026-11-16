package io.github.shijianjs.kmphutool.core.lang

/**
 * Consistent hashing ring with virtual nodes.
 *
 * Port of `cn.hutool.core.lang.ConsistentHash`. `SortedMap` is not available in `commonMain`, so the
 * ring is an `Int`-keyed map scanned for the successor key — fine for the handful of virtual nodes a
 * ring actually holds. The default hash is FNV-1, as upstream.
 *
 * @hutool cn.hutool.core.lang.ConsistentHash
 */
class ConsistentHash<T>(
    private val numberOfReplicas: Int,
    nodes: Collection<T>,
    private val hashFunc: (Any) -> Int = { fnvHash(it.toString()) },
) {
    private val circle = mutableMapOf<Int, T>()

    init {
        nodes.forEach { add(it) }
    }

    /** Adds a node together with its [numberOfReplicas] virtual nodes. */
    fun add(node: T) {
        for (i in 0 until numberOfReplicas) {
            circle[hashFunc(node.toString() + i)] = node
        }
    }

    /** Removes a node and its virtual nodes. */
    fun remove(node: T) {
        for (i in 0 until numberOfReplicas) {
            circle.remove(hashFunc(node.toString() + i))
        }
    }

    /** The node owning [key]: exact match, else the next clockwise virtual node. */
    fun get(key: Any): T? {
        if (circle.isEmpty()) return null
        val hash = hashFunc(key)
        circle[hash]?.let { return it }
        val successor = circle.keys.filter { it >= hash }.minOrNull() ?: circle.keys.min()
        return circle[successor]
    }

    companion object {
        /** FNV-1 32-bit hash, matching Hutool's default ring hash. */
        fun fnvHash(text: String): Int {
            var hash = 0x811C9DC5.toInt()
            for (ch in text) {
                hash = hash * 0x01000193
                hash = hash xor ch.code
            }
            return hash
        }
    }
}
