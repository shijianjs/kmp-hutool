package io.github.shijianjs.kmphutool.core.crypto

/**
 * Block-cipher paddings.
 *
 * Port of `cn.hutool.crypto.Padding`.
 *
 * @hutool cn.hutool.crypto.Padding
 */
enum class Padding {
    NoPadding,
    ZeroPadding,
    ISO10126Padding,
    OAEPPadding,
    PKCS1Padding,
    PKCS5Padding,
    SSL3Padding,
}
