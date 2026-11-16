package io.github.shijianjs.kmphutool.core

import io.github.shijianjs.kmphutool.core.lang.Filter
import io.github.shijianjs.kmphutool.core.lang.tree.Tree
import io.github.shijianjs.kmphutool.core.lang.tree.TreeNode
import io.github.shijianjs.kmphutool.core.lang.tree.TreeNodeConfig
import io.github.shijianjs.kmphutool.core.lang.tree.TreeUtil
import io.github.shijianjs.kmphutool.core.lang.tree.parser.DefaultNodeParser
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlin.test.assertTrue

class TreeTest {
    private fun nodes(): List<TreeNode<String>> = listOf(
        TreeNode("1", "0", "root"),
        TreeNode("11", "1", "child-a"),
        TreeNode("12", "1", "child-b"),
        TreeNode("111", "11", "grandchild", 2),
    )

    @Test
    fun buildsHierarchyFromFlatList() {
        // buildSingle creates an empty node whose id is the requested parent id.
        val tree = TreeUtil.buildSingle(nodes(), "0")
        assertEquals("0", tree.id)
        assertNull(tree.name)
        assertEquals(1, tree.children!!.size)
        val root = tree.children!!.first()
        assertEquals("root", root.name)
        assertEquals(2, root.children!!.size)
        assertEquals("grandchild", tree.getNode("111")!!.name)
        assertTrue(tree.hasChild())
    }

    @Test
    fun parentsFollowTreeLinks() {
        val tree = TreeUtil.buildSingle(nodes(), "0")
        val grandchild = tree.getNode("111")!!
        assertEquals(listOf<CharSequence?>("grandchild", "child-a", "root"), TreeUtil.getParentsName(grandchild, true))
        assertEquals(listOf<String?>("111", "11", "1", "0"), TreeUtil.getParentsId(grandchild, true))
    }

    @Test
    fun depthPrunesTree() {
        val config = TreeNodeConfig().apply { deep = 1 }
        val tree = TreeUtil.buildSingle(nodes(), "0", config, DefaultNodeParser())
        assertNull(tree.getNode("111"))
        assertEquals(1, tree.children!!.size)
        assertNull(tree.children!!.first().children)
    }

    @Test
    fun filterKeepsMatchingBranches() {
        val tree = TreeUtil.buildSingle(nodes(), "0")
        val filtered = tree.filterNew(Filter { it.name == "grandchild" })!!
        assertEquals("0", filtered.id)
        val root = filtered.children!!.single()
        assertEquals("root", root.name)
        assertEquals("grandchild", filtered.getNode("111")!!.name)
        assertNull(filtered.getNode("12"))
    }

    @Test
    fun treeBehavesLikeMap() {
        val tree = Tree<String>()
        tree.id = "x"
        tree.putExtra("dept", "RD")
        assertEquals("x", tree["id"])
        assertEquals("RD", tree["dept"])

        tree.weight = 1
        val other = Tree<String>()
        other.weight = 2
        assertEquals(-1, tree.compareTo(other))
    }
}

