package io.github.shijianjs.kmphutool.core.dfa

import io.github.shijianjs.kmphutool.core.lang.DefaultSegment

/**
 * One match found in the text: the tree word, the matched text and its inclusive range.
 *
 * Port of `cn.hutool.dfa.FoundWord`; ranges are inherited from [DefaultSegment].
 *
 * @hutool cn.hutool.dfa.FoundWord
 */
class FoundWord(
    private val word: String,
    private val foundWord: String,
    startIndex: Int,
    endIndex: Int,
) : DefaultSegment<Int>(startIndex, endIndex) {
    /** The word registered in the tree. */
    fun getWord(): String = word

    /** The text that was actually matched (may include stop characters). */
    fun getFoundWord(): String = foundWord

    override fun toString(): String = foundWord
}
