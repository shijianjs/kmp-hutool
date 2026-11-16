package io.github.shijianjs.kmphutool.core.text

/**
 * Characters and strings that the utilities compare against, kept in one place so that call sites
 * never inline a literal that could drift from the one used in a parser.
 *
 * Port of `cn.hutool.core.text.StrPool` and `cn.hutool.core.text.CharPool`.
 *
 * @hutool cn.hutool.core.text.StrPool
 * @hutool cn.hutool.core.text.CharPool
 */
object StrPool {
    /** A single space. */
    const val SPACE: Char = ' '

    /** A single tab. */
    const val TAB: Char = '\t'

    /** A dot. */
    const val DOT: Char = '.'

    /** A forward slash. */
    const val SLASH: Char = '/'

    /** A backslash. */
    const val BACKSLASH: Char = '\\'

    /** Carriage return. */
    const val CR: Char = '\r'

    /** Line feed. */
    const val LF: Char = '\n'

    /** A hyphen. */
    const val DASHED: Char = '-'

    /** An underscore. */
    const val UNDERLINE: Char = '_'

    /** A comma. */
    const val COMMA: Char = ','

    /** The opening brace of a placeholder. */
    const val DELIM_START: Char = '{'

    /** The closing brace of a placeholder. */
    const val DELIM_END: Char = '}'

    /** An opening square bracket. */
    const val BRACKET_START: Char = '['

    /** A closing square bracket. */
    const val BRACKET_END: Char = ']'

    /** A double quote. */
    const val DOUBLE_QUOTES: Char = '"'

    /** A single quote. */
    const val SINGLE_QUOTE: Char = '\''

    /** An ampersand. */
    const val AMP: Char = '&'

    /** A colon. */
    const val COLON: Char = ':'

    /** An at sign. */
    const val AT: Char = '@'

    /** An empty string. */
    const val EMPTY = ""

    /** A single space, as a string. */
    const val SPACE_STR = " "

    /** A tab, as a string. */
    const val TAB_STR = "\t"

    /** A dot, as a string. */
    const val DOT_STR = "."

    /** Two dots, as a string. */
    const val DOUBLE_DOT = ".."

    /** A forward slash, as a string. */
    const val SLASH_STR = "/"

    /** A backslash, as a string. */
    const val BACKSLASH_STR = "\\"

    /** Carriage return, as a string. */
    const val CR_STR = "\r"

    /** Line feed, as a string. */
    const val LF_STR = "\n"

    /** A CRLF pair. */
    const val CRLF = "\r\n"

    /** An underscore, as a string. */
    const val UNDERLINE_STR = "_"

    /** A hyphen, as a string. */
    const val DASHED_STR = "-"

    /** A comma, as a string. */
    const val COMMA_STR = ","

    /** The opening brace of a placeholder, as a string. */
    const val DELIM_START_STR = "{"

    /** The closing brace of a placeholder, as a string. */
    const val DELIM_END_STR = "}"

    /** An opening square bracket, as a string. */
    const val BRACKET_START_STR = "["

    /** A closing square bracket, as a string. */
    const val BRACKET_END_STR = "]"

    /** A colon, as a string. */
    const val COLON_STR = ":"

    /** An at sign, as a string. */
    const val AT_STR = "@"

    /** The `&nbsp;` entity. */
    const val HTML_NBSP = "&nbsp;"

    /** The `&amp;` entity. */
    const val HTML_AMP = "&amp;"

    /** The `&quot;` entity. */
    const val HTML_QUOTE = "&quot;"

    /** The `&apos;` entity. */
    const val HTML_APOS = "&apos;"

    /** The `&lt;` entity. */
    const val HTML_LT = "&lt;"

    /** The `&gt;` entity. */
    const val HTML_GT = "&gt;"

    /** A pair of braces: the empty JSON object literal. */
    const val EMPTY_JSON = "{}"

    /** The UTF-8 charset name; the only charset this port encodes with. */
    const val UTF_8 = "UTF-8"
}
