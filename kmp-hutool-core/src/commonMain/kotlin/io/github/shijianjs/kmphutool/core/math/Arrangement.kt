package io.github.shijianjs.kmphutool.core.math

/**
 * Arrangements `A(n, m)` of a fixed data set.
 *
 * Port of `cn.hutool.core.math.Arrangement`. [select] and [selectAll] enumerate distinct
 * permutations (no element repeats); [iterate] walks `A(n, m)` lazily with an explicit-stack
 * variant of upstream's recursive search.
 *
 * @hutool cn.hutool.core.math.Arrangement
 */
class Arrangement(private val datas: Array<String>) {
    /** All arrangements of [m] elements. */
    fun select(m: Int): List<Array<String>> {
        if (m < 0 || m > datas.size) return emptyList()
        if (m == 0) return listOf(emptyArray())
        val capacity = count(datas.size, m).coerceAtMost(Int.MAX_VALUE.toLong()).toInt()
        val result = ArrayList<Array<String>>(capacity)
        dfs(arrayOfNulls(m), 0, BooleanArray(datas.size), result)
        return result
    }

    /** Every arrangement of sizes 1 through `datas.size`. */
    fun selectAll(): List<Array<String>> {
        val result = ArrayList<Array<String>>()
        for (m in 1..datas.size) result.addAll(select(m))
        return result
    }

    /** A lazy iterator over the arrangements of [m] elements. */
    fun iterate(m: Int): Iterable<Array<String>> = Iterable { ArrangementIterator(datas, m) }

    private fun dfs(current: Array<String?>, depth: Int, visited: BooleanArray, result: MutableList<Array<String>>) {
        if (depth == current.size) {
            @Suppress("UNCHECKED_CAST")
            result.add(current.copyOf() as Array<String>)
            return
        }
        for (i in datas.indices) {
            if (!visited[i]) {
                visited[i] = true
                current[depth] = datas[i]
                dfs(current, depth + 1, visited, result)
                visited[i] = false
            }
        }
    }

    /** Depth-first iterator with an explicit stack, mirroring upstream's lazy variant. */
    private class ArrangementIterator(datas: Array<String>, m: Int) : Iterator<Array<String>> {
        private val datas = datas
        private val n = datas.size
        private val m = m
        private val visited = BooleanArray(n)
        private val buffer = arrayOfNulls<String>(m.coerceAtLeast(0))
        private val indices = IntArray(m.coerceAtLeast(0)) { -1 }
        private var depth = if (m < 0 || m > n) -1 else 0
        private var end = m < 0 || m > n
        private var nextItem: Array<String>? = null
        private var nextPrepared = false

        override fun hasNext(): Boolean {
            if (end) return false
            if (nextPrepared) return nextItem != null
            prepareNext()
            return nextItem != null
        }

        override fun next(): Array<String> {
            if (!nextPrepared) prepareNext()
            val item = nextItem ?: throw NoSuchElementException()
            nextItem = null
            nextPrepared = false
            if (m == 0) end = true
            return item
        }

        private fun prepareNext() {
            if (nextPrepared) return
            if (m == 0) {
                nextItem = emptyArray()
                nextPrepared = true
                return
            }
            while (depth >= 0) {
                val start = indices[depth] + 1
                var found = false
                var i = start
                while (i < n) {
                    if (!visited[i]) {
                        if (indices[depth] != -1) visited[indices[depth]] = false
                        indices[depth] = i
                        visited[i] = true
                        buffer[depth] = datas[i]
                        found = true
                        break
                    }
                    i++
                }
                if (!found) {
                    if (indices[depth] != -1) {
                        visited[indices[depth]] = false
                        indices[depth] = -1
                    }
                    depth--
                    continue
                }
                if (depth == m - 1) {
                    @Suppress("UNCHECKED_CAST")
                    nextItem = buffer.copyOf() as Array<String>
                    visited[indices[depth]] = false
                    nextPrepared = true
                    return
                }
                depth++
                if (depth < m) indices[depth] = -1
            }
            end = true
            nextItem = null
            nextPrepared = true
        }
    }

    companion object {
        /** `A(n, n)` = `n!`. */
        fun count(n: Int): Long = count(n, n)

        /** `A(n, m)`; throws [ArithmeticException] on overflow. */
        fun count(n: Int, m: Int): Long {
            require(!(m < 0 || m > n)) { "n >= 0 && m >= 0 && m <= n required" }
            if (m == 0) return 1
            var result = 1L
            for (i in 0 until m) {
                val next = result * (n - i)
                if (next < result) throw ArithmeticException("Overflow computing A($n,$m)")
                result = next
            }
            return result
        }

        /** `A(n,1) + A(n,2) + ... + A(n,n)`. */
        fun countAll(n: Int): Long {
            var total = 0L
            for (i in 1..n) total += count(n, i)
            return total
        }
    }
}
