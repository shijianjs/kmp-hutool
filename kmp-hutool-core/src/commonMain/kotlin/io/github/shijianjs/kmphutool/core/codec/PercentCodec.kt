package io.github.shijianjs.kmphutool.core.codec

import io.github.shijianjs.kmphutool.core.lang.Assert

/**
 * Percent-encoding (URL encoding).
 *
 * Port of `cn.hutool.core.codec.PercentCodec`. `Charset` is not available in `commonMain`, so encoding
 * is always UTF-8; consecutive unsafe characters are encoded as one UTF-8 run, which keeps surrogate
 * pairs (emoji, rare CJK) intact.
 *
 * @hutool cn.hutool.core.codec.PercentCodec
 */
class PercentCodec private constructor(
    private val safeCharacters: BooleanArray = BooleanArray(256),
    private var encodeSpaceAsPlus: Boolean = false,
) {
    /** Marks [c] as safe (not encoded). */
    fun addSafe(c: Char): PercentCodec = apply {
        if (c.code < 256) safeCharacters[c.code] = true
    }

    /** Removes [c] from the safe set. */
    fun removeSafe(c: Char): PercentCodec = apply {
        if (c.code < 256) safeCharacters[c.code] = false
    }

    /** Merges another codec's safe set into this one. */
    fun or(codec: PercentCodec): PercentCodec = apply {
        for (i in safeCharacters.indices) safeCharacters[i] = safeCharacters[i] || codec.safeCharacters[i]
        if (codec.encodeSpaceAsPlus) encodeSpaceAsPlus = true
    }

    /** A new codec holding the union of both safe sets. */
    fun orNew(codec: PercentCodec): PercentCodec = of(this).or(codec)

    /** When `true`, a space becomes `+` (form encoding) instead of `%20`. */
    fun setEncodeSpaceAsPlus(encodeSpaceAsPlus: Boolean): PercentCodec = apply {
        this.encodeSpaceAsPlus = encodeSpaceAsPlus
    }

    /** Encodes [path]; [customSafeChar]s are passed through for this call only. */
    fun encode(path: CharSequence?, vararg customSafeChar: Char): String {
        if (path == null) return ""
        val sb = StringBuilder(path.length)
        val unsafeRun = StringBuilder()
        fun flushRun() {
            if (unsafeRun.isEmpty()) return
            for (b in unsafeRun.toString().encodeToByteArray()) {
                sb.append('%')
                sb.append(HEX[(b.toInt() and 0xF0) ushr 4])
                sb.append(HEX[b.toInt() and 0x0F])
            }
            unsafeRun.clear()
        }
        for (c in path) {
            when {
                isSafe(c) || customSafeChar.contains(c) -> {
                    flushRun()
                    sb.append(c)
                }
                encodeSpaceAsPlus && c == ' ' -> {
                    flushRun()
                    sb.append('+')
                }
                else -> unsafeRun.append(c)
            }
        }
        flushRun()
        return sb.toString()
    }

    private fun isSafe(c: Char): Boolean = c.code < 256 && safeCharacters[c.code]

    companion object {
        private val HEX = "0123456789ABCDEF"

        /** Copies [codec]'s safe set. */
        fun of(codec: PercentCodec): PercentCodec = PercentCodec(codec.safeCharacters.copyOf(), codec.encodeSpaceAsPlus)

        /** A codec treating every char of [chars] as safe. */
        fun of(chars: CharSequence): PercentCodec {
            Assert.notNull(chars, "chars must not be null")
            val codec = PercentCodec()
            chars.forEach { codec.addSafe(it) }
            return codec
        }

        /** A codec with only `A-Za-z0-9` safe, encoding spaces as `%20`. */
        fun default(): PercentCodec = PercentCodec()
    }
}
