package io.github.shijianjs.kmphutool.core.crypto.asymmetric

/**
 * Asymmetric cipher names.
 *
 * Port of `cn.hutool.crypto.asymmetric.AsymmetricAlgorithm`.
 *
 * @hutool cn.hutool.crypto.asymmetric.AsymmetricAlgorithm
 */
enum class AsymmetricAlgorithm(val value: String) {
    RSA("RSA"),
    RSA_ECB_PKCS1("RSA/ECB/PKCS1Padding"),
    RSA_ECB("RSA/ECB/NoPadding"),
    RSA_None("RSA/None/NoPadding"),
}
