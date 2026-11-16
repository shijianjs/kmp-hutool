package io.github.shijianjs.kmphutool.core.codec

/**
 * Base64 encoding and decoding, in the standard and URL-safe alphabets, with and without padding.
 *
 * Port of `cn.hutool.core.codec.Base64`. The Java class has `InputStream`/`File` overloads and a
 * charset parameter; the port works on [ByteArray] and [String] only, and always decodes UTF-8, which
 * is the charset every modern protocol uses. `kotlin.io.encoding.Base64` would be an alternative but
 * it is still experimental in the standard library, so the encoder is implemented here.
 *
 * @hutool cn.hutool.core.codec.Base64
 */
object Base64 {
    private const val ALPHABET_STANDARD = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/"
    private const val ALPHABET_URL_SAFE = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789-_"

    /** Encodes [source] as standard Base64 with padding. */
    fun encode(source: ByteArray): String = encodeStr(source, isMultiLine = false, isUrlSafe = false)

    /** Encodes [source] as URL-safe Base64 (`-` and `_` instead of `+` and `/`). */
    fun encodeUrlSafe(source: ByteArray): String = encodeStr(source, isMultiLine = false, isUrlSafe = true)

    /** Encodes [source] as Base64 without the trailing `=` padding. */
    fun encodeWithoutPadding(source: ByteArray): String =
        encodeStr(source, isMultiLine = false, isUrlSafe = false).trimEnd('=')

    /** Encodes the UTF-8 bytes of [source] as standard Base64. */
    fun encode(source: CharSequence): String = encode(source.toString().encodeToByteArray())

    /** Encodes the UTF-8 bytes of [source] as URL-safe Base64. */
    fun encodeUrlSafe(source: CharSequence): String = encodeUrlSafe(source.toString().encodeToByteArray())

    /** Decodes [source] into bytes; padding is optional and both alphabets are accepted. */
    fun decode(source: CharSequence): ByteArray = decode(source.toString())

    /** Decodes [source] into bytes. */
    fun decode(source: String): ByteArray {
        val cleaned = source.filterNot { it.isWhitespace() }
        require(cleaned.isNotEmpty() || source.isEmpty()) { "Base64 input is empty" }
        if (cleaned.isEmpty()) return ByteArray(0)
        val normalized = cleaned.replace('-', '+').replace('_', '/')
        val padding = normalized.takeLastWhile { it == '=' }.length
        val body = if (padding > 0) normalized.dropLast(padding) else normalized
        require(body.length % 4 != 1) { "Illegal Base64 input length: ${body.length}" }
        val result = ByteArray((body.length * 3) / 4)
        var byteIndex = 0
        var index = 0
        while (index < body.length) {
            val c0 = indexValue(body[index], ALPHABET_STANDARD)
            val c1 = if (index + 1 < body.length) indexValue(body[index + 1], ALPHABET_STANDARD) else -1
            val c2 = if (index + 2 < body.length) indexValue(body[index + 2], ALPHABET_STANDARD) else -1
            val c3 = if (index + 3 < body.length) indexValue(body[index + 3], ALPHABET_STANDARD) else -1
            require(c0 >= 0) { "Illegal Base64 character '${body[index]}'" }
            if (byteIndex < result.size) {
                result[byteIndex++] = (((c0 shl 2) or if (c1 >= 0) (c1 shr 4) else 0)).toByte()
            }
            if (c1 >= 0 && byteIndex < result.size) {
                result[byteIndex++] = (((c1 shl 4) or if (c2 >= 0) (c2 shr 2) else 0)).toByte()
            }
            if (c2 >= 0 && byteIndex < result.size) {
                result[byteIndex++] = (((c2 shl 6) or if (c3 >= 0) c3 else 0)).toByte()
            }
            index += 4
        }
        return result
    }

    /** Decodes [base64] and returns the bytes as a UTF-8 string. */
    fun decodeStr(base64: CharSequence): String = decode(base64).decodeToString()

    /** `true` when [base64] is well-formed Base64 text. */
    fun isBase64(base64: CharSequence?): Boolean {
        if (base64 == null || base64.isEmpty()) return false
        val cleaned = base64.filterNot { it.isWhitespace() }
        if (cleaned.isEmpty() || cleaned.length % 4 != 0) {
            // Without padding a valid length is 2 or 3 modulo 4.
            if (cleaned.length % 4 == 2 || cleaned.length % 4 == 3) {
                return cleaned.all { indexValue(it, ALPHABET_STANDARD) >= 0 || indexValue(it, ALPHABET_URL_SAFE) >= 0 }
            }
            return false
        }
        val body = cleaned.trimEnd('=')
        return body.all { indexValue(it, ALPHABET_STANDARD) >= 0 || indexValue(it, ALPHABET_URL_SAFE) >= 0 }
    }

    /**
     * Encodes [arr] with control over line breaks and the alphabet.
     *
     * @param isMultiLine when `true`, a line break is inserted every 76 characters (MIME form)
     * @param isUrlSafe when `true`, `-` and `_` replace `+` and `/`
     */
    fun encode(arr: ByteArray, isMultiLine: Boolean, isUrlSafe: Boolean): String =
        encodeStr(arr, isMultiLine, isUrlSafe)

    /** Internal common implementation of the three encoding modes. */
    fun encodeStr(arr: ByteArray, isMultiLine: Boolean, isUrlSafe: Boolean): String {
        val alphabet = if (isUrlSafe) ALPHABET_URL_SAFE else ALPHABET_STANDARD
        val builder = StringBuilder(((arr.size + 2) / 3) * 4 + 8)
        var index = 0
        var lineLength = 0
        while (index < arr.size) {
            val b0 = arr[index].toInt() and 0xFF
            val b1 = if (index + 1 < arr.size) arr[index + 1].toInt() and 0xFF else -1
            val b2 = if (index + 2 < arr.size) arr[index + 2].toInt() and 0xFF else -1
            builder.append(alphabet[b0 shr 2])
            builder.append(alphabet[((b0 and 0x03) shl 4) or if (b1 >= 0) b1 shr 4 else 0])
            builder.append(if (b1 >= 0) alphabet[((b1 and 0x0F) shl 2) or if (b2 >= 0) b2 shr 6 else 0] else '=')
            builder.append(if (b2 >= 0) alphabet[b2 and 0x3F] else '=')
            lineLength += 4
            if (isMultiLine && lineLength == 76 && index + 3 < arr.size) {
                builder.append("\r\n")
                lineLength = 0
            }
            index += 3
        }
        return builder.toString()
    }

    private fun indexValue(ch: Char, alphabet: String): Int = when (ch) {
        '=' -> -1
        else -> alphabet.indexOf(ch)
    }
}
