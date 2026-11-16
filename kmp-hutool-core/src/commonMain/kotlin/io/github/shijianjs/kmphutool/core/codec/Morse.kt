package io.github.shijianjs.kmphutool.core.codec

/**
 * Morse code encoder/decoder, binary `0`/`1` alphabet with configurable dit/dah/split characters.
 *
 * Port of `cn.hutool.core.codec.Morse`, originally from Java-MorseCoder. `java.lang.StringBuilder`
 * code-point APIs are JVM-only here, so code points (including surrogate pairs) are walked manually and
 * re-assembled on decode.
 *
 * @hutool cn.hutool.core.codec.Morse
 */
class Morse(
    private val dit: Char = '.',
    private val dah: Char = '-',
    private val split: Char = '/',
) {
    fun encode(text: String): String {
        val builder = StringBuilder()
        val upper = text.uppercase()
        var index = 0
        while (index < upper.length) {
            val codePoint = codePointAtChar(upper, index)
            index += if (codePoint > 0xFFFF) 2 else 1
            val word = ALPHABETS[codePoint] ?: codePoint.toString(2)
            for (ch in word) {
                builder.append(if (ch == '0') dit else dah)
            }
            builder.append(split)
        }
        return builder.toString()
    }

    fun decode(morse: String): String {
        if (!morse.all { it == dit || it == dah || it == split }) {
            throw IllegalArgumentException("Incorrect morse.")
        }
        val builder = StringBuilder()
        for (word in morse.split(split)) {
            if (word.isEmpty()) continue
            val binary = buildString(word.length) {
                for (ch in word) append(if (ch == dit) '0' else '1')
            }
            val codePoint = DICTIONARIES[binary] ?: binary.toInt(2)
            appendCodePoint(builder, codePoint)
        }
        return builder.toString()
    }

    private fun codePointAtChar(text: String, index: Int): Int {
        val high = text[index]
        if (high.isHighSurrogate() && index + 1 < text.length) {
            val low = text[index + 1]
            if (low.isLowSurrogate()) {
                return 0x10000 + ((high.code - 0xD800) shl 10) + (low.code - 0xDC00)
            }
        }
        return high.code
    }

    private fun appendCodePoint(builder: StringBuilder, codePoint: Int) {
        if (codePoint <= 0xFFFF) {
            builder.append(codePoint.toChar())
        } else {
            val value = codePoint - 0x10000
            builder.append((0xD800 + (value shr 10)).toChar())
            builder.append((0xDC00 + (value and 0x3FF)).toChar())
        }
    }

    companion object {
        private val ALPHABETS = HashMap<Int, String>()
        private val DICTIONARIES = HashMap<String, Int>()

        private fun register(ch: Char, code: String) {
            ALPHABETS[ch.code] = code
            DICTIONARIES[code] = ch.code
        }

        init {
            register('A', "01"); register('B', "1000"); register('C', "1010"); register('D', "100")
            register('E', "0"); register('F', "0010"); register('G', "110"); register('H', "0000")
            register('I', "00"); register('J', "0111"); register('K', "101"); register('L', "0100")
            register('M', "11"); register('N', "10"); register('O', "111"); register('P', "0110")
            register('Q', "1101"); register('R', "010"); register('S', "000"); register('T', "1")
            register('U', "001"); register('V', "0001"); register('W', "011"); register('X', "1001")
            register('Y', "1011"); register('Z', "1100")
            register('0', "11111"); register('1', "01111"); register('2', "00111"); register('3', "00011")
            register('4', "00001"); register('5', "00000"); register('6', "10000"); register('7', "11000")
            register('8', "11100"); register('9', "11110")
            register('.', "010101"); register(',', "110011"); register('?', "001100"); register('\'', "011110")
            register('!', "101011"); register('/', "10010"); register('(', "10110"); register(')', "101101")
            register('&', "01000"); register(':', "111000"); register(';', "101010"); register('=', "10001")
            register('+', "01010"); register('-', "100001"); register('_', "001101"); register('"', "010010")
            register('$', "0001001"); register('@', "011010")
        }
    }
}


