package io.github.shijianjs.kmphutool.core.lang.tree

/**
 * Common node surface for tree building and sorting.
 *
 * Port of `cn.hutool.core.lang.tree.Node` as properties, which is the Kotlin idiom for the Java
 * getter/setter pairs.
 *
 * @hutool cn.hutool.core.lang.tree.Node
 */
interface Node<T> : Comparable<Node<T>> {
    var id: T?
    var parentId: T?
    var name: CharSequence?
    var weight: Comparable<*>?

    override fun compareTo(other: Node<T>): Int {
        val mine = weight
        val theirs = other.weight
        if (mine == null && theirs == null) return 0
        if (mine == null) return -1
        if (theirs == null) return 1
        @Suppress("UNCHECKED_CAST")
        return (mine as Comparable<Any?>).compareTo(theirs)
    }
}
