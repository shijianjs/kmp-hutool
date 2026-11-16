package io.github.shijianjs.kmphutool.core.text.finder

/**
 * Finds a substring by reporting its start and its exclusive end.
 *
 * Port of `cn.hutool.core.text.finder.Finder`. Call [start] first, then [end] with that index.
 *
 * @hutool cn.hutool.core.text.finder.Finder
 */
interface Finder {
    /** Finds a match starting at or after [from]; returns the start index or [INDEX_NOT_FOUND]. */
    fun start(from: Int): Int

    /** The exclusive end of the match that starts at [start]; [INDEX_NOT_FOUND] when unknown. */
    fun end(start: Int): Int

    /** Makes the finder reusable. */
    fun reset(): Finder = this

    companion object {
        const val INDEX_NOT_FOUND: Int = -1
    }
}
