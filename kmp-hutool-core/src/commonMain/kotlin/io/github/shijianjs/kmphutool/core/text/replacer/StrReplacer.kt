package io.github.shijianjs.kmphutool.core.text.replacer

import io.github.shijianjs.kmphutool.core.lang.Replacer
import io.github.shijianjs.kmphutool.core.text.StrBuilder

/**
 * Base class for character-wise replacements: subclasses say how much of the input they consumed at
 * a position, and the base class assembles the output.
 *
 * Port of `cn.hutool.core.text.replacer.StrReplacer`. Kotlin has no package-private accessibility,
 * so the hook is `internal` instead of the upstream `protected` method — subclasses in other
 * modules use [replace] only.
 *
 * @hutool cn.hutool.core.text.replacer.StrReplacer
 */
abstract class StrReplacer : Replacer<CharSequence> {
    /**
     * Replaces the text starting at [pos], appending to [out].
     *
     * @return how many input characters were consumed; `0` means "copy this character unchanged"
     */
    internal abstract fun replaceAt(str: CharSequence, pos: Int, out: StrBuilder): Int

    override fun replace(t: CharSequence): CharSequence {
        if (t.isEmpty()) return t

        val out = StrBuilder()
        var pos = 0
        while (pos < t.length) {
            val consumed = replaceAt(t, pos, out)
            if (consumed == 0) {
                out.append(t[pos])
                pos++
            }
            pos += consumed
        }
        return out
    }
}
