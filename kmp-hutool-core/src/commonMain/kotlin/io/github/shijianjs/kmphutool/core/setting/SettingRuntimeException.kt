package io.github.shijianjs.kmphutool.core.setting

import io.github.shijianjs.kmphutool.core.util.StrUtil

/**
 * Exception thrown while reading a setting file.
 *
 * Port of `cn.hutool.setting.SettingRuntimeException`.
 *
 * @hutool cn.hutool.setting.SettingRuntimeException
 */
class SettingRuntimeException : RuntimeException {
    constructor(cause: Throwable) : super(cause)

    constructor(message: String) : super(message)

    constructor(messageTemplate: String, vararg params: Any?) :
        super(StrUtil.format(messageTemplate, *params))

    constructor(message: String, cause: Throwable) : super(message, cause)

    constructor(cause: Throwable, messageTemplate: String, vararg params: Any?) :
        super(StrUtil.format(messageTemplate, *params), cause)
}
