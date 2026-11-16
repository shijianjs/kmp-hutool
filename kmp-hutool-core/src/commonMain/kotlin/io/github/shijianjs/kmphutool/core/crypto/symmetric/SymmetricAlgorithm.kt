package io.github.shijianjs.kmphutool.core.crypto.symmetric

/**
 * Symmetric cipher names.
 *
 * Port of `cn.hutool.crypto.symmetric.SymmetricAlgorithm`.
 *
 * @hutool cn.hutool.crypto.symmetric.SymmetricAlgorithm
 */
enum class SymmetricAlgorithm(val value: String) {
    AES("AES"),
    ARCFOUR("ARCFOUR"),
    Blowfish("Blowfish"),
    DES("DES"),
    DESede("DESede"),
    RC2("RC2"),
    PBEWithMD5AndDES("PBEWithMD5AndDES"),
    PBEWithSHA1AndDESede("PBEWithSHA1AndDESede"),
    PBEWithSHA1AndRC2_40("PBEWithSHA1AndRC2_40"),
}
