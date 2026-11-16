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
    constructor(messageTemplate: CharSequence, vararg params: Any?) : super(StrUtil.format(messageTemplate, *params))
}
