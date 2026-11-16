package io.github.shijianjs.kmphutool.core

import io.github.shijianjs.kmphutool.core.exceptions.UtilException
import io.github.shijianjs.kmphutool.core.util.VersionUtil
import kotlin.test.Test
import kotlin.test.assertFailsWith
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class VersionUtilTest {
    @Test
    fun comparesAgainstOperators() {
        assertTrue(VersionUtil.isGreaterThan("1.0.2", "1.0.1"))
        assertTrue(VersionUtil.isGreaterThanOrEqual("1.0.2", "1.0.2"))
        assertTrue(VersionUtil.isLessThan("1.0.1", "1.0.2"))
        assertTrue(VersionUtil.isLessThanOrEqual("1.0.2", "1.0.2"))
        assertFalse(VersionUtil.isGreaterThan("1.0.2", "1.0.2"))
    }

    @Test
    fun matchesBooleanExpressions() {
        assertTrue(VersionUtil.matchEl("1.0.2", ">=1.0.2"))
        assertTrue(VersionUtil.matchEl("1.0.2", "<1.0.1;1.0.2"))
        assertFalse(VersionUtil.matchEl("1.0.2", "<1.0.2"))
        assertTrue(VersionUtil.matchEl("1.0.2", "1.0.0-1.1.1"))
        assertTrue(VersionUtil.matchEl("1.0.2", "≥1.0.0"))
    }

    @Test
    fun matchesListsAndRejectsBadDelimiters() {
        assertTrue(VersionUtil.anyMatch("1.0.2", listOf("1.0.1", "1.0.2")))
        assertTrue(VersionUtil.anyMatch("1.0.2", "0.9.9", "1.0.2"))
        assertFailsWith<UtilException> { VersionUtil.matchEl("1.0.2", ">=1.0.0", "-") }
    }
}
