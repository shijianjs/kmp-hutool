package io.github.shijianjs.kmphutool.core

import io.github.shijianjs.kmphutool.core.dfa.SensitiveProcessor
import io.github.shijianjs.kmphutool.core.dfa.WordTree
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class WordTreeTest {
    @Test
    fun findsWordsWithRanges() {
        val tree = WordTree().addWords("红领巾", "红河", "hello")
        assertTrue(tree.isMatch("我爱红领巾"))
        assertEquals("红领巾", tree.match("我爱红领巾"))
        val found = tree.matchWord("我爱红领巾")
        assertEquals(2, found!!.getStartIndex())
        assertEquals(4, found.getEndIndex())
        assertEquals("红领巾", found.getWord())
    }

    @Test
    fun collectsAllWords() {
        val tree = WordTree().addWords("ab", "b", "abc")
        assertEquals(listOf("ab", "b", "ab", "b"), tree.matchAll("abab", -1, true, false))
        assertEquals(listOf("ab"), tree.matchAll("abc"))

    }

    @Test
    fun ignoresStopCharactersAtStart() {
        val tree = WordTree().addWords("中国")
        assertTrue(tree.isMatch("，中国"))
    }

    @Test
    fun defaultProcessorMasksTheFoundWord() {
        val tree = WordTree().addWords("红领巾")
        val found = tree.matchWord("我爱红领巾")!!
        val processor = object : SensitiveProcessor {}
        assertEquals("***", processor.process(found))
    }
}



