package io.github.shijianjs.kmphutool.demo.page.collection

import io.github.shijianjs.ktjs.dom.element.invoke
import io.github.shijianjs.ktjs.dom.webcomponent.CustomElementReferenceAdapter
import io.github.shijianjs.kmphutool.core.collection.CollUtil
import io.github.shijianjs.kmphutool.demo.page.ResultPage
import io.github.shijianjs.kmphutool.demo.page.ResultRow
import io.github.shijianjs.kmphutool.demo.page.ResultSection
import io.github.shijianjs.kmphutool.demo.page.ResultTexts
import io.github.shijianjs.kmphutool.demo.page.resultTexts

/** Every string this page shows, and the calls behind them. */
internal class CollectionPage : ResultPage() {
    init {
        render(collectionTexts)
    }

    companion object : CustomElementReferenceAdapter<CollectionPage>(CollectionPage::class.js, "hutool-collection-page") {
        /** The calls this page demonstrates on small fixed lists, so the values stay readable. */
        private val rows: List<ResultRow> = listOf(
            ResultRow("CollUtil.join([1, 2, 3], \"-\")", CollUtil.join(listOf(1, 2, 3), "-")),
            ResultRow("CollUtil.distinct([1, 1, 2, 3])", CollUtil.distinct(listOf(1, 1, 2, 3)).joinToString(", ")),
            ResultRow("CollUtil.union([1, 2], [2, 3])", CollUtil.union(listOf(1, 2), listOf(2, 3)).joinToString(", ")),
            ResultRow("CollUtil.isEmpty([])", CollUtil.isEmpty(emptyList<String>()).toString()),
            ResultRow(
                "[1, 2, 3, 4, 5].chunked(2)",
                listOf(1, 2, 3, 4, 5).chunked(2).joinToString(" | ") { it.joinToString(",", prefix = "[", postfix = "]") },
            ),
        )
        private val english: ResultTexts = ResultTexts(
            title = "Collections",
            summary = "Joining, de-duplicating and partitioning lists — the operations that live on CollUtil " +
                "upstream and on Kotlin's own collection API here.",
            sections = listOf(
                ResultSection(
                    title = "Joining and partitioning",
                    hint = "union keeps the element order of the first list and drops what the second one repeats.",
                    rows = rows,
                ),
            ),
        )
        private val chinese: ResultTexts = ResultTexts(
            title = "集合",
            summary = "拼接、去重、切分列表 —— 这些上游在 CollUtil 上，这里一部分直接落在 Kotlin 自己的集合 API 上。",
            sections = listOf(
                ResultSection(
                    title = "拼接与切分",
                    hint = "union 保留第一个列表的元素顺序，丢掉第二个列表里重复的部分。",
                    rows = rows,
                ),
            ),
        )
        private val collectionTexts = resultTexts(english, chinese)
    }
}
