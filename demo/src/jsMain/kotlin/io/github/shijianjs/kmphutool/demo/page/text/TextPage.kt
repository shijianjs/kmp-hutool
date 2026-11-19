package io.github.shijianjs.kmphutool.demo.page.text

import io.github.shijianjs.kmphutool.core.text.StrFormatter
import io.github.shijianjs.kmphutool.core.util.ReUtil
import io.github.shijianjs.kmphutool.core.util.StrUtil
import io.github.shijianjs.kmphutool.demo.page.ResultPage
import io.github.shijianjs.kmphutool.demo.page.ResultRow
import io.github.shijianjs.kmphutool.demo.page.ResultSection
import io.github.shijianjs.kmphutool.demo.page.ResultTexts
import io.github.shijianjs.kmphutool.demo.page.resultTexts
import io.github.shijianjs.ktjs.demo.nav.Page

/** The calls this page demonstrates; every value is computed here, once, by the port itself. */
private val rows: List<ResultRow> = listOf(
    ResultRow("StrUtil.format(\"{} + {} = {}\", 1, 2, 3)", StrUtil.format("{} + {} = {}", 1, 2, 3)),
    ResultRow("StrUtil.upperFirst(\"kotlin\")", StrUtil.upperFirst("kotlin")),
    ResultRow("StrUtil.splitTrim(\"a, b ,c\", \",\")", StrUtil.splitTrim("a, b ,c", ",").joinToString(" | ")),
    ResultRow("StrFormatter.format(\"Hello {name}!\", …)", StrFormatter.format("Hello {name}!", mapOf("name" to "Kotlin"))),
    ResultRow("StrUtil.wrap(\"kmp\", \"<\", \">\")", StrUtil.wrap("kmp", "<", ">")),
    ResultRow("ReUtil.replaceAll(\"\\\\d+\", \"a1b2c3\", \"#\")", ReUtil.replaceAll("\\d+", "a1b2c3", "#")),
)

private val english: ResultTexts = ResultTexts(
    title = "Strings",
    summary = "Formatting, splitting and replacing, as Hutool's StrUtil, StrFormatter and ReUtil do them.",
    sections = listOf(
        ResultSection(
            title = "Formatting and splitting",
            hint = "Placeholder syntax is Hutool's, not Kotlin's: {} for positional, {name} for a map.",
            rows = rows,
        ),
    ),
)

private val chinese: ResultTexts = ResultTexts(
    title = "字符串",
    summary = "格式化、切分与替换，对应 Hutool 的 StrUtil、StrFormatter 与 ReUtil。",
    sections = listOf(
        ResultSection(
            title = "格式化与切分",
            hint = "占位符沿用 Hutool 的语法，不是 Kotlin 的：{} 是位置参数，{name} 取 map 里的键。",
            rows = rows,
        ),
    ),
)

private val textTexts = resultTexts(english, chinese)

/** Every string this page shows, and the calls behind them. */
internal class TextPage : Page by ResultPage(textTexts)
