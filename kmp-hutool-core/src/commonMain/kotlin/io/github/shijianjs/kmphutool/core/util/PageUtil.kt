package io.github.shijianjs.kmphutool.core.util

/**
 * Page arithmetic shared by every "take page N" API.
 *
 * Port of `cn.hutool.core.util.PageUtil`. Hutool work with `int`; the port does the same and
 * returns `0` for nonsensical input instead of throwing, matching upstream's tolerance.
 *
 * @hutool cn.hutool.core.util.PageUtil
 */
object PageUtil {
    /** The number of pages needed to hold [totalCount] items at [pageSize] per page. */
    fun totalPage(totalCount: Int, pageSize: Int): Int {
        if (pageSize <= 0) return 0
        if (totalCount <= 0) return 0
        return (totalCount + pageSize - 1) / pageSize
    }

    /** The 1-based page number an [offset] falls on. */
    fun pageNo(offset: Int, pageSize: Int): Int {
        if (pageSize <= 0) return 0
        return offset / pageSize + 1
    }

    /** The 0-based offset of the first item of page [pageNo]. */
    fun offset(pageNo: Int, pageSize: Int): Int {
        if (pageSize <= 0 || pageNo <= 0) return 0
        return (pageNo - 1) * pageSize
    }

    /** The page before [pageNo], or `1` when already on the first page. */
    fun firstPageNo(pageNo: Int, pageSize: Int): Int = if (pageNo <= 1) 1 else pageNo - 1

    /** The last page for [totalCount] items at [pageSize] per page. */
    fun lastPageNo(pageNo: Int, pageSize: Int, totalCount: Int): Int {
        val total = totalPage(totalCount, pageSize)
        return if (pageNo >= total) total else pageNo + 1
    }

    /** `true` when [pageNo] is inside `1..totalPage`. */
    fun isValidPageNo(pageNo: Int, pageSize: Int, totalCount: Int): Boolean =
        pageNo in 1..totalPage(totalCount, pageSize)

    /**
     * Clamps [pageNo] into `1..totalPage`.
     *
     * @param defaultPageNo the page returned when there are no pages at all
     */
    fun fixPageNo(pageNo: Int, pageSize: Int, totalCount: Int, defaultPageNo: Int = 1): Int {
        val total = totalPage(totalCount, pageSize)
        if (total <= 0) return defaultPageNo
        return pageNo.coerceIn(1, total)
    }
}
