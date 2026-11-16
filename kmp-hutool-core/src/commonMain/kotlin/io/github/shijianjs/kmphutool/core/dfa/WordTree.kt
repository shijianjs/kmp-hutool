package io.github.shijianjs.kmphutool.core.dfa

import io.github.shijianjs.kmphutool.core.lang.Filter

/**
 * A DFA word tree: each node maps a character to its child node, and marked characters end a word.
 * Groups share prefixes, so `红领巾` and `红河` share the `红` node.
 *
 * Port of `cn.hutool.dfa.WordTree`. Kotlin's `MutableMap` replaces `HashMap` inheritance, and
 * `getOrPut` replaces `computeIfAbsent`.
 *
 * @hutool cn.hutool.dfa.WordTree
 */
class WordTree {
    private val children = HashMap<Char, WordTree>()
    private val endCharacterSet = HashSet<Char>()

    /** Character filter; characters rejected here never enter a match. */
    private var charFilter: Filter<Char> = Filter { StopChar.isNotStopChar(it) }

    /** Sets the character filter and returns `this`. */
    fun setCharFilter(charFilter: Filter<Char>): WordTree {
        this.charFilter = charFilter
        return this
    }

    /** Adds a collection of words. */
    fun addWords(words: Collection<String>): WordTree {
        val distinct = if (words is Set<String>) words else HashSet(words)
        for (word in distinct) addWord(word)
        return this
    }

    /** Adds words from a vararg list. */
    fun addWords(vararg words: String): WordTree = addWords(words.toSet())

    /** Adds one word. */
    fun addWord(word: String): WordTree {
        val charFilter = charFilter
        var parent: WordTree? = null
        var current = this
        var lastAcceptedChar: Char? = null
        for (currentChar in word) {
            if (charFilter.accept(currentChar)) {
                val child = current.children.getOrPut(currentChar) { WordTree() }
                parent = current
                current = child
                lastAcceptedChar = currentChar
            }
        }
        if (parent != null) parent.endCharacterSet.add(lastAcceptedChar!!)
        return this
    }

    /** Whether [text] contains any tree word. */
    fun isMatch(text: String?): Boolean = if (text == null) false else matchWord(text) != null

    /** The first matched text, or `null`. */
    fun match(text: String): String? = matchWord(text)?.toString()

    /** The first match with its range, or `null`. */
    fun matchWord(text: String?): FoundWord? {
        if (text == null) return null
        return matchAllWords(text, 1, false, false).firstOrNull()
    }

    /** All matched texts. */
    fun matchAll(text: String): List<String> = matchAll(text, -1)

    /** All matches with ranges. */
    fun matchAllWords(text: String): List<FoundWord> = matchAllWords(text, -1, false, false)

    /** All matched texts, capped at [limit] (`-1` for no limit). */
    fun matchAll(text: String, limit: Int): List<String> = matchAll(text, limit, false, false)

    /** All matches with ranges, capped at [limit]. */
    fun matchAllWords(text: String, limit: Int): List<FoundWord> = matchAllWords(text, limit, false, false)

    /** All matched texts. */
    fun matchAll(text: String, limit: Int, isDensityMatch: Boolean, isGreedMatch: Boolean): List<String> =
        matchAllWords(text, limit, isDensityMatch, isGreedMatch).map { it.toString() }

    /**
     * All matches. Density matching reports overlapping matches (`abab` → `ab,b,ab`); greedy matching
     * prefers the longest word at each start position.
     */
    fun matchAllWords(text: String, limit: Int, isDensityMatch: Boolean, isGreedMatch: Boolean): List<FoundWord> {
        val foundWords = ArrayList<FoundWord>()
        var current = this
        val length = text.length
        val charFilter = this.charFilter
        val wordBuffer = StringBuilder()
        val keyBuffer = StringBuilder()
        var i = 0
        while (i < length) {
            wordBuffer.setLength(0)
            keyBuffer.setLength(0)
            var j = i
            while (j < length) {
                val currentChar = text[j]
                if (!charFilter.accept(currentChar)) {
                    if (wordBuffer.isNotEmpty()) {
                        // A stop character between word characters stays part of the match.
                        wordBuffer.append(currentChar)
                    } else {
                        // A leading stop character is skipped.
                        i++
                    }
                    j++
                    continue
                } else if (!current.children.containsKey(currentChar)) {
                    break
                }
                wordBuffer.append(currentChar)
                keyBuffer.append(currentChar)
                if (current.isEnd(currentChar)) {
                    foundWords.add(FoundWord(keyBuffer.toString(), wordBuffer.toString(), i, j))
                    if (limit > 0 && foundWords.size >= limit) return foundWords
                    if (!isDensityMatch) {
                        i = j
                        break
                    }
                    if (!isGreedMatch) break
                }
                current = current.children.getValue(currentChar)
                j++
            }
            current = this
            i++
        }
        return foundWords
    }

    private fun isEnd(c: Char): Boolean = endCharacterSet.contains(c)

    /** Removes every word and end marker. */
    fun clear() {
        children.clear()
        endCharacterSet.clear()
    }
}
