package io.github.shijianjs.kmphutool.demo.page

import io.github.shijianjs.ktjs.demo.i18n.Binder
import io.github.shijianjs.ktjs.demo.i18n.Lang
import io.github.shijianjs.ktjs.demo.i18n.Langs
import io.github.shijianjs.ktjs.demo.i18n.Translations
import io.github.shijianjs.ktjs.demo.page.DemoPage
import io.github.shijianjs.ktjs.demo.page.codeText
import io.github.shijianjs.ktjs.demo.page.exampleBox
import io.github.shijianjs.ktjs.demo.page.grid
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
 * Base class of the pages that are nothing but computed results.
 *
 * Eight pages of this site differ only in which family of calls they run, so they share this
 * renderer: one section per family, one card per call. A subclass is a page element of its own
 * (`<hutool-china-page>` and so on) and hands its table in from `init` through [render] — the class
 * that owns the route is the class that owns the DOM, the shared part is only how the results are
 * laid out.
 *
 * The values are computed once, when the page is built (they are deterministic), and a language
 * switch only rewrites the headings — the cards are left alone, which is also why switching
 * languages never re-runs a call.
 */
internal open class ResultPage : DemoPage() {
    private var translations: Translations<ResultTexts>? = null

    private lateinit var binder: Binder<ResultTexts>

    /** Fills this page from [translations]: title, summary and one section per family of calls. */
    protected fun render(translations: Translations<ResultTexts>) {
        this.translations = translations
        binder = Binder(translations)
        body.replaceChildren()

        val english = translations[Langs.EN]
        english.sections.forEachIndexed { index, section ->
            val block = sectionBlock(section.title, section.hint)
            binder.bind(block.heading) { it.sections[index].title }
            binder.bind(block.hint) { it.sections[index].hint }
            // One card per call, laid out by the grid: a call and its value are two lines of the same
            // box, so the eye reads pairs instead of two columns that happen to line up.
            val cards = section.rows.map { entry ->
                exampleBox(null, codeText(entry.call), paragraph(entry.value))
            }
            block.body.append(grid(minColumnWidth = 240, *cards.toTypedArray()))
            body.append(block.root)
        }

        binder.apply(Langs.EN)
    }

    override fun applyTranslation(lang: Lang) {
        val translations = translations ?: return
        val current = translations[lang]
        setTitle(current.title)
        setSummary(current.summary)
        binder.apply(lang)
    }
}
