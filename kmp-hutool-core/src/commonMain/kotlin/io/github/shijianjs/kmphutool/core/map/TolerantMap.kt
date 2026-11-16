package io.github.shijianjs.kmphutool.core.map

/**
 * A map that answers a configurable value for keys it does not contain.
 *
 * Port of `cn.hutool.core.map.TolerantMap`. `get` never returns `null` for a missing key, it returns
 * the default value, which is how upstream treats "tolerant" lookups.
 *
 * @hutool cn.hutool.core.map.TolerantMap
 */
class TolerantMap<K, V>(
    rawMap: MutableMap<K, V> = linkedMapOf(),
    private val defaultValue: V,
) : MapWrapper<K, V>(rawMap) {

    override fun get(key: K): V = rawMap[key] ?: defaultValue

    /** Gets the stored value without falling back to the default. */
    fun getRawValue(key: K): V? = rawMap[key]

    override fun equals(other: Any?): Boolean =
        this === other ||
            (other is TolerantMap<*, *> && rawMap == other.rawMap && defaultValue == other.defaultValue)

    override fun hashCode(): Int = 31 * rawMap.hashCode() + (defaultValue?.hashCode() ?: 0)

    override fun toString(): String = "TolerantMap{map=$rawMap, defaultValue=$defaultValue}"

    companion object {
        /** Wraps [map] with [defaultValue]; the map is used as-is, not copied. */
        fun <K, V> of(map: MutableMap<K, V>, defaultValue: V): TolerantMap<K, V> =
            TolerantMap(map, defaultValue)
    }
}
