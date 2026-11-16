package io.github.shijianjs.kmphutool.core.lang

import io.github.shijianjs.kmphutool.core.convert.Convert

/**
 * Renders a simple ASCII table for console/log output.
 *
 * Port of `cn.hutool.core.lang.ConsoleTable`. `print()` is intentionally absent: a common source
 * set has no portable console, so call `toString()` and print it where the platform allows.
 *
 * @hutool cn.hutool.core.lang.ConsoleTable
 */
class ConsoleTable {
    private companion object {
        const val CORNER = '+'
        const val SPACE = '\u3000'
        const val LF = '\n'
        const val ROW_LINE = '－'
        const val COLUMN_LINE = '|'
    }

    private var isSBCMode = true
    private val headerList = mutableListOf<MutableList<String>>()
    private val bodyList = mutableListOf<MutableList<String>>()
    private var columnCharNumber: MutableList<Int>? = null

    /** Toggles full-width (SBC) mode, which keeps CJK and ASCII aligned. */
    fun setSBCMode(isSBCMode: Boolean): ConsoleTable {
        this.isSBCMode = isSBCMode
        return this
    }

    fun addHeader(vararg titles: String): ConsoleTable {
        if (columnCharNumber == null) {
            columnCharNumber = MutableList(titles.size) { 0 }
        }
        val list = mutableListOf<String>()
        fillColumns(list, titles)
        headerList.add(list)
        return this
    }

    fun addBody(vararg values: String): ConsoleTable {
        val list = mutableListOf<String>()
        bodyList.add(list)
        fillColumns(list, values)
        return this
    }

    private fun fillColumns(target: MutableList<String>, columns: Array<out String>) {
        val widths = requireNotNull(columnCharNumber) { "addHeader must be called before addBody" }
        for (i in columns.indices) {
            var column = columns[i]
            if (isSBCMode) column = Convert.toSBC(column) ?: column
            target.add(column)
            val width = column.length
            if (width > widths[i]) widths[i] = width
        }
    }

    override fun toString(): String {
        val sb = StringBuilder()
        fillBorder(sb)
        fillRows(sb, headerList)
        fillBorder(sb)
        fillRows(sb, bodyList)
        fillBorder(sb)
        return sb.toString()
    }

    private fun fillRows(sb: StringBuilder, rows: List<List<String>>) {
        for (row in rows) {
            sb.append(COLUMN_LINE)
            fillRow(sb, row)
            sb.append(LF)
        }
    }

    private fun fillRow(sb: StringBuilder, row: List<String>) {
        val widths = requireNotNull(columnCharNumber)
        for (i in row.indices) {
            val value = row[i]
            sb.append(SPACE)
            sb.append(value)
            if (sbcCount(value) % 2 == 1) sb.append(' ')
            sb.append(SPACE)
            val maxLength = widths[i]
            repeat(maxLength - value.length + sbcCount(value) / 2) { sb.append(SPACE) }
            sb.append(COLUMN_LINE)
        }
    }

    private fun fillBorder(sb: StringBuilder) {
        sb.append(CORNER)
        for (width in requireNotNull(columnCharNumber)) {
            repeat(width + 2) { sb.append(ROW_LINE) }
            sb.append(CORNER)
        }
        sb.append(LF)
    }

    private fun sbcCount(value: String): Int = value.count { it.code < 127 }
}
