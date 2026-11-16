package io.github.shijianjs.kmphutool.core

import io.github.shijianjs.kmphutool.core.json.JSONStrFormatter
import kotlin.test.Test
import kotlin.test.assertTrue

class JSONStrFormatterTest {
    @Test
    fun indentsObjectMembers() {
        val formatted = JSONStrFormatter.format("""{"a":[1,2],"b":{"c":3}}""")
        assertTrue(formatted.contains("\n    \"a\""))
        assertTrue(formatted.contains("\n        \"c\""))
    }
}
