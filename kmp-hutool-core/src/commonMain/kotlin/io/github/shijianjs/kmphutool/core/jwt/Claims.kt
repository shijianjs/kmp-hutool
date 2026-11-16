package io.github.shijianjs.kmphutool.core.jwt

import io.github.shijianjs.kmphutool.core.codec.Base64
import io.github.shijianjs.kmphutool.core.json.JSONObject

/**
 * Base class for JWT header and payload claims.
 *
 * Port of `cn.hutool.jwt.Claims`. Claims are a [JSONObject] of plain Kotlin values; the JVM
 * `Serializable` marker is not needed here.
 *
 * @hutool cn.hutool.jwt.Claims
 */
open class Claims {
    private var claimJSON: JSONObject? = null

    protected fun setClaim(name: String, value: Any?) {
        val json = init()
        if (value == null) json.remove(name) else json.put(name, value)
    }

    protected fun putAll(values: Map<String, *>) {
        values.forEach { (key, value) -> setClaim(key, value) }
    }

    fun getClaim(name: String): Any? = init()[name]

    fun getClaimsJson(): JSONObject = init()

    fun parse(tokenPart: String) {
        claimJSON = JSONObject(Base64.decodeStr(tokenPart))
    }

    override fun toString(): String = init().toJSONString()

    private fun init(): JSONObject = claimJSON ?: JSONObject().also { claimJSON = it }
}
