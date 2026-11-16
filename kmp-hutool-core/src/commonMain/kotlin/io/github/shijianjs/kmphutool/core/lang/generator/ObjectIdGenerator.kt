package io.github.shijianjs.kmphutool.core.lang.generator

import io.github.shijianjs.kmphutool.core.util.IdUtil

/**
 * Generates Mongo-style 24-character object ids.
 *
 * Port of `cn.hutool.core.lang.generator.ObjectIdGenerator`; the id construction itself lives in
 * [IdUtil.objectId].
 *
 * @hutool cn.hutool.core.lang.generator.ObjectIdGenerator
 */
class ObjectIdGenerator : Generator<String> {
    override fun next(): String = IdUtil.objectId()
}
