package io.github.shijianjs.kmphutool.demo.page.china

import io.github.shijianjs.ktjs.dom.element.invoke
import io.github.shijianjs.ktjs.dom.webcomponent.CustomElementReferenceAdapter
import io.github.shijianjs.kmphutool.core.util.CreditCodeUtil
import io.github.shijianjs.kmphutool.core.util.DesensitizedUtil
import io.github.shijianjs.kmphutool.core.util.IdcardUtil
import io.github.shijianjs.kmphutool.core.util.PhoneUtil
import io.github.shijianjs.kmphutool.demo.page.ResultPage
import io.github.shijianjs.kmphutool.demo.page.ResultRow
import io.github.shijianjs.kmphutool.demo.page.ResultSection
import io.github.shijianjs.kmphutool.demo.page.ResultTexts
import io.github.shijianjs.kmphutool.demo.page.resultTexts

/** A well-formed mainland id card, and the inputs the masking helpers take. */
private const val ID_CARD: String = "11010519491231002X"

/** The calls this page demonstrates. */
private val rows: List<ResultRow> = listOf(
    ResultRow("IdcardUtil.isValidCard(\"$ID_CARD\")", IdcardUtil.isValidCard(ID_CARD).toString()),
    ResultRow("IdcardUtil.getBirthByIdCard(…)", IdcardUtil.getBirthByIdCard(ID_CARD) ?: ""),
    ResultRow("IdcardUtil.getAgeByIdCard(…)", IdcardUtil.getAgeByIdCard(ID_CARD).toString()),
    ResultRow("IdcardUtil.getProvinceByIdCard(…)", IdcardUtil.getProvinceByIdCard(ID_CARD) ?: ""),
    ResultRow("PhoneUtil.hideBetween(\"13812345678\")", PhoneUtil.hideBetween("13812345678")),
    ResultRow("DesensitizedUtil.bankCard(\"6222021234567890\")", DesensitizedUtil.bankCard("6222021234567890")),
    ResultRow("CreditCodeUtil.randomCreditCode()", CreditCodeUtil.randomCreditCode()),
)

private val english: ResultTexts = ResultTexts(
    title = "China-specific helpers",
    summary = "Id-card parsing, phone and bank-card masking, and the unified social credit code — the " +
        "parts of Hutool that only make sense in mainland formats.",
    sections = listOf(
        ResultSection(
            title = "Parsing and masking",
            hint = "The id card is the library's own sample number; the credit code is randomised on every " +
                "build, so it differs between reloads.",
            rows = rows,
        ),
    ),
)

private val chinese: ResultTexts = ResultTexts(
    title = "中文相关工具",
    summary = "身份证解析、手机号与银行卡脱敏，以及统一社会信用代码 —— Hutool 里只在中国大陆格式下成立的那部分。",
    sections = listOf(
        ResultSection(
            title = "解析与脱敏",
            hint = "身份证号是库自带的示例；信用代码每次构建随机生成，所以刷新页面会变。",
            rows = rows,
        ),
    ),
)

private val chinaTexts = resultTexts(english, chinese)

/** Every string this page shows, and the calls behind them. */
internal class ChinaPage : ResultPage() {
    init {
        render(chinaTexts)
    }

    companion object : CustomElementReferenceAdapter<ChinaPage>(ChinaPage::class.js, "hutool-china-page")
}

