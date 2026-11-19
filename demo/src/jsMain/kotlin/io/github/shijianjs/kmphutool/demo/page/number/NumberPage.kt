package io.github.shijianjs.kmphutool.demo.page.number

import io.github.shijianjs.kmphutool.core.util.NumberUtil
import io.github.shijianjs.kmphutool.demo.page.ResultPage
import io.github.shijianjs.kmphutool.demo.page.ResultRow
import io.github.shijianjs.kmphutool.demo.page.ResultSection
import io.github.shijianjs.kmphutool.demo.page.ResultTexts
import io.github.shijianjs.kmphutool.demo.page.resultTexts
import io.github.shijianjs.ktjs.demo.nav.Page

/** The calls this page demonstrates, each returning a formatted value rather than a raw double. */
private val rows: List<ResultRow> = listOf(
    ResultRow("NumberUtil.round(3.14159, 2)", NumberUtil.round(3.14159, 2).toString()),
    ResultRow("NumberUtil.format(1234567.891, 2)", NumberUtil.format(1_234_567.891, 2)),
    ResultRow("NumberUtil.isNumber(\"1e3\")", NumberUtil.isNumber("1e3").toString()),
    ResultRow("NumberUtil.add(0.1, 0.2)", NumberUtil.add(0.1, 0.2).toString()),
    ResultRow("NumberUtil.ceilDiv(7, 2)", NumberUtil.ceilDiv(7, 2).toString()),
)

private val english: ResultTexts = ResultTexts(
    title = "Numbers",
    summary = "Rounding, formatting and the arithmetic that avoids binary floating-point surprises.",
    sections = listOf(
        ResultSection(
            title = "Rounding and arithmetic",
            hint = "NumberUtil.add does what 0.1 + 0.2 cannot: it works on the decimal value, so the " +
                "result is 0.3 and not 0.30000000000000004.",
            rows = rows,
        ),
    ),
)

private val chinese: ResultTexts = ResultTexts(
    title = "数字",
    summary = "取整、格式化，以及避开二进制浮点误差的算术运算。",
    sections = listOf(
        ResultSection(
            title = "取整与算术",
            hint = "NumberUtil.add 做的事 0.1 + 0.2 做不到：它按十进制值计算，结果是 0.3，" +
                "而不是 0.30000000000000004。",
            rows = rows,
        ),
    ),
)

private val numberTexts = resultTexts(english, chinese)

/** Every string this page shows, and the calls behind them. */
internal class NumberPage : Page by ResultPage(numberTexts)
