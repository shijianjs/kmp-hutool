package io.github.shijianjs.kmphutool.core.json

import io.github.shijianjs.kmphutool.core.util.StrUtil

/**
 * Exception raised while parsing or serialising JSON.
 *
 * Port of `cn.hutool.json.JSONException`.
 *
 * @hutool cn.hutool.json.JSONException
 */
open class JSONException : RuntimeException {
    constructor(message: String?) : super(message)
    constructor(message: String?, cause: Throwable?) : super(message, cause)
    constructor(cause: Throwable?) : super(cause?.message, cause)

    companion object {
        /** Same message formatting as Hutool's `JSONException(CharSequence, Object...)`. */
        fun format(messageTemplate: CharSequence, vararg params: Any?): JSONException =
            JSONException(StrUtil.format(messageTemplate, *params))
    }
}
