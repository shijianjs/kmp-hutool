package io.github.shijianjs.kmphutool.core.json

/**
 * Explicit JSON `null`.
 *
 * Port of `cn.hutool.json.JSONNull`. [NULL] is the singleton; a Kotlin `null` value is written as
 * the same JSON `null`.
 *
 * @hutool cn.hutool.json.JSONNull
 */
object JSONNull {
    val NULL: JSONNull get() = this

    override fun toString(): String = "null"
}
