package io.github.shijianjs.kmphutool.core.exceptions

import io.github.shijianjs.kmphutool.core.util.StrUtil

/**
 * A runtime exception carrying an application status code.
 *
 * Port of `cn.hutool.core.exceptions.StatefulException`.
 *
 * @hutool cn.hutool.core.exceptions.StatefulException
 */
class StatefulException : RuntimeException {
    /** Application status code, `0` when unset. */
    var status: Int = 0
        private set

    constructor() : super()

    constructor(msg: String) : super(msg)

    constructor(messageTemplate: String, vararg params: Any?) : super(StrUtil.format(messageTemplate, *params))

    constructor(throwable: Throwable) : super(throwable)

    constructor(msg: String, throwable: Throwable?) : super(msg, throwable)

    constructor(status: Int, msg: String) : super(msg) {
        this.status = status
    }

    constructor(status: Int, throwable: Throwable) : super(throwable) {
        this.status = status
    }

    constructor(status: Int, msg: String, throwable: Throwable?) : super(msg, throwable) {
        this.status = status
    }
}
