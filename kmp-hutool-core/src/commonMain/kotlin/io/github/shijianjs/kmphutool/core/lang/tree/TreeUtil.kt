package io.github.shijianjs.kmphutool.core.lang.tree

import io.github.shijianjs.kmphutool.core.lang.tree.parser.DefaultNodeParser
import io.github.shijianjs.kmphutool.core.lang.tree.parser.NodeParser

/**
 * Tree construction and traversal helpers.
 *
 * Port of `cn.hutool.core.lang.tree.TreeUtil`. The stream-based generic builder uses plain loops, and
 * overloads that differ only by an id type are folded into one generic signature.
 *
 * @hutool cn.hutool.core.lang.tree.TreeUtil
 */
object TreeUtil {
    /** A single-rooted tree with parent id `0`. */
    fun buildSingle(list: List<TreeNode<Int>>): Tree<Int> = buildSingle(list, 0)

    /** A forest whose roots have parent id `0`. */
    fun build(list: List<TreeNode<Int>>): List<Tree<Int>> = build(list, 0)

    /** A single-rooted tree for [parentId]. */
    fun <E> buildSingle(list: List<TreeNode<E>>, parentId: E): Tree<E> =
        buildSingle(list, parentId, TreeNodeConfig.DEFAULT_CONFIG, DefaultNodeParser())

    /** A forest for [parentId]. */
    fun <E> build(list: List<TreeNode<E>>, parentId: E): List<Tree<E>> =
        build(list, parentId, TreeNodeConfig.DEFAULT_CONFIG, DefaultNodeParser())

    /** A single-rooted tree, converting items through [nodeParser]. */
    fun <T, E> buildSingle(list: List<T>, parentId: E, nodeParser: NodeParser<T, E>): Tree<E> =
        buildSingle(list, parentId, TreeNodeConfig.DEFAULT_CONFIG, nodeParser)

    /** A forest, converting items through [nodeParser]. */
    fun <T, E> build(list: List<T>, parentId: E, nodeParser: NodeParser<T, E>): List<Tree<E>> =
        build(list, parentId, TreeNodeConfig.DEFAULT_CONFIG, nodeParser)

    /** A forest: the children of the synthetic root node. */
    fun <T, E> build(list: List<T>, rootId: E, config: TreeNodeConfig, nodeParser: NodeParser<T, E>): List<Tree<E>> =
        buildSingle(list, rootId, config, nodeParser).children ?: emptyList()

    /** A single-rooted tree built through [TreeBuilder]. */
    fun <T, E> buildSingle(list: List<T>, rootId: E, config: TreeNodeConfig, nodeParser: NodeParser<T, E>): Tree<E> =
        TreeBuilder.of(rootId, config).append(list, nodeParser).build()

    /** A forest built from pre-made nodes. */
    fun <E> build(map: Map<E, Tree<E>>, rootId: E): List<Tree<E>> = buildSingle(map, rootId).children ?: emptyList()

    /** A single-rooted tree built from pre-made nodes. */
    fun <E> buildSingle(map: Map<E, Tree<E>>, rootId: E): Tree<E> {
        val first = map.values.firstOrNull { it != null }
        if (first != null) return TreeBuilder.of(rootId, first.config).append(map).build()
        return createEmptyNode(rootId)
    }

    /** Depth-first lookup of [id] under [node]. */
    fun <T> getNode(node: Tree<T>, id: T?): Tree<T>? {
        if (id == node.id) return node
        val children = node.children ?: return null
        for (child in children) {
            val found = getNode(child, id)
            if (found != null) return found
        }
        return null
    }

    /** Names from [node] up to the root; a `null` root name is skipped. */
    fun <T> getParentsName(node: Tree<T>?, includeCurrentNode: Boolean): List<CharSequence?> {
        val result = ArrayList<CharSequence?>()
        if (node == null) return result
        if (includeCurrentNode) result.add(node.name)
        var parent = node.parent
        while (parent != null) {
            val name = parent.name
            parent = parent.parent
            if (name != null || parent != null) result.add(name)
        }
        return result
    }

    /** Ids from [node] up to the root; a `null` root id is skipped. */
    fun <T> getParentsId(node: Tree<T>?, includeCurrentNode: Boolean): List<T?> {
        val result = ArrayList<T?>()
        if (node == null) return result
        if (includeCurrentNode) result.add(node.id)
        var parent = node.parent
        while (parent != null) {
            val id = parent.id
            parent = parent.parent
            if (id != null || parent != null) result.add(id)
        }
        return result
    }

    /** An empty node holding only [id]. */
    fun <E> createEmptyNode(id: E): Tree<E> = Tree<E>().apply { this.id = id }

    /**
     * Builds a forest over arbitrary objects without binding them to [Tree].
     *
     * @param idFunc reads the node id
     * @param parentIdFunc reads the parent id
     * @param setChildFunc attaches the children computed for a node
     */
    fun <T, E> build(
        nodes: List<E>,
        rootId: T,
        idFunc: (E) -> T,
        parentIdFunc: (E) -> T,
        setChildFunc: (E, List<E>) -> Unit,
    ): List<E> {
        val roots = nodes.filter { parentIdFunc(it) == rootId }
        val visited = HashSet<T>(roots.size + nodes.size)
        roots.forEach { setChildren(it, nodes, visited, idFunc, parentIdFunc, setChildFunc) }
        return roots
    }

    private fun <T, E> setChildren(
        root: E,
        nodes: List<E>,
        visited: MutableSet<T>,
        idFunc: (E) -> T,
        parentIdFunc: (E) -> T,
        setChildFunc: (E, List<E>) -> Unit,
    ) {
        val children = ArrayList<E>()
        for (candidate in nodes) {
            if (visited.contains(idFunc(candidate))) continue
            if (idFunc(root) != parentIdFunc(candidate)) continue
            visited.add(idFunc(candidate))
            children.add(candidate)
            setChildren(candidate, nodes, visited, idFunc, parentIdFunc, setChildFunc)
        }
        setChildFunc(root, children)
    }
}
