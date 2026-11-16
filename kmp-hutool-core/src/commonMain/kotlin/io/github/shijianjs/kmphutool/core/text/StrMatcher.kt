package io.github.shijianjs.kmphutool.core.text

/**
 * Shell-style wildcard matching: `*` matches any run of characters, `?` exactly one.
 *
 * Port of `cn.hutool.core.text.StrMatcher`. Matching is iterative with a single backtrack point, so
 * it stays linear in the text length instead of recursing per wildcard.
 *
 * @hutool cn.hutool.core.text.StrMatcher
 */
object StrMatcher {
    /** `true` when [text] matches [pattern]; case-sensitive. */
    fun isMatch(pattern: CharSequence, text: CharSequence): Boolean = isMatch(pattern, text, false)

    /** `true` when [text] matches [pattern]; [isIgnoreCase] folds case. */
    fun isMatch(pattern: CharSequence, text: CharSequence, isIgnoreCase: Boolean): Boolean {
        val patternText = if (isIgnoreCase) pattern.toString().lowercase() else pattern.toString()
        val sourceText = if (isIgnoreCase) text.toString().lowercase() else text.toString()

        var patternIndex = 0
        var textIndex = 0
        var starIndex = -1
        var matchIndex = 0

        while (textIndex < sourceText.length) {
            val current = patternText.getOrNull(patternIndex)
            when {
                current == '?' || current == sourceText[textIndex] -> {
                    patternIndex++
                    textIndex++
                }
                current == '*' -> {
                    starIndex = patternIndex
                    matchIndex = textIndex
                    patternIndex++
                }
                starIndex != -1 -> {
                    // Backtrack: let the last `*` swallow one more character.
                    patternIndex = starIndex + 1
                    matchIndex++
                    textIndex = matchIndex
                }
                else -> return false
            }
        }
        while (patternIndex < patternText.length && patternText[patternIndex] == '*') patternIndex++
        return patternIndex == patternText.length
    }
}
