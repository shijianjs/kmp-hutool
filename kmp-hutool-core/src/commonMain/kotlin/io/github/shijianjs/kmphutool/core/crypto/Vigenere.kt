package io.github.shijianjs.kmphutool.core.crypto

/**
 * The Vigenère polyalphabetic cipher over printable ASCII (`32..126`).
 *
 * Port of `cn.hutool.crypto.symmetric.Vigenere`; the algorithm came from
 * `zhaorenjie110/SymmetricEncryptionAndDecryption`.
 *
 * @hutool cn.hutool.crypto.symmetric.Vigenere
 */
object Vigenere {
    /** Encrypts [data] with [cipherKey]. */
    fun encrypt(data: CharSequence, cipherKey: CharSequence): String {
        val dataLen = data.length
        val keyLen = cipherKey.length
        require(keyLen != 0) { "cipherKey must not be empty" }

        val cipherArray = CharArray(dataLen)
        for (i in 0..dataLen / keyLen) {
            for (t in 0 until keyLen) {
                val index = t + i * keyLen
                if (index < dataLen) {
                    val dataChar = data[index]
                    val keyChar = cipherKey[t]
                    cipherArray[index] = (((dataChar.code + keyChar.code - 64) % 95) + 32).toChar()
                }
            }
        }
        return cipherArray.concatToString()
    }

    /** Decrypts [data] with [cipherKey]. */
    fun decrypt(data: CharSequence, cipherKey: CharSequence): String {
        val dataLen = data.length
        val keyLen = cipherKey.length
        require(keyLen != 0) { "cipherKey must not be empty" }

        val clearArray = CharArray(dataLen)
        for (i in 0..dataLen / keyLen) {
            for (t in 0 until keyLen) {
                val index = t + i * keyLen
                if (index < dataLen) {
                    val dataChar = data[index]
                    val keyChar = cipherKey[t]
                    clearArray[index] = if (dataChar.code - keyChar.code >= 0) {
                        (((dataChar.code - keyChar.code) % 95) + 32).toChar()
                    } else {
                        (((dataChar.code - keyChar.code + 95) % 95) + 32).toChar()
                    }
                }
            }
        }
        return clearArray.concatToString()
    }
}
