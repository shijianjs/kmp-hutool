package io.github.shijianjs.kmphutool.core.crypto

/**
 * Cipher operation modes, with the `javax.crypto.Cipher` numeric values.
 *
 * Port of `cn.hutool.crypto.CipherMode`.
 *
 * @hutool cn.hutool.crypto.CipherMode
 */
enum class CipherMode(private val value: Int) {
    encrypt(1),
    decrypt(2),
    wrap(3),
    unwrap(4);

    /** The `Cipher` constant value. */
    fun getValue(): Int = value
}
