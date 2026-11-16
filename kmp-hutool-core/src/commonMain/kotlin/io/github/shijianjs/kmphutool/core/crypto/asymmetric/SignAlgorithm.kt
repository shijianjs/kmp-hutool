package io.github.shijianjs.kmphutool.core.crypto.asymmetric

/**
 * Signature algorithm names.
 *
 * Port of `cn.hutool.crypto.asymmetric.SignAlgorithm`. The `*_PSS` entries need BouncyCastle
 * on platforms whose default JCA provider lacks RSASSA-PSS.
 *
 * @hutool cn.hutool.crypto.asymmetric.SignAlgorithm
 */
enum class SignAlgorithm(val value: String) {
    NONEwithRSA("NONEwithRSA"),
    MD2withRSA("MD2withRSA"),
    MD5withRSA("MD5withRSA"),
    SHA1withRSA("SHA1withRSA"),
    SHA256withRSA("SHA256withRSA"),
    SHA384withRSA("SHA384withRSA"),
    SHA512withRSA("SHA512withRSA"),
    NONEwithDSA("NONEwithDSA"),
    SHA1withDSA("SHA1withDSA"),
    NONEwithECDSA("NONEwithECDSA"),
    SHA1withECDSA("SHA1withECDSA"),
    SHA256withECDSA("SHA256withECDSA"),
    SHA384withECDSA("SHA384withECDSA"),
    SHA512withECDSA("SHA512withECDSA"),
    SHA256withRSA_PSS("SHA256WithRSA/PSS"),
    SHA384withRSA_PSS("SHA384WithRSA/PSS"),
    SHA512withRSA_PSS("SHA512WithRSA/PSS"),
}
