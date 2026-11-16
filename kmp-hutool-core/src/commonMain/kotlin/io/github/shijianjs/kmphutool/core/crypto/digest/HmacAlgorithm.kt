package io.github.shijianjs.kmphutool.core.crypto.digest

/**
 * HMAC algorithms named by Hutool; availability depends on the platform provider.
 *
 * Port of `cn.hutool.crypto.digest.HmacAlgorithm`.
 *
 * @hutool cn.hutool.crypto.digest.HmacAlgorithm
 */
enum class HmacAlgorithm(val value: String) {
    HmacMD5("HmacMD5"),
    HmacSHA1("HmacSHA1"),
    HmacSHA256("HmacSHA256"),
    HmacSHA384("HmacSHA384"),
    HmacSHA512("HmacSHA512"),

    /** Requires BouncyCastle on the JVM. */
    HmacSM3("HmacSM3"),

    /** SM4 CMAC, requires BouncyCastle on the JVM. */
    SM4CMAC("SM4CMAC"),
}
