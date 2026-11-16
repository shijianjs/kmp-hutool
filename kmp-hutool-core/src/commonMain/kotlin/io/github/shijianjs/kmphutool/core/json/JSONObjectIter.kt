package io.github.shijianjs.kmphutool.core.json

/**
 * Adapts an iterator of values to typed [JSONObject] iteration, so a `JSONArray` can be traversed
 * with `for (obj in iterable)`.
 *
 * Port of `cn.hutool.json.JSONObjectIter`.
 *
 * @hutool cn.hutool.json.JSONObjectIter
 */
class JSONObjectIter(private val iterator: Iterator<Any?>) : Iterable<JSONObject> {
    override fun iterator(): Iterator<JSONObject> = object : Iterator<JSONObject> {
        override fun hasNext(): Boolean = iterator.hasNext()

        override fun next(): JSONObject =
            iterator.next() as? JSONObject ?: throw JSONException("Element is not a JSONObject")
    }
}
