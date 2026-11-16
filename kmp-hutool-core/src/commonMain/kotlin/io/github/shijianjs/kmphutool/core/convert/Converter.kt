package io.github.shijianjs.kmphutool.core.convert

/**
 * A converter from an arbitrary value to [T].
 *
 * Port of `cn.hutool.core.convert.Converter`. `convertWithCheck` keeps upstream's "quietly
 * return the default on failure" behaviour.
 *
 * @hutool cn.hutool.core.convert.Converter
 */
fun interface Converter<T> {
    fun convert(value: Any?, defaultValue: T): T

    fun convertWithCheck(value: Any?, defaultValue: T, quietly: Boolean): T =
        try {
            convert(value, defaultValue)
        } catch (e: Exception) {
            if (quietly) defaultValue else throw e
        }
}
