package io.github.shijianjs.kmphutool.core.lang.tree

import io.github.shijianjs.kmphutool.core.builder.Builder
import io.github.shijianjs.kmphutool.core.lang.tree.parser.NodeParser

/**
 * Assembles a [Tree] from a flat node collection.
 *
 * Port of `cn.hutool.core.lang.tree.TreeBuilder`. Nodes are attached to the node whose id equals their
 * parent id, sorted by weight, and the tree is pruned when [TreeNodeConfig.deep] is set.
 *
 * @hutool cn.hutool.core.lang.tree.TreeBuilder
 */
class TreeBuilder<E>(rootNode: Tree<E>) : Builder<Tree<E>> {
    private var root: Tree<E> = rootNode
    private val idTreeMap: MutableMap<E, Tree<E>> = LinkedHashMap()
    private var built = false

    constructor(rootId: E, config: TreeNodeConfig? = null) : this(Tree<E>(config).apply { id = rootId })

    /** Sets the root id. */
    fun setId(id: E): TreeBuilder<E> = apply { root.id = id }

    /** Sets the root's parent id. */
    fun setParentId(parentId: E): TreeBuilder<E> = apply { root.parentId = parentId }

    /** Sets the root's display name. */
    fun setName(name: CharSequence): TreeBuilder<E> = apply { root.name = name }

    /** Sets the root's weight. */
    fun setWeight(weight: Comparable<*>): TreeBuilder<E> = apply { root.weight = weight }

    /** Adds an extra field to the root. */
    fun putExtra(key: String, value: Any?): TreeBuilder<E> = apply { root.putExtra(key, value) }

    /** Adds pre-built nodes, keyed by id. */
    fun append(map: Map<E, Tree<E>>): TreeBuilder<E> = apply {
        checkBuilt()
        idTreeMap.putAll(map)
    }

    /** Adds pre-built nodes. */
    fun append(trees: Iterable<Tree<E>>): TreeBuilder<E> = apply {
        checkBuilt()
        for (tree in trees) {
            val id = tree.id ?: continue
            idTreeMap[id] = tree
        }
    }

    /** Converts [list] through [nodeParser] and adds the resulting nodes. */
    fun <T> append(list: List<T>, nodeParser: NodeParser<T, E>): TreeBuilder<E> = apply {
        checkBuilt()
        val config = root.config
        val rootId = root.id
        for (item in list) {
            val node = Tree<E>(config)
            nodeParser.parse(item, node)
            val nodeId = node.id
            if (rootId != null && nodeId != null && rootId::class != nodeId::class) {
                throw IllegalArgumentException("rootId type is node.getId().getClass()!")
            }
            if (rootId != null && rootId == nodeId) {
                root = node
            } else if (nodeId != null) {
                idTreeMap[nodeId] = node
            }
        }
    }

    /** Clears the accumulated nodes so the builder can be reused. */
    fun reset(): TreeBuilder<E> = apply {
        idTreeMap.clear()
        root.children = null
        built = false
    }

    override fun build(): Tree<E> {
        checkBuilt()
        buildFromMap()
        cutTree()
        built = true
        idTreeMap.clear()
        return root
    }

    /** The root's children, i.e. a forest without an explicit root node. */
    fun buildList(): List<Tree<E>> {
        if (built) return root.children ?: emptyList()
        return build().children ?: emptyList()
    }

    private fun buildFromMap() {
        if (idTreeMap.isEmpty()) return
        val rootId = root.id
        val sorted = idTreeMap.values.sortedWith { a, b -> a.compareTo(b) }
        for (node in sorted) {
            val parentId = node.parentId
            if (rootId == parentId) {
                root.addChildren(node)
                continue
            }
            if (parentId != null) idTreeMap[parentId]?.addChildren(node)
        }
    }

    private fun cutTree() {
        val deep = root.config.deep ?: return
        if (deep < 0) return
        cutTree(root, 0, deep)
    }

    private fun cutTree(tree: Tree<E>, currentDeep: Int, maxDeep: Int) {
        if (currentDeep == maxDeep) {
            tree.children = null
            return
        }
        tree.children?.forEach { cutTree(it, currentDeep + 1, maxDeep) }
    }

    private fun checkBuilt() {
        check(!built) { "Current tree has been built." }
    }

    companion object {
        /** A builder rooted at [rootId]. */
        fun <E> of(rootId: E): TreeBuilder<E> = of(rootId, null)

        /** A builder rooted at [rootId] using [config]. */
        fun <E> of(rootId: E, config: TreeNodeConfig?): TreeBuilder<E> = TreeBuilder(rootId, config)
    }
}
