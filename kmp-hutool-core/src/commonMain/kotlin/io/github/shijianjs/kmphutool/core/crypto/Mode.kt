package io.github.shijianjs.kmphutool.core.crypto

/**
 * Block-cipher modes.
 *
 * Port of `cn.hutool.crypto.Mode`.
 *
 * @hutool cn.hutool.crypto.Mode
 */
enum class Mode {
    NONE,
    CBC,
    CFB,
    CTR,
    CTS,
    ECB,
    OFB,
    PCBC,
}
