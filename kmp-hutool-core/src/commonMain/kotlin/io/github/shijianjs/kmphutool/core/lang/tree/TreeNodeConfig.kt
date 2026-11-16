package io.github.shijianjs.kmphutool.core.lang.tree

/**
 * Key names and the optional depth limit used when building a [Tree].
 *
 * Port of `cn.hutool.core.lang.tree.TreeNodeConfig`; the Java builder-style setters become plain
 * mutable properties, and [deep] caps recursion depth (`null` means unlimited).
 *
 * @hutool cn.hutool.core.lang.tree.TreeNodeConfig
 */
class TreeNodeConfig {
    var idKey: String = "id"
    var parentIdKey: String = "parentId"
    var weightKey: String = "weight"
    var nameKey: String = "name"
    var childrenKey: String = "children"

    /** Maximum depth counted from `0`; `null` disables pruning. */
    var deep: Int? = null

    companion object {
        /** The default configuration, using `id`/`parentId`/`name`/`weight`/`children`. */
        var DEFAULT_CONFIG: TreeNodeConfig = TreeNodeConfig()
    }
}

