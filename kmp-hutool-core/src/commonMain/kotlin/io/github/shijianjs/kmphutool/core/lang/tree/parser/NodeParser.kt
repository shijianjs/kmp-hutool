package io.github.shijianjs.kmphutool.core.lang.tree.parser

import io.github.shijianjs.kmphutool.core.lang.tree.Tree

/**
 * Converts one source object into a [Tree] node.
 *
 * Port of `cn.hutool.core.lang.tree.parser.NodeParser`.
 *
 * @hutool cn.hutool.core.lang.tree.parser.NodeParser
 */
fun interface NodeParser<T, E> {
    /** Copies the fields of [source] into [treeNode]. */
    fun parse(source: T, treeNode: Tree<E>)
}
