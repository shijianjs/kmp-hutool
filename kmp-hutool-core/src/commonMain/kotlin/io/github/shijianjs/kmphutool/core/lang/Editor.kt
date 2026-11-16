package io.github.shijianjs.kmphutool.core.lang

/**
 * A function that edits one element: returning `null` drops it, anything else replaces it.
 *
 * Port of `cn.hutool.core.lang.Editor`.
 *
 * @hutool cn.hutool.core.lang.Editor
 */
fun interface Editor<T> {
    /** Returns the edited value, or `null` to drop it. */
    fun edit(t: T): T?
}
