package io.github.shijianjs.kmphutool.core.lang.generator

import io.github.shijianjs.kmphutool.core.util.IdUtil

/**
 * Generates UUID strings through [IdUtil.fastUUID].
 *
 * Port of `cn.hutool.core.lang.generator.UUIDGenerator`.
 *
 * @hutool cn.hutool.core.lang.generator.UUIDGenerator
 */
class UUIDGenerator : Generator<String> {
    override fun next(): String = IdUtil.fastUUID()
}
