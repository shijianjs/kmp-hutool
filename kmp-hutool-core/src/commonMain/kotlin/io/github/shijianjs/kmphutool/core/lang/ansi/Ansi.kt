package io.github.shijianjs.kmphutool.core.lang.ansi

/**
 * Distinguishes a foreground colour from a background colour.
 *
 * @hutool cn.hutool.core.lang.ansi.ForeOrBack
 */
enum class ForeOrBack {
    /** Foreground. */
    FORE,

    /** Background. */
    BACK,
}

/**
 * A node that can be rendered as an ANSI escape sequence.
 *
 * Port of `cn.hutool.core.lang.ansi.AnsiElement` (Spring Boot lineage).
 *
 * @hutool cn.hutool.core.lang.ansi.AnsiElement
 */
interface AnsiElement {
    /** The numeric ANSI code, `-1` when the element is not a single code. */
    val code: Int
        get() = -1

    /** [AnsiEncoder] concatenates this into the escape sequence. */
    override fun toString(): String
}

/**
 * ANSI text styles.
 *
 * Port of `cn.hutool.core.lang.ansi.AnsiStyle`.
 *
 * @hutool cn.hutool.core.lang.ansi.AnsiStyle
 */
enum class AnsiStyle(override val code: Int) : AnsiElement {
    /** Reset / normal. */
    NORMAL(0),

    /** Bold or increased intensity. */
    BOLD(1),

    /** Faint, decreased intensity. */
    FAINT(2),

    /** Italic. */
    ITALIC(3),

    /** Underline. */
    UNDERLINE(4);

    override fun toString(): String = code.toString()
}

/**
 * The 16 standard foreground colours.
 *
 * Port of `cn.hutool.core.lang.ansi.AnsiColor`.
 *
 * @hutool cn.hutool.core.lang.ansi.AnsiColor
 */
enum class AnsiColor(override val code: Int) : AnsiElement {
    DEFAULT(39),
    BLACK(30),
    RED(31),
    GREEN(32),
    YELLOW(33),
    BLUE(34),
    MAGENTA(35),
    CYAN(36),
    WHITE(37),
    BRIGHT_BLACK(90),
    BRIGHT_RED(91),
    BRIGHT_GREEN(92),
    BRIGHT_YELLOW(93),
    BRIGHT_BLUE(94),
    BRIGHT_MAGENTA(95),
    BRIGHT_CYAN(96),
    BRIGHT_WHITE(97);

    override fun toString(): String = code.toString()
}

/**
 * The 16 standard background colours.
 *
 * Port of `cn.hutool.core.lang.ansi.AnsiBackground`.
 *
 * @hutool cn.hutool.core.lang.ansi.AnsiBackground
 */
enum class AnsiBackground(override val code: Int) : AnsiElement {
    DEFAULT(49),
    BLACK(40),
    RED(41),
    GREEN(42),
    YELLOW(43),
    BLUE(44),
    MAGENTA(45),
    CYAN(46),
    WHITE(47),
    BRIGHT_BLACK(100),
    BRIGHT_RED(101),
    BRIGHT_GREEN(102),
    BRIGHT_YELLOW(103),
    BRIGHT_BLUE(104),
    BRIGHT_MAGENTA(105),
    BRIGHT_CYAN(106),
    BRIGHT_WHITE(107);

    override fun toString(): String = code.toString()
}

/**
 * An 8-bit (256 colour) foreground or background colour.
 *
 * Port of `cn.hutool.core.lang.ansi.Ansi8BitColor`.
 *
 * @hutool cn.hutool.core.lang.ansi.Ansi8BitColor
 */
class Ansi8BitColor private constructor(
    private val prefix: String,
    override val code: Int,
) : AnsiElement {
    override fun toString(): String = prefix + code

    override fun equals(other: Any?): Boolean =
        other is Ansi8BitColor && prefix == other.prefix && code == other.code

    override fun hashCode(): Int = prefix.hashCode() * 31 + code

    companion object {
        private const val PREFIX_FORE = "38;5;"
        private const val PREFIX_BACK = "48;5;"

        /** A foreground 8-bit colour; [code] must be 0..255. */
        fun foreground(code: Int): Ansi8BitColor {
            require(code in 0..255) { "Code must be between 0 and 255" }
            return Ansi8BitColor(PREFIX_FORE, code)
        }

        /** A background 8-bit colour; [code] must be 0..255. */
        fun background(code: Int): Ansi8BitColor {
            require(code in 0..255) { "Code must be between 0 and 255" }
            return Ansi8BitColor(PREFIX_BACK, code)
        }
    }
}

/**
 * Colour bit depth used by [AnsiColorWrapper].
 *
 * Kotlin counterpart of `AnsiColors.BitDepth`; `AnsiColors` itself maps to AWT `Color` and is
 * therefore out of scope, so the enum lives here.
 *
 * @hutool cn.hutool.core.lang.ansi.AnsiColors.BitDepth
 */
enum class BitDepth(val bits: Int) {
    /** 4-bit, 16 colours. */
    FOUR(4),

    /** 8-bit, 256 colours. */
    EIGHT(8);

    companion object {
        /** Resolves a bit depth from its bit count. */
        fun of(bits: Int): BitDepth =
            entries.firstOrNull { it.bits == bits }
                ?: throw IllegalArgumentException("Unsupported ANSI bit depth '$bits'")
    }
}

/**
 * Pairs a colour code with its bit depth so it can be turned into an [AnsiElement].
 *
 * Port of `cn.hutool.core.lang.ansi.AnsiColorWrapper`.
 *
 * @hutool cn.hutool.core.lang.ansi.AnsiColorWrapper
 */
class AnsiColorWrapper(val code: Int, val bitDepth: BitDepth) {
    init {
        if (bitDepth == BitDepth.FOUR) {
            require(code in 30..37 || code in 90..97) {
                "The value of 4 bit color only supported [30~37],[90~97]."
            }
        }
        require(code in 0..255) { "The value of 8 bit color only supported [0~255]." }
    }

    /** Materialises the matching foreground or background element. */
    fun toAnsiElement(foreOrBack: ForeOrBack): AnsiElement {
        if (bitDepth == BitDepth.FOUR) {
            if (foreOrBack == ForeOrBack.FORE) {
                return AnsiColor.entries.firstOrNull { it.code == code }
                    ?: throw IllegalArgumentException("No matched AnsiColor instance,code=$code")
            }
            return AnsiBackground.entries.firstOrNull { it.code == code + 10 }
                ?: throw IllegalArgumentException("No matched AnsiBackground instance,code=$code")
        }
        return if (foreOrBack == ForeOrBack.FORE) Ansi8BitColor.foreground(code) else Ansi8BitColor.background(code)
    }

    override fun equals(other: Any?): Boolean =
        other is AnsiColorWrapper && code == other.code && bitDepth == other.bitDepth

    override fun hashCode(): Int = code * 31 + bitDepth.hashCode()
}

/**
 * Builds ANSI escape sequences from mixed text and [AnsiElement]s.
 *
 * Port of `cn.hutool.core.lang.ansi.AnsiEncoder`.
 *
 * @hutool cn.hutool.core.lang.ansi.AnsiEncoder
 */
object AnsiEncoder {
    private const val ENCODE_JOIN = ";"
    private const val ENCODE_START = "\u001B["
    private const val ENCODE_END = "m"
    private const val RESET = "0;39"

    /** Renders [elements] as one ANSI-escaped string, resetting attributes at the end. */
    fun encode(vararg elements: Any?): String {
        val sb = StringBuilder()
        buildEnabled(sb, elements)
        return sb.toString()
    }

    private fun buildEnabled(sb: StringBuilder, elements: Array<out Any?>) {
        var writingAnsi = false
        var containsEncoding = false
        for (element in elements) {
            if (element == null) continue
            if (element is AnsiElement) {
                containsEncoding = true
                if (writingAnsi) sb.append(ENCODE_JOIN) else {
                    sb.append(ENCODE_START)
                    writingAnsi = true
                }
            } else if (writingAnsi) {
                sb.append(ENCODE_END)
                writingAnsi = false
            }
            sb.append(element)
        }
        if (containsEncoding) {
            sb.append(if (writingAnsi) ENCODE_JOIN else ENCODE_START)
            sb.append(RESET)
            sb.append(ENCODE_END)
        }
    }
}
