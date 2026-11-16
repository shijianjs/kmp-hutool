package io.github.shijianjs.kmphutool.core.json.xml

/**
 * Options for XML-to-JSON conversion.
 *
 * Port of `cn.hutool.json.xml.ParseConfig`.
 *
 * @hutool cn.hutool.json.xml.ParseConfig
 */
class ParseConfig {
    var keepStrings: Boolean = false
    var maxNestingDepth: Int = 512

    companion object {
        fun of(): ParseConfig = ParseConfig()
    }
}
