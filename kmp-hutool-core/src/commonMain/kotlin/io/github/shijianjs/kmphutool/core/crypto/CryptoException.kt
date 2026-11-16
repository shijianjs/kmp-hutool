package io.github.shijianjs.kmphutool.core.crypto

import io.github.shijianjs.kmphutool.core.util.StrUtil

/**
 * Exception raised by the crypto helpers.
 *
 * Port of `cn.hutool.crypto.CryptoException`.
 *
 * @hutool cn.hutool.crypto.CryptoException
 */
open class CryptoException : RuntimeException {
    constructor(message: String?) : super(message)
    constructor(message: String?, cause: Throwable?) : super(message, cause)
    constructor(cause: Throwable?) : super(cause?.message, cause)
    constructor(messageTemplate: CharSequence, vararg params: Any?) : super(StrUtil.format(messageTemplate, *params))
}
