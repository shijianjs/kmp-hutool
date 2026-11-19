package io.github.shijianjs.kmphutool.demo.page

import io.github.shijianjs.ktjs.demo.i18n.Langs
import io.github.shijianjs.ktjs.demo.i18n.Translations
import io.github.shijianjs.ktjs.demo.i18n.texts

/** Declares a page's two text classes, so each page file carries only its own copy and its own data. */
internal fun <T : Any> resultTexts(english: T, chinese: T): Translations<T> = texts(
    Langs.EN to english,
    Langs.ZH_HANS to chinese,
)
