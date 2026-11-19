package io.github.shijianjs.kmphutool.demo

import io.github.shijianjs.ktjs.demo.i18n.Langs
import io.github.shijianjs.ktjs.demo.i18n.SiteTexts
import io.github.shijianjs.ktjs.demo.i18n.Translations
import io.github.shijianjs.ktjs.demo.i18n.texts

/**
 * Every string the layout and the navigation show, in English.
 *
 * One object per language, declared once in [siteTexts].
 */
private val englishSite: SiteTexts = SiteTexts(
    siteTitle = "kmp-hutool",
    langLabel = "English",
    languageLabel = "Language",
    themeLabel = "Theme",
    docsLabel = "Docs",
    siteDocsLabel = "Documentation",
    apiLabel = "API reference",
    sourceLabel = "GitHub",
    lightLabel = "Light",
    darkLabel = "Dark",
    systemLabel = "Follow browser",
    groupTitles = mapOf(
        "start" to "Getting started",
        "text" to "Text, numbers, collections",
        "data" to "Encodings and dates",
        "china" to "China helpers",
        "crypto" to "Hashes and signing",
    ),
    pageTitles = mapOf(
        "overview" to "Overview",
        "text" to "Strings",
        "number" to "Numbers",
        "collection" to "Collections",
        "encoding" to "Codes and identifiers",
        "dates" to "Dates and money",
        "china" to "China-specific helpers",
        "hashes" to "Hashes and signing",
    ),
)

private val chineseSite: SiteTexts = SiteTexts(
    siteTitle = "kmp-hutool",
    langLabel = "中文",
    languageLabel = "语言",
    themeLabel = "外观",
    docsLabel = "文档",
    siteDocsLabel = "使用文档",
    apiLabel = "API 文档",
    sourceLabel = "GitHub 仓库",
    lightLabel = "亮色",
    darkLabel = "暗色",
    systemLabel = "跟随浏览器",
    groupTitles = mapOf(
        "start" to "开始",
        "text" to "文本、数字、集合",
        "data" to "编码与日期",
        "china" to "中文工具",
        "crypto" to "摘要与签名",
    ),
    pageTitles = mapOf(
        "overview" to "概览",
        "text" to "字符串",
        "number" to "数字",
        "collection" to "集合",
        "encoding" to "编码与标识",
        "dates" to "日期与金额",
        "china" to "中文相关工具",
        "hashes" to "摘要与签名",
    ),
)

/** The layout's strings, one entry per registered language. */
val siteTexts: Translations<SiteTexts> = texts(
    Langs.EN to englishSite,
    Langs.ZH_HANS to chineseSite,
)
