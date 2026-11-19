package io.github.shijianjs.kmphutool.demo

import io.github.shijianjs.kmphutool.demo.page.china.ChinaPage
import io.github.shijianjs.kmphutool.demo.page.collection.CollectionPage
import io.github.shijianjs.kmphutool.demo.page.dates.DatesPage
import io.github.shijianjs.kmphutool.demo.page.encoding.EncodingPage
import io.github.shijianjs.kmphutool.demo.page.hashes.HashesPage
import io.github.shijianjs.kmphutool.demo.page.number.NumberPage
import io.github.shijianjs.kmphutool.demo.page.overview.OverviewPage
import io.github.shijianjs.kmphutool.demo.page.text.TextPage
import io.github.shijianjs.ktjs.demo.nav.DemoMenu
import io.github.shijianjs.ktjs.demo.nav.Entry
import io.github.shijianjs.ktjs.demo.nav.Group

/** The upstream Hutool release this port follows; the navbar shows it under the site name. */
const val SITE_VERSION: String = "5.8.48"

/** The documentation site. */
const val DOCS_URL: String = "https://shijianjs.github.io/kmp-hutool/"

/** Dokka reference assembled by pagesSite next to the demo. */
const val API_URL: String = "https://shijianjs.github.io/kmp-hutool/api/"

/** Source repository, linked from the navbar's link menu. */
const val SOURCE_URL: String = "https://github.com/shijianjs/kmp-hutool"

internal val START: Group = Group("start", "lucide:sparkles")

internal val TEXT: Group = Group("text", "lucide:type")

internal val DATA: Group = Group("data", "lucide:database")

internal val CHINA: Group = Group("china", "lucide:id-card")

internal val CRYPTO: Group = Group("crypto", "lucide:shield-check")

/**
 * The site: one entry per page, and one page per family of utilities.
 *
 * Adding a page means adding one entry here and its titles in [siteTexts].
 */
fun siteMenu(): DemoMenu = DemoMenu(
    listOf(
        Entry("overview", START) { OverviewPage() },
        Entry("text", TEXT) { TextPage() },
        Entry("number", TEXT) { NumberPage() },
        Entry("collection", TEXT) { CollectionPage() },
        Entry("encoding", DATA) { EncodingPage() },
        Entry("dates", DATA) { DatesPage() },
        Entry("china", CHINA) { ChinaPage() },
        Entry("hashes", CRYPTO) { HashesPage() },
    ),
)
