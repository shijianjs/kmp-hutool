package io.github.shijianjs.kmphutool.core.crypto.digest

/**
 * Message digest algorithms known to Hutool.
 *
 * Port of `cn.hutool.crypto.digest.DigestAlgorithm`.
 *
 * @hutool cn.hutool.crypto.digest.DigestAlgorithm
 */
enum class DigestAlgorithm(val value: String) {
    MD2("MD2"),
    MD5("MD5"),
    SHA1("SHA-1"),
    SHA256("SHA-256"),
    SHA384("SHA-384"),
    SHA512("SHA-512"),
}
