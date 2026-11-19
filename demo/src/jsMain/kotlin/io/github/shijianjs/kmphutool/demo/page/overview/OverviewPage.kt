package io.github.shijianjs.kmphutool.demo.page.overview

import io.github.shijianjs.kmphutool.demo.API_URL
import io.github.shijianjs.kmphutool.demo.DOCS_URL
import io.github.shijianjs.kmphutool.demo.SOURCE_URL
import io.github.shijianjs.ktjs.demo.i18n.Binder
import io.github.shijianjs.ktjs.demo.i18n.Lang
import io.github.shijianjs.ktjs.demo.i18n.Langs
import io.github.shijianjs.ktjs.demo.i18n.Translations
import io.github.shijianjs.ktjs.demo.i18n.texts
import io.github.shijianjs.ktjs.demo.nav.Page
import io.github.shijianjs.ktjs.demo.page.anchor
import io.github.shijianjs.ktjs.demo.page.newPage
import io.github.shijianjs.ktjs.demo.page.paragraph
import io.github.shijianjs.ktjs.demo.page.sectionBlock
import io.github.shijianjs.ktjs.dom.element.invoke
import io.github.shijianjs.ktjs.dom.element.unaryPlus
import web.html.HtmlTagName

/** Every string this page shows, in one language. */
private class OverviewTexts(
    val title: String,
    val summary: String,
    val docsLink: String,
    val sourceLink: String,
    val apiLink: String,
    val purposeHeading: String,
    val body: String,
    val bullets: List<String>,
    val pagesHeading: String,
    val pagesBody: String,
    val platformsHeading: String,
    val platformsBody: String,
)

private val overviewEnglish: OverviewTexts = OverviewTexts(
    title = "Overview",
    summary = "A Kotlin Multiplatform port of Hutool's utility classes: the same call names, the same " +
        "results, without a JVM underneath.",
    docsLink = "Documentation",
    sourceLink = "Source repository",
    apiLink = "API reference",
    purposeHeading = "What it is",
    body = "Hutool is a large collection of small utilities, and most of them are not JVM-specific: " +
        "formatting a string, rounding a number, validating an id card, hashing bytes. This port keeps " +
        "the classes and the call names — StrUtil, NumberUtil, IdcardUtil, SM3 — and rewrites the " +
        "implementations so they run on every Kotlin target.",
    bullets = listOf(
        "StrUtil, StrFormatter, ReUtil — strings and regular expressions.",
        "NumberUtil, MathUtil — decimals without binary floating-point drift.",
        "IdcardUtil, PhoneUtil, DesensitizedUtil — the mainland-specific formats.",
    ),
    pagesHeading = "What the pages show",
    pagesBody = "Each page runs one family of calls and prints what came back. The values are computed " +
        "once, when the page is built: they are deterministic, and a language switch rewrites the " +
        "headings only, so nothing is recomputed and nothing jumps.",
    platformsHeading = "Why a port and not the JVM library",
    platformsBody = "Hutool is published for the JVM, and a Kotlin/JS or Native build cannot use it. The " +
        "port is a separate implementation with the same surface, so a shared module can call the " +
        "utilities it already knows; the pages here are that implementation running in this browser.",
)

private val overviewChinese: OverviewTexts = OverviewTexts(
    title = "概览",
    summary = "Hutool 工具类的 Kotlin Multiplatform 移植：调用名一样、结果一样，底下没有 JVM。",
    docsLink = "使用文档",
    sourceLink = "源码仓库",
    apiLink = "API 文档",
    purposeHeading = "它是什么",
    body = "Hutool 是一大堆小工具，其中多数并不依赖 JVM：格式化字符串、取整、校验身份证号、对字节做摘要。" +
        "这个移植保留类名与调用名 —— StrUtil、NumberUtil、IdcardUtil、SM3 —— 重写实现，" +
        "让它们能在每个 Kotlin 目标上跑。",
    bullets = listOf(
        "StrUtil、StrFormatter、ReUtil —— 字符串与正则。",
        "NumberUtil、MathUtil —— 十进制计算，不受二进制浮点误差影响。",
        "IdcardUtil、PhoneUtil、DesensitizedUtil —— 中国大陆特有的格式。",
    ),
    pagesHeading = "这些页面在演示什么",
    pagesBody = "每页跑一族调用，把返回值印出来。值只在页面构造时算一次：它们都是确定的，" +
        "切换语言只改标题，所以不会重算、也不会跳动。",
    platformsHeading = "为什么是移植，而不是直接用 JVM 库",
    platformsBody = "Hutool 只发布 JVM 版本，Kotlin/JS 或 Native 构建用不了。移植是一份接口相同、" +
        "实现独立的版本，于是共用模块可以直接调它已经熟悉的那套工具；这里的页面就是这份实现跑在当前浏览器里。",
)

private val overviewTexts: Translations<OverviewTexts> = texts(
    Langs.EN to overviewEnglish,
    Langs.ZH_HANS to overviewChinese,
)

/** Landing page: what this port is, how the demo is organised, why it exists at all. */
internal class OverviewPage : Page {
    private val page = newPage()

    private val texts = Binder(overviewTexts)

    override val node get() = page

    init {
        page.addLink(texts.bind(anchor("", DOCS_URL, external = true), { it.docsLink }))
        page.addLink(texts.bind(anchor("", API_URL, external = true), { it.apiLink }))
        page.addLink(texts.bind(anchor("", SOURCE_URL, external = true), { it.sourceLink }))

        val purpose = sectionBlock(overviewEnglish.purposeHeading, null)
        texts.bind(purpose.title) { it.purposeHeading }
        // One bound <li> per bullet, so a language switch rewrites each line in place.
        val bullets = HtmlTagName.ul {
            List(3) { index -> texts.bind(+HtmlTagName.li()) { it.bullets.getOrNull(index).orEmpty() } }
        }
        purpose.body.append(texts.bind(paragraph(), { it.body }), bullets)

        val pages = sectionBlock(overviewEnglish.pagesHeading, null)
        texts.bind(pages.title) { it.pagesHeading }
        pages.body.append(texts.bind(paragraph(), { it.pagesBody }))

        val platforms = sectionBlock(overviewEnglish.platformsHeading, null)
        texts.bind(platforms.title) { it.platformsHeading }
        platforms.body.append(texts.bind(paragraph(), { it.platformsBody }))

        page.body.append(purpose.root, pages.root, platforms.root)
    }

    override fun applyTranslation(lang: Lang) {
        val current = overviewTexts[lang]
        page.setTitle(current.title)
        page.setSummary(current.summary)
        texts.apply(lang)
    }
}
