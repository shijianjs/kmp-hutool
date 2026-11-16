package io.github.shijianjs.kmphutool.core.lang.tree.parser

import io.github.shijianjs.kmphutool.core.lang.tree.Tree
import io.github.shijianjs.kmphutool.core.lang.tree.TreeNode

/**
 * Copies a [TreeNode]'s fields (and extras) onto a [Tree].
 *
 * Port of `cn.hutool.core.lang.tree.parser.DefaultNodeParser`.
 *
 * @hutool cn.hutool.core.lang.tree.parser.DefaultNodeParser
 */
class DefaultNodeParser<T> : NodeParser<TreeNode<T>, T> {
    override fun parse(source: TreeNode<T>, treeNode: Tree<T>) {
        treeNode.id = source.id
        treeNode.parentId = source.parentId
        treeNode.weight = source.weight
        treeNode.name = source.name
        source.extra?.forEach { (key, value) -> treeNode.putExtra(key, value) }
    }
}
