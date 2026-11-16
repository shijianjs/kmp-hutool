package io.github.shijianjs.kmphutool.core.lang.tree

import io.github.shijianjs.kmphutool.core.lang.Filter

/**
 * A tree node backed by a string-keyed map, so nodes can carry arbitrary extra fields.
 *
 * Port of `cn.hutool.core.lang.tree.Tree`. Kotlin's `LinkedHashMap` cannot be subclassed, so the port
 * implements [MutableMap] by delegation, which keeps insertion order and the same map API.
 *
 * @hutool cn.hutool.core.lang.tree.Tree
 */
class Tree<T>(treeNodeConfig: TreeNodeConfig? = null) :
    Node<T>,
    MutableMap<String, Any?> by LinkedHashMap() {

    /** The key configuration used by this node. */
    val config: TreeNodeConfig = treeNodeConfig ?: TreeNodeConfig.DEFAULT_CONFIG

    /** Parent node when this node was attached through [addChildren] or [setParent]. */
    var parent: Tree<T>? = null
        private set

    override var id: T?
        get() = valueOf(config.idKey)
        set(value) {
            put(config.idKey, value)
        }

    override var parentId: T?
        get() = valueOf(config.parentIdKey)
        set(value) {
            put(config.parentIdKey, value)
        }

    override var name: CharSequence?
        get() = get(config.nameKey) as? CharSequence
        set(value) {
            put(config.nameKey, value)
        }

    override var weight: Comparable<*>?
        get() = get(config.weightKey) as? Comparable<*>
        set(value) {
            put(config.weightKey, value)
        }

    /** Child nodes, or `null` when the node is a leaf. */
    var children: MutableList<Tree<T>>?
        get() {
            @Suppress("UNCHECKED_CAST")
            return get(config.childrenKey) as? MutableList<Tree<T>>
        }
        set(value) {
            if (value == null) remove(config.childrenKey) else put(config.childrenKey, value)
        }

    /** Whether the node has at least one child. */
    fun hasChild(): Boolean = !children.isNullOrEmpty()

    /** Visits this node and every descendant, depth first. */
    fun walk(consumer: (Tree<T>) -> Unit) {
        consumer(this)
        children?.forEach { it.walk(consumer) }
    }

    /** Returns a filtered copy; `null` means the node and its subtree are dropped. */
    fun filterNew(filter: Filter<Tree<T>>): Tree<T>? = cloneTree().filter(filter)

    /** Filters this tree in place; `null` means nothing under this node survived. */
    fun filter(filter: Filter<Tree<T>>): Tree<T>? {
        if (filter.accept(this)) return this
        val current = children
        if (current != null) {
            val kept = ArrayList<Tree<T>>(current.size)
            for (child in current) child.filter(filter)?.let { kept.add(it) }
            if (kept.isNotEmpty()) {
                children = kept
                return this
            }
            children = null
        }
        return null
    }

    /** Attaches [children], re-parenting each of them. */
    fun addChildren(vararg children: Tree<T>): Tree<T> {
        if (children.isEmpty()) return this
        val list = this.children ?: ArrayList<Tree<T>>().also { this.children = it }
        for (child in children) {
            child.setParent(this)
            list.add(child)
        }
        return this
    }

    /** Sets the parent without touching the children list. */
    fun setParent(parent: Tree<T>?): Tree<T> {
        this.parent = parent
        if (parent != null) parentId = parent.id
        return this
    }

    /** Adds an extra field, rejecting a blank key. */
    fun putExtra(key: String, value: Any?) {
        require(key.isNotEmpty()) { "Key must be not empty !" }
        put(key, value)
    }

    /** Deep-copies the node structure; extra values are copied by reference. */
    fun cloneTree(): Tree<T> {
        val copy = Tree<T>(config)
        copy.putAll(this)
        copy.children = children?.map { it.cloneTree().setParent(copy) }?.toMutableList()
        return copy
    }

    /** Looks up a descendant by id. */
    fun getNode(id: T?): Tree<T>? = TreeUtil.getNode(this, id)

    /** Names from this node up to the root. */
    fun getParentsName(includeCurrentNode: Boolean): List<CharSequence?> = TreeUtil.getParentsName(this, includeCurrentNode)

    /** Names from [id]'s node up to the root. */
    fun getParentsName(id: T?, includeCurrentNode: Boolean): List<CharSequence?> =
        TreeUtil.getParentsName(getNode(id), includeCurrentNode)

    override fun toString(): String = buildString { printNode(this@Tree, this, 0) }

    @Suppress("UNCHECKED_CAST")
    private fun valueOf(key: String): T? = get(key) as? T

    private fun printNode(tree: Tree<*>, out: StringBuilder, indent: Int) {
        out.append(" ".repeat(indent)).append(tree.name).append('[').append(tree.id).append(']').append('\n')
        val kids = tree.children
        if (!kids.isNullOrEmpty()) for (child in kids) printNode(child, out, indent + 2)
    }
}

