package io.github.shijianjs.kmphutool.core

import io.github.shijianjs.kmphutool.core.crypto.SignUtil
import io.github.shijianjs.kmphutool.core.crypto.digest.DigestAlgorithm
import kotlin.test.Test
import kotlin.test.assertEquals

class SignUtilTest {
    @Test
    fun sortedParameterDigestsMatchPython() {
        val params = linkedMapOf("b" to 2, "a" to 1)
        assertEquals("C5A47BAB03810E857EE630E2E29EFAF3", SignUtil.signParamsMd5(params, "key"))
        assertEquals("24CFF97E73C3D7B3FF151D5E930B5B7FB6323A53", SignUtil.signParamsSha1(params, "key"))
        assertEquals(
            "30583F292F91B149ACA3AA73B866A74E2957AA626C6B65A32A247DDA68671B23",
            SignUtil.signParamsSha256(params, "key"),
        )
    }

    @Test
    fun separatorsAndNullHandling() {
        val params = linkedMapOf("b" to 2, "a" to 1, "c" to null)
        assertEquals(
            "D53CF64E768F4EF09C806BBE12258C78211B2690",
            SignUtil.signParams(DigestAlgorithm.SHA1, params, "&", "=", true),
        )
    }
}
