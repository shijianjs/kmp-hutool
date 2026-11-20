package io.github.shijianjs.kmphutool.demo.page.overview

import io.github.shijianjs.ktjs.dom.element.invoke
import io.github.shijianjs.ktjs.dom.element.unaryPlus

/** Every string this page shows, in one language. */
internal class OverviewTexts(
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
