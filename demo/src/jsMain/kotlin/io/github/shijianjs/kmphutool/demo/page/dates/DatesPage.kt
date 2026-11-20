package io.github.shijianjs.kmphutool.demo.page.dates

import io.github.shijianjs.ktjs.dom.element.invoke
import io.github.shijianjs.ktjs.dom.webcomponent.CustomElementReferenceAdapter
import io.github.shijianjs.kmphutool.core.convert.NumberChineseFormatter
import io.github.shijianjs.kmphutool.core.date.BetweenFormatter
import io.github.shijianjs.kmphutool.core.date.ChineseDate
import io.github.shijianjs.kmphutool.core.date.DateUnit
import io.github.shijianjs.kmphutool.demo.page.ResultPage
import io.github.shijianjs.kmphutool.demo.page.ResultRow
import io.github.shijianjs.kmphutool.demo.page.ResultSection
import io.github.shijianjs.kmphutool.demo.page.ResultTexts
import io.github.shijianjs.kmphutool.demo.page.resultTexts

/** One lunar date, so the term and the festivals come from the same object. */
private val lunar: ChineseDate = ChineseDate(2024, 1, 1)

/** The calls this page demonstrates. */
private val rows: List<ResultRow> = listOf(
    ResultRow("ChineseDate(2024, 1, 1)", lunar.toString()),
    ResultRow("ChineseDate.getTerm()", lunar.getTerm()),
    ResultRow("ChineseDate.getFestivals()", lunar.getFestivals()),
    ResultRow("DateUnit.HOUR.getMillis()", DateUnit.HOUR.getMillis().toString()),
    ResultRow(
        "BetweenFormatter(90_000, SECOND).format()",
        BetweenFormatter(90_000L, BetweenFormatter.Level.SECOND).format(),
    ),
    ResultRow("NumberChineseFormatter.format(1234.56, …)", NumberChineseFormatter.format(1234.56, true, true)),
)

private val english: ResultTexts = ResultTexts(
    title = "Dates and money",
    summary = "The lunar calendar, solar terms and festivals, durations, and amounts written in Chinese.",
    sections = listOf(
        ResultSection(
            title = "Lunar calendar and formatting",
            hint = "ChineseDate converts the solar date and carries the term (节气) and the festivals of " +
                "that day; the last row is an amount spelled out, which is what a Chinese invoice needs.",
            rows = rows,
        ),
    ),
)

private val chinese: ResultTexts = ResultTexts(
    title = "日期与金额",
    summary = "农历、节气与节日，时长，以及用中文大写写出来的金额。",
    sections = listOf(
        ResultSection(
            title = "农历与格式化",
            hint = "ChineseDate 把公历日期转换过去，并带上那一天的节气与节日；最后一行是金额的中文写法，" +
                "发票要的正是这个。",
            rows = rows,
        ),
    ),
)

private val datesTexts = resultTexts(english, chinese)

/** Every string this page shows, and the calls behind them. */
internal class DatesPage : ResultPage() {
    init {
        render(datesTexts)
    }

    companion object : CustomElementReferenceAdapter<DatesPage>(DatesPage::class.js, "hutool-dates-page")
}

