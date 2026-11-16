package io.github.shijianjs.kmphutool.core.text.escape

import io.github.shijianjs.kmphutool.core.text.replacer.LookupReplacer

/**
 * HTML4 unescaping: the XML entities plus the ISO-8859-1 and HTML 4.0 tables, inverted.
 *
 * Port of `cn.hutool.core.text.escape.Html4Unescape`.
 *
 * @hutool cn.hutool.core.text.escape.Html4Unescape
 */
class Html4Unescape : XmlUnescape() {
    init {
        addChain(LookupReplacer(*ISO8859_1_UNESCAPE))
        addChain(LookupReplacer(*HTML40_EXTENDED_UNESCAPE))
    }

    companion object {
        internal val ISO8859_1_UNESCAPE: Array<Array<String>> =
            InternalEscapeUtil.invert(Html4Escape.ISO8859_1_ESCAPE)
        internal val HTML40_EXTENDED_UNESCAPE: Array<Array<String>> =
            InternalEscapeUtil.invert(Html4Escape.HTML40_EXTENDED_ESCAPE)
    }
}
