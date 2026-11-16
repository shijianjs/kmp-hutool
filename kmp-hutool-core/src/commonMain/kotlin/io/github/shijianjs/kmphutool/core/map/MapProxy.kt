package io.github.shijianjs.kmphutool.core.map

/**
 * A thin delegating wrapper that adds an untyped `getObj(key, defaultValue)`.
 *
 * Port of `cn.hutool.core.map.MapProxy` without its dynamic-proxy half: `create`, `invoke` and
 * `toProxyBean` rely on `java.lang.reflect` and are not portable, so callers should use the typed
 * map directly.
 *
 * @hutool cn.hutool.core.map.MapProxy
 */
class MapProxy(private val target: MutableMap<Any?, Any?>) : MutableMap<Any?, Any?> by target {
    /** Returns the value for [key], or [defaultValue] when absent. */
    fun getObj(key: Any?, defaultValue: Any? = null): Any? = target[key] ?: defaultValue

    override fun toString(): String = target.toString()

    companion object {
        /** Wraps [map]. */
        fun create(map: MutableMap<Any?, Any?>): MapProxy = MapProxy(map)
    }
}
