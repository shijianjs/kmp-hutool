package io.github.shijianjs.kmphutool.core.text.escape

import io.github.shijianjs.kmphutool.core.text.replacer.LookupReplacer
import io.github.shijianjs.kmphutool.core.text.replacer.ReplacerChain

/**
 * XML escaping: `'`, `"`, `&`, `<`, `>` become their named entities.
 *
 * Port of `cn.hutool.core.text.escape.XmlEscape`.
 *
 * @hutool cn.hutool.core.text.escape.XmlEscape
 */
class XmlEscape : ReplacerChain() {
    init {
        addChain(LookupReplacer(*BASIC_ESCAPE))
    }

    companion object {
        internal val BASIC_ESCAPE: Array<Array<String>> = arrayOf(
            arrayOf("'", "&apos;"),
            arrayOf("\"", "&quot;"),
            arrayOf("&", "&amp;"),
            arrayOf("<", "&lt;"),
            arrayOf(">", "&gt;"),
        )
    }
}
