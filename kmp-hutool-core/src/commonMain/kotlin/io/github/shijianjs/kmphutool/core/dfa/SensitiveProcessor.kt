package io.github.shijianjs.kmphutool.core.dfa

/**
 * Replaces a found sensitive word; the default masks it with one `*` per character.
 *
 * Port of `cn.hutool.dfa.SensitiveProcessor`.
 *
 * @hutool cn.hutool.dfa.SensitiveProcessor
 */
interface SensitiveProcessor {
    fun process(foundWord: FoundWord): String {
        val length = foundWord.getFoundWord().length
        return "*".repeat(length)
    }
}
