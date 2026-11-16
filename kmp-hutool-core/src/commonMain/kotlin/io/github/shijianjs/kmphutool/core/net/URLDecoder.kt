package io.github.shijianjs.kmphutool.core.net

/**
 * `application/x-www-form-urlencoded` decoder working on a UTF-8 payload.
 *
 * Port of `cn.hutool.core.net.URLDecoder`. `Charset` is not available in `commonMain`, so the port
 * decodes UTF-8 only; malformed `%` sequences are copied through verbatim, as upstream does.
 *
 * @hutool cn.hutool.core.net.URLDecoder
 */
object URLDecoder {
    private const val ESCAPE_CHAR = '%'

    /** Decodes for a path: `%20` becomes a space, `+` is kept. */
    fun decodeForPath(str: String?): String? = decode(str, false)

    /** Decodes a form payload: `+` and `%20` become spaces. */
    fun decode(str: String?): String? = decode(str, true)

    /** Decodes [str]; [isPlusToSpace] decides whether `+` means a space. */
    fun decode(str: String?, isPlusToSpace: Boolean): String? {
        if (str == null) return str
        if (str.isEmpty()) return ""
        val bytes = ArrayList<Byte>(str.length)
        var i = 0
        while (i < str.length) {
            val c = str[i]
            when {
                c == '+' && isPlusToSpace -> {
                    bytes.add(' '.code.toByte())
                    i++
                }
                c == ESCAPE_CHAR && i + 2 < str.length -> {
                    val high = digit16(str[i + 1])
                    val low = digit16(str[i + 2])
                    if (high >= 0 && low >= 0) {
                        bytes.add(((high shl 4) + low).toByte())
                        i += 3
                    } else {
                        bytes.add(c.code.toByte())
                        i++
                    }
                }
                else -> {
                    bytes.add(c.code.toByte())
                    i++
                }
            }
        }
        return bytes.toByteArray().decodeToString()
    }

    /** Decodes percent-escapes in [bytes], optionally turning `+` into spaces. */
    fun decode(bytes: ByteArray?, isPlusToSpace: Boolean = true): ByteArray? {
        if (bytes == null) return null
        val out = ByteArray(bytes.size)
        var written = 0
        var i = 0
        while (i < bytes.size) {
            val b = bytes[i]
            when {
                b.toInt() == '+'.code -> {
                    out[written++] = if (isPlusToSpace) ' '.code.toByte() else b
                    i++
                }
                b == ESCAPE_CHAR.code.toByte() -> {
                    if (i + 2 < bytes.size) {
                        val u = digit16(bytes[i + 1].toInt().toChar())
                        val l = digit16(bytes[i + 2].toInt().toChar())
                        if (u >= 0 && l >= 0) {
                            out[written++] = ((u shl 4) + l).toByte()
                            i += 3
                            continue
                        }
                    }
                    out[written++] = b
                    i++
                }
                else -> {
                    out[written++] = b
                    i++
                }
            }
        }
        return out.copyOf(written)
    }

    private fun digit16(ch: Char): Int = when (ch) {
        in '0'..'9' -> ch - '0'
        in 'a'..'f' -> ch - 'a' + 10
        in 'A'..'F' -> ch - 'A' + 10
        else -> -1
    }
}
