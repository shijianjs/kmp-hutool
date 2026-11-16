package io.github.shijianjs.kmphutool.core.jwt.signers

import io.github.shijianjs.kmphutool.core.crypto.asymmetric.SignAlgorithm
import io.github.shijianjs.kmphutool.core.crypto.digest.HmacAlgorithm

/**
 * Maps JWT algorithm IDs (`HS256`) to crypto algorithm names and back.
 *
 * Port of `cn.hutool.jwt.signers.AlgorithmUtil`. Hutool's `BiMap` is replaced by two lookup
 * maps because common code has no `BiMap`.
 *
 * @hutool cn.hutool.jwt.signers.AlgorithmUtil
 */
object AlgorithmUtil {
    private val idToAlgorithm: Map<String, String> = mutableMapOf<String, String>().apply {
        put("HS256", HmacAlgorithm.HmacSHA256.value)
        put("HS384", HmacAlgorithm.HmacSHA384.value)
        put("HS512", HmacAlgorithm.HmacSHA512.value)
        put("HMD5", HmacAlgorithm.HmacMD5.value)
        put("HSHA1", HmacAlgorithm.HmacSHA1.value)
        put("SM4CMAC", HmacAlgorithm.SM4CMAC.value)
        put("RS256", SignAlgorithm.SHA256withRSA.value)
        put("RS384", SignAlgorithm.SHA384withRSA.value)
        put("RS512", SignAlgorithm.SHA512withRSA.value)
        put("ES256", SignAlgorithm.SHA256withECDSA.value)
        put("ES384", SignAlgorithm.SHA384withECDSA.value)
        put("ES512", SignAlgorithm.SHA512withECDSA.value)
        put("PS256", SignAlgorithm.SHA256withRSA_PSS.value)
        put("PS384", SignAlgorithm.SHA384withRSA_PSS.value)
        put("PS512", SignAlgorithm.SHA512withRSA_PSS.value)
        put("RMD2", SignAlgorithm.MD2withRSA.value)
        put("RMD5", SignAlgorithm.MD5withRSA.value)
        put("RSHA1", SignAlgorithm.SHA1withRSA.value)
        put("DNONE", SignAlgorithm.NONEwithDSA.value)
        put("DSHA1", SignAlgorithm.SHA1withDSA.value)
        put("ENONE", SignAlgorithm.NONEwithECDSA.value)
        put("ESHA1", SignAlgorithm.SHA1withECDSA.value)
    }
    private val algorithmToId: Map<String, String> = idToAlgorithm.entries.associate { it.value to it.key }

    /** Returns the algorithm name for an ID, or [idOrAlgorithm] when it is not a known ID. */
    fun getAlgorithm(idOrAlgorithm: String): String =
        idToAlgorithm[idOrAlgorithm.uppercase()] ?: idOrAlgorithm

    /** Returns the JWT ID for an algorithm name, or [idOrAlgorithm] when it is not known. */
    fun getId(idOrAlgorithm: String): String =
        algorithmToId[idOrAlgorithm] ?: idOrAlgorithm
}
