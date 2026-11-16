package io.github.shijianjs.kmphutool.core.net

import io.github.shijianjs.kmphutool.core.codec.PercentCodec

/**
 * `application/x-www-form-urlencoded` encoder: spaces become `+`.
 *
 * Port of `cn.hutool.core.net.FormUrlencoded`.
 *
 * @hutool cn.hutool.core.net.FormUrlencoded
 */
object FormUrlencoded {
    /** Values are encoded except for `-`, `_`, `.`, `*`; spaces become `+`. */
    val ALL: PercentCodec = PercentCodec.of(RFC3986.UNRESERVED)
        .removeSafe('~')
        .addSafe('*')
        .setEncodeSpaceAsPlus(true)
}
