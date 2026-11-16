package io.github.shijianjs.kmphutool.core.text.replacer

import io.github.shijianjs.kmphutool.core.lang.Chain
import io.github.shijianjs.kmphutool.core.text.StrBuilder

/**
 * Applies several [StrReplacer]s in order; the first one that consumes characters wins.
 *
 * Port of `cn.hutool.core.text.replacer.ReplacerChain`.
 *
 * @hutool cn.hutool.core.text.replacer.ReplacerChain
 */
open class ReplacerChain(vararg strReplacers: StrReplacer) :
    StrReplacer(),
    Chain<StrReplacer, ReplacerChain> {

    private val replacers = mutableListOf<StrReplacer>()

    init {
        strReplacers.forEach { addChain(it) }
    }

    override fun addChain(element: StrReplacer): ReplacerChain {
        replacers.add(element)
        return this
    }

    override fun iterator(): Iterator<StrReplacer> = replacers.iterator()

    override fun replaceAt(str: CharSequence, pos: Int, out: StrBuilder): Int {
        for (replacer in replacers) {
            val consumed = replacer.replaceAt(str, pos, out)
            if (consumed != 0) return consumed
        }
        return 0
    }
}
