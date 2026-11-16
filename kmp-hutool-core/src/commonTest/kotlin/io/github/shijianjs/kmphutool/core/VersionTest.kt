package io.github.shijianjs.kmphutool.core

import io.github.shijianjs.kmphutool.core.lang.Version
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class VersionTest {
    @Test
    fun comparesNumericParts() {
        assertTrue(Version.of("1.0.0") < Version.of("1.0.1"))
        assertTrue(Version.of("1.2") < Version.of("1.10"))
        assertTrue(Version.of("2.0") > Version.of("1.99"))
        assertEquals(Version.of("1.0.0"), Version.of("1.0.0"))
    }

    @Test
    fun preReleaseSortsBeforeRelease() {
        assertTrue(Version.of("1.0-rc1") < Version.of("1.0"))
        assertTrue(Version.of("1.0-rc1") < Version.of("1.0-rc2"))
        assertTrue(Version.of("1.0+build1") == Version.of("1.0+build2") || Version.of("1.0+build1") < Version.of("1.0+build2"))
    }

    @Test
    fun stringTokensCompareLexicographically() {
        assertTrue(Version.of("1.0-alpha") < Version.of("1.0-beta"))
        assertEquals("1.0", Version.of("1.0").toString())
        assertEquals("1.0".hashCode(), Version.of("1.0").hashCode())
    }
}
