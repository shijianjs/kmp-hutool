package io.github.shijianjs.kmphutool.core.jwt.signers

import io.github.shijianjs.kmphutool.core.util.StrUtil

/**
 * The `alg=none` signer: no signature is produced and an empty signature is accepted.
 *
 * Port of `cn.hutool.jwt.signers.NoneJWTSigner`.
 *
 * @hutool cn.hutool.jwt.signers.NoneJWTSigner
 */
class NoneJWTSigner : JWTSigner {
    override fun sign(headerBase64: String, payloadBase64: String): String = ""

    override fun verify(headerBase64: String, payloadBase64: String, signBase64: String): Boolean =
        StrUtil.isEmpty(signBase64)

    override fun getAlgorithm(): String = ID_NONE

    companion object {
        const val ID_NONE: String = "none"

        val NONE: NoneJWTSigner = NoneJWTSigner()

        /** True when [alg] is blank or `none` (case-insensitive). */
        fun isNone(alg: String?): Boolean =
            StrUtil.isBlank(alg) || alg.equals(ID_NONE, ignoreCase = true)
    }
}
