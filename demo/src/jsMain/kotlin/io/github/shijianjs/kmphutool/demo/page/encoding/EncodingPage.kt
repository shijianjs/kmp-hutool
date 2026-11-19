package io.github.shijianjs.kmphutool.demo.page.encoding

import io.github.shijianjs.ktjs.dom.element.invoke
import io.github.shijianjs.ktjs.dom.webcomponent.defineIfMissing
import web.components.CustomElementReference
import web.dom.TagName
import io.github.shijianjs.kmphutool.core.codec.Base64
import io.github.shijianjs.kmphutool.core.comparator.VersionComparator
import io.github.shijianjs.kmphutool.core.lang.Dict
import io.github.shijianjs.kmphutool.core.math.MathUtil
import io.github.shijianjs.kmphutool.core.util.IdUtil
import io.github.shijianjs.kmphutool.demo.page.ResultPage
import io.github.shijianjs.kmphutool.demo.page.ResultRow
import io.github.shijianjs.kmphutool.demo.page.ResultSection
import io.github.shijianjs.kmphutool.demo.page.ResultTexts
import io.github.shijianjs.kmphutool.demo.page.resultTexts

/** One dictionary, so both getStr and getInt read the same object. */
private val dict: Dict = Dict.of("name" to "Ada", "age" to 36)

/** The calls this page demonstrates. */
private val rows: List<ResultRow> = listOf(
    ResultRow("Base64.encode(\"kmp-hutool\")", Base64.encode("kmp-hutool")),
    ResultRow("Base64.decodeStr(Base64.encode(\"Kotlin\"))", Base64.decodeStr(Base64.encode("Kotlin"))),
    ResultRow("IdUtil.getSnowflakeNextId()", IdUtil.getSnowflakeNextId().toString()),
    ResultRow("Dict.getStr(\"name\")", dict.getStr("name") ?: ""),
    ResultRow("Dict.getInt(\"age\")", (dict.getInt("age") ?: 0).toString()),
    ResultRow("MathUtil.combinationCount(5, 2)", MathUtil.combinationCount(5, 2).toString()),
    ResultRow(
        "VersionComparator on 1.0-SNAPSHOT … 2.0",
        listOf("1.0-SNAPSHOT", "1.0", "1.10", "2.0").sortedWith(VersionComparator()).joinToString(" < "),
    ),
)

private val english: ResultTexts = ResultTexts(
    title = "Codes and identifiers",
    summary = "Base64, snowflake ids, a typed dictionary, combinatorics and version ordering.",
    sections = listOf(
        ResultSection(
            title = "Encodings, ids and ordering",
            hint = "The snowflake id is generated when the page is built; VersionComparator sorts " +
                "1.10 after 1.0, which plain string comparison gets wrong.",
            rows = rows,
        ),
    ),
)

private val chinese: ResultTexts = ResultTexts(
    title = "编码与标识",
    summary = "Base64、雪花 id、带类型的字典、组合数，以及版本号排序。",
    sections = listOf(
        ResultSection(
            title = "编码、id 与排序",
            hint = "雪花 id 在页面构造时生成；VersionComparator 把 1.10 排在 1.0 之后，" +
                "而按字符串比较就会排错。",
            rows = rows,
        ),
    ),
)

private val encodingTexts = resultTexts(english, chinese)

/** Every string this page shows, and the calls behind them. */
internal class EncodingPage : ResultPage() {
    init {
        render(encodingTexts)
    }

    companion object : CustomElementReference<EncodingPage>(EncodingPage::class.js) {
        val NAME: TagName<EncodingPage> = TagName("hutool-encoding-page")

        fun define() {
            defineIfMissing(NAME, this)
        }
    }
}

/** Creates a defined `<hutool-encoding-page>`. */
internal fun newEncodingPage(): EncodingPage {
    EncodingPage.define()
    return EncodingPage.NAME()
}

