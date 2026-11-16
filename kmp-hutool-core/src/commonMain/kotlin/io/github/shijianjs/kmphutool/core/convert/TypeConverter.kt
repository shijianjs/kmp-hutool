package io.github.shijianjs.kmphutool.core.convert

import kotlin.reflect.KClass

/**
 * A custom conversion rule driven by a target type.
 *
 * Port of `cn.hutool.core.convert.TypeConverter`. `java.lang.reflect.Type` is replaced by
 * [KClass], which is the portable way to name a target type in common code.
 *
 * @hutool cn.hutool.core.convert.TypeConverter
 */
fun interface TypeConverter {
    /** Converts [value] to [targetType]. */
    fun convert(targetType: KClass<*>, value: Any?): Any?
}
