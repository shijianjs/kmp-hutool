package io.github.shijianjs.kmphutool.core

import io.github.shijianjs.kmphutool.core.text.escape.Html4Escape
import io.github.shijianjs.kmphutool.core.text.escape.Html4Unescape
import io.github.shijianjs.kmphutool.core.text.escape.XmlEscape
import io.github.shijianjs.kmphutool.core.text.escape.XmlUnescape
import kotlin.test.Test
import kotlin.test.assertEquals

class EscapeTest {
    @Test
    fun xmlEscapeRoundTrip() {
        val escaped = XmlEscape().replace("<a href=\"x\">'&'</a>").toString()
        assertEquals("&lt;a href=&quot;x&quot;&gt;&apos;&amp;&apos;&lt;/a&gt;", escaped)
        assertEquals("<a href=\"x\">'&'</a>", XmlUnescape().replace(escaped).toString())
    }

    @Test
    fun html4TablesApply() {
        assertEquals("&lt;", Html4Escape().replace("<").toString())
        assertEquals("&copy;", Html4Escape().replace("\u00A9").toString())
        assertEquals("\u00A9", Html4Unescape().replace("&copy;").toString())
        assertEquals("\u00A0", Html4Unescape().replace("&nbsp;").toString())
    }

    @Test
    fun numericEntitiesDecode() {
        assertEquals("'", XmlUnescape().replace("&#39;").toString())
        assertEquals("A", XmlUnescape().replace("&#x41;").toString())
        // A lone '&#' without a semicolon is left alone.
        assertEquals("&#39", XmlUnescape().replace("&#39").toString())
    }
}
