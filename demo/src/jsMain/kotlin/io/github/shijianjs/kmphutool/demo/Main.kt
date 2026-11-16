package io.github.shijianjs.kmphutool.demo

import io.github.shijianjs.kmphutool.core.codec.Base64
import io.github.shijianjs.kmphutool.core.collection.CollUtil
import io.github.shijianjs.kmphutool.core.lang.Dict
import io.github.shijianjs.kmphutool.core.math.MathUtil
import io.github.shijianjs.kmphutool.core.text.StrFormatter
import io.github.shijianjs.kmphutool.core.util.CreditCodeUtil
import io.github.shijianjs.kmphutool.core.util.DesensitizedUtil
import io.github.shijianjs.kmphutool.core.util.IdUtil
import io.github.shijianjs.kmphutool.core.util.IdcardUtil
import io.github.shijianjs.kmphutool.core.util.NumberUtil
import io.github.shijianjs.kmphutool.core.util.PhoneUtil
import io.github.shijianjs.kmphutool.core.util.ReUtil
import io.github.shijianjs.kmphutool.core.util.StrUtil
import io.github.shijianjs.kmphutool.core.comparator.VersionComparator
import io.github.shijianjs.kmphutool.core.convert.NumberChineseFormatter
import io.github.shijianjs.kmphutool.core.date.BetweenFormatter
import io.github.shijianjs.kmphutool.core.date.ChineseDate
import io.github.shijianjs.kmphutool.core.date.DateUnit
import kotlinx.browser.document
import org.w3c.dom.HTMLElement

/**
 * The demo page: every value below is computed by `kmp-hutool-core` in the browser, and the whole
 * layout is rebuilt when the language button is pressed.
 */
private enum class Locale(val toggle: String, val title: String, val subtitle: String) {
    EN("中文", "kmp-hutool demo", "Every value on this page is computed by kmp-hutool-core in your browser."),
    ZH("English", "kmp-hutool 演示", "本页所有结果都由 kmp-hutool-core 在浏览器中实时计算。"),
}

private var locale = Locale.EN

fun main() {
    render()
}

private fun render() {
    val root = document.getElementById("app") as? HTMLElement ?: return
    root.textContent = ""

    root.appendChild(header())
    root.appendChild(textSection())
    root.appendChild(numberSection())
    root.appendChild(collectionSection())
    root.appendChild(codeSection())
    root.appendChild(chinaSection())
    root.appendChild(footer())
}

private fun header(): HTMLElement {
    val header = element("header", "hero")
    header.appendChild(text("h1", null, locale.title))
    header.appendChild(text("p", "subtitle", locale.subtitle))

    val button = element("button", "lang-toggle")
    button.textContent = locale.toggle
    button.addEventListener("click", {
        locale = if (locale == Locale.EN) Locale.ZH else Locale.EN
        render()
    })
    header.appendChild(button)
    return header
}

private fun textSection(): HTMLElement {
    val rows = listOf(
        "StrUtil.format" to StrUtil.format("{} + {} = {}", 1, 2, 3),
        "StrUtil.upperFirst" to StrUtil.upperFirst("kotlin"),
        "StrUtil.splitTrim" to StrUtil.splitTrim("a, b ,c", ",").joinToString(" | "),
        "StrFormatter.format" to StrFormatter.format("Hello {name}!", mapOf("name" to "Kotlin")),
        "StrUtil.wrap" to StrUtil.wrap("kmp", "<", ">"),
        "ReUtil.replaceAll" to ReUtil.replaceAll("\\d+", "a1b2c3", "#"),
    )
    return section("Text", rows)
}

private fun numberSection(): HTMLElement {
    val rows = listOf(
        "NumberUtil.round" to NumberUtil.round(3.14159, 2).toString(),
        "NumberUtil.format" to NumberUtil.format(1_234_567.891, 2),
        "NumberUtil.isNumber" to NumberUtil.isNumber("1e3").toString(),
        "NumberUtil.add" to NumberUtil.add(0.1, 0.2).toString(),
        "NumberUtil.ceilDiv" to NumberUtil.ceilDiv(7, 2).toString(),
    )
    return section("Number", rows)
}

private fun collectionSection(): HTMLElement {
    val rows = listOf(
        "CollUtil.join" to CollUtil.join(listOf(1, 2, 3), "-"),
        "CollUtil.distinct" to CollUtil.distinct(listOf(1, 1, 2, 3)).joinToString(", "),
        "CollUtil.union" to CollUtil.union(listOf(1, 2), listOf(2, 3)).joinToString(", "),
        "CollUtil.isEmpty" to CollUtil.isEmpty(emptyList<String>()).toString(),
        "ListUtil.partition" to listOf(1, 2, 3, 4, 5)
            .chunked(2)
            .joinToString(" | ") { it.joinToString(",", prefix = "[", postfix = "]") },
    )
    return section("Collection", rows)
}

private fun codeSection(): HTMLElement {
    val dict = Dict.of("name" to "Ada", "age" to 36)
    val rows = listOf(
        "Base64.encode" to Base64.encode("kmp-hutool"),
        "Base64.decode" to Base64.decodeStr(Base64.encode("Kotlin")),
        "IdUtil.getSnowflakeNextId" to IdUtil.getSnowflakeNextId().toString(),
        "Dict.getStr" to (dict.getStr("name") ?: ""),
        "Dict.getInt" to (dict.getInt("age") ?: 0).toString(),
        "MathUtil.combinationCount" to MathUtil.combinationCount(5, 2).toString(),
        "VersionComparator" to versionComparison(),
    )
    return section("Code, ids, math", rows)
}

private fun chinaSection(): HTMLElement {
    val idCard = "11010519491231002X"
    val rows = listOf(
        "IdcardUtil.isValidCard" to IdcardUtil.isValidCard(idCard).toString(),
        "IdcardUtil.getBirthByIdCard" to (IdcardUtil.getBirthByIdCard(idCard) ?: ""),
        "IdcardUtil.getAgeByIdCard" to IdcardUtil.getAgeByIdCard(idCard).toString(),
        "IdcardUtil.getProvinceByIdCard" to (IdcardUtil.getProvinceByIdCard(idCard) ?: ""),
        "PhoneUtil.hideBetween" to PhoneUtil.hideBetween("13812345678"),
        "DesensitizedUtil.bankCard" to DesensitizedUtil.bankCard("6222021234567890"),
        "CreditCodeUtil.randomCreditCode" to CreditCodeUtil.randomCreditCode(),
    )
    return section("China-specific helpers", rows)
}

private fun dateSection(): HTMLElement {
    val lunar = ChineseDate(2024, 1, 1)
    val rows = listOf(
        "ChineseDate" to lunar.toString(),
        "ChineseDate.getTerm" to lunar.getTerm(),
        "ChineseDate.getFestivals" to lunar.getFestivals(),
        "DateUnit.HOUR.millis" to DateUnit.HOUR.getMillis().toString(),
        "BetweenFormatter" to BetweenFormatter(90_000L, BetweenFormatter.Level.SECOND).format(),
        "NumberChineseFormatter.money" to NumberChineseFormatter.format(1234.56, true, true),
    )
    return section("Dates, lunar calendar, money", rows)
}

private fun versionComparison(): String {
    val comparator = VersionComparator()
    val ordered = listOf("1.0-SNAPSHOT", "1.0", "1.10", "2.0")
        .sortedWith(comparator)
        .joinToString(" < ")
    return ordered
}

private fun footer(): HTMLElement {
    val footer = element("footer", "footer")
    val docs = element("a", null)
    docs.setAttribute("href", "../")
    docs.textContent = "Docs / 文档"
    footer.appendChild(docs)
    return footer
}

private fun section(title: String, rows: List<Pair<String, String>>): HTMLElement {
    val section = element("section", "card")
    section.appendChild(text("h2", null, title))
    val list = element("ol", "rows")
    for ((name, value) in rows) {
        val row = element("li", "row")
        row.appendChild(text("code", "name", name))
        row.appendChild(text("span", "value", value))
        list.appendChild(row)
    }
    section.appendChild(list)
    return section
}

private fun element(tag: String, className: String?): HTMLElement {
    val node = document.createElement(tag) as HTMLElement
    if (className != null) node.className = className
    return node
}

private fun text(tag: String, className: String?, content: String): HTMLElement {
    val node = element(tag, className)
    node.textContent = content
    return node
}
