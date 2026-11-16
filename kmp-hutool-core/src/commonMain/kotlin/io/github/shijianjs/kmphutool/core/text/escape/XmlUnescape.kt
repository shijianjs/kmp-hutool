package io.github.shijianjs.kmphutool.core.text.escape

import io.github.shijianjs.kmphutool.core.text.replacer.LookupReplacer
import io.github.shijianjs.kmphutool.core.text.replacer.ReplacerChain

/**
 * XML unescaping: named entities plus numeric `&#...;` / `&#x...;` forms.
 *
 * Port of `cn.hutool.core.text.escape.XmlUnescape`.
 *
 * @hutool cn.hutool.core.text.escape.XmlUnescape
 */
open class XmlUnescape : ReplacerChain() {
    init {
        addChain(LookupReplacer(*BASIC_UNESCAPE))
        addChain(NumericEntityUnescaper())
    }

    companion object {
        internal val BASIC_UNESCAPE: Array<Array<String>> = InternalEscapeUtil.invert(XmlEscape.BASIC_ESCAPE)
    }
}
