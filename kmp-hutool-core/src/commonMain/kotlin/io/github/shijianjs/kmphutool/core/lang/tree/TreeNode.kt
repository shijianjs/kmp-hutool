package io.github.shijianjs.kmphutool.core.lang.tree

/**
 * A tree node: id, parent id, display name and sort weight.
 *
 * Port of `cn.hutool.core.lang.tree.TreeNode`. The Java getter/setter pairs become Kotlin
 * properties; [parentId] is mutable so a node can be re-parented.
 *
 * @hutool cn.hutool.core.lang.tree.TreeNode
 */
class TreeNode<T>(
    override var id: T? = null,
    override var parentId: T? = null,
    override var name: CharSequence? = null,
    override var weight: Comparable<*>? = 0,
) : Node<T> {
    /** Arbitrary extra fields carried with the node. */
    var extra: MutableMap<String, Any?>? = null

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is TreeNode<*>) return false
        return id == other.id
    }

    override fun hashCode(): Int = id?.hashCode() ?: 0

    override fun toString(): String = "TreeNode(id=$id, parentId=$parentId, name=$name, weight=$weight)"
}
