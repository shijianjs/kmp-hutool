package io.github.shijianjs.kmphutool.demo.page

import io.github.shijianjs.ktjs.demo.i18n.Binder
import io.github.shijianjs.ktjs.demo.i18n.Lang
import io.github.shijianjs.ktjs.demo.i18n.Langs
import io.github.shijianjs.ktjs.demo.i18n.Translations
import io.github.shijianjs.ktjs.demo.nav.Page
import io.github.shijianjs.ktjs.demo.page.codeText
import io.github.shijianjs.ktjs.demo.page.exampleBox
import io.github.shijianjs.ktjs.demo.page.grid
import io.github.shijianjs.ktjs.demo.page.newPage
import io.github.shijianjs.ktjs.demo.page.paragraph
import io.github.shijianjs.ktjs.demo.page.sectionBlock

/**
 * One call and its result: the expression on the left, what kmp-hutool-core returned on the right.
 *
 * Neither side is translated — a call reads the same in every language, and a value derived from it
 * (a hash, a formatted number) is data, not copy.
 */
class ResultRow(val call: String, val value: String)

/** One family of calls, under the heading the section shows. */
class ResultSection(val title: String, val hint: String, val rows: List<ResultRow>)

/** Everything a results page shows, in one language. */
class ResultTexts(val title: String, val summary: String, val sections: List<ResultSection>)

/**
 * A page that is nothing but computed results.
 *
 * Eight pages of this site differ only in which family of calls they run, so they share this frame:
 * one section per family, one card per call. The values are computed once, when the page is built
 * (they are deterministic), and a language switch only rewrites the headings — the cards are left
 * alone, which is also why switching languages never re-runs a call.
 */
internal class ResultPage(private val translations: Translations<ResultTexts>) : Page {
    private val page = newPage()

    private val binder = Binder(translations)

    override val node get() = page

    init {
        val english = translations[Langs.EN]
        english.sections.forEachIndexed { index, section ->
            val block = sectionBlock(section.title, section.hint)
            binder.bind(block.title) { it.sections[index].title }
            binder.bind(block.hint) { it.sections[index].hint }
            // One card per call, laid out by the grid: a call and its value are two lines of the same
            // box, so the eye reads pairs instead of two columns that happen to line up.
            val cards = section.rows.map { entry ->
                exampleBox(null, codeText(entry.call), paragraph(entry.value))
            }
            block.body.append(grid(minColumnWidth = 240, *cards.toTypedArray()))
            page.body.append(block.root)
        }
    }

    override fun applyTranslation(lang: Lang) {
        val current = translations[lang]
        page.setTitle(current.title)
        page.setSummary(current.summary)
        binder.apply(lang)
    }
}
