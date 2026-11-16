package io.github.shijianjs.kmphutool.core.lang

import io.github.shijianjs.kmphutool.core.convert.Convert
import io.github.shijianjs.kmphutool.core.util.StrUtil

/**
 * A string-keyed map with typed getters, the "small dynamic object" Hutool uses for JSON-ish data.
 *
 * Port of `cn.hutool.core.lang.Dict`. It is a [MutableMap] delegate, so every map operation works
 * directly. Members that convert a map into a JavaBean (`toBean`, `parse`, `parseBean`,
 * `setFields`) are not ported: commonMain has no bean reflection, and Kotlin data classes are
 * constructed explicitly instead.
 *
 * @hutool cn.hutool.core.lang.Dict
 */
class Dict(
    private val backing: MutableMap<String, Any?> = LinkedHashMap(),
) : MutableMap<String, Any?> by backing {
    /** Stores [value] under [attr] and returns this for chaining. */
    fun set(attr: String, value: Any?): Dict = apply { backing[attr] = value }

    /** Stores [value] under [attr] unless it is `null`. */
    fun setIgnoreNull(attr: String, value: Any?): Dict = apply {
        if (value != null) backing[attr] = value
    }

    /** The raw value for [attr]. */
    fun getObj(attr: String): Any? = backing[attr]

    /** The value for [attr] as a string, or `null`. */
    fun getStr(attr: String): String? = Convert.toStr(backing[attr])

    /** The value for [attr] as an int, or `null`. */
    fun getInt(attr: String): Int? = Convert.toInt(backing[attr])

    /** The value for [attr] as a long, or `null`. */
    fun getLong(attr: String): Long? = Convert.toLong(backing[attr])

    /** The value for [attr] as a double, or `null`. */
    fun getDouble(attr: String): Double? = Convert.toDouble(backing[attr])

    /** The value for [attr] as a float, or `null`. */
    fun getFloat(attr: String): Float? = Convert.toFloat(backing[attr])

    /** The value for [attr] as a short, or `null`. */
    fun getShort(attr: String): Short? = Convert.toShort(backing[attr])

    /** The value for [attr] as a byte, or `null`. */
    fun getByte(attr: String): Byte? = Convert.toByte(backing[attr])

    /** The value for [attr] as a char, or `null`. */
    fun getChar(attr: String): Char? = Convert.toChar(backing[attr])

    /** The value for [attr] as a boolean, or `null`. */
    fun getBool(attr: String): Boolean? = Convert.toBool(backing[attr])

    /** The value for [attr] as a list; a string is split on `,`. */
    fun getList(attr: String): List<Any?> = Convert.toList(backing[attr])

    /** The value for [attr] as a nested [Dict] when possible, otherwise `null`. */
    fun getDict(attr: String): Dict? {
        val value = backing[attr] ?: return null
        if (value is Dict) return value
        if (value is Map<*, *>) {
            val result = Dict()
            value.forEach { (key, element) -> result[key?.toString() ?: ""] = element }
            return result
        }
        return null
    }

    /** The value for [attr] as an enum constant, or `null`. */
    inline fun <reified E : Enum<E>> getEnum(attr: String): E? = Convert.toEnum<E>(getObj(attr))

    /**
     * Reads a nested value by a dotted path: `getByPath("a.b.c")` walks three levels and accepts
     * list indexes such as `a.0.b`.
     */
    fun getByPath(expression: String?): Any? {
        if (StrUtil.isBlank(expression)) return null
        val parts = StrUtil.split(expression!!, '.', isTrim = true)
        var current: Any? = this
        for (part in parts) {
            current = when (val node = current) {
                is Dict -> node[part]
                is Map<*, *> -> node[part]
                is List<*> -> part.toIntOrNull()?.let { node.getOrNull(it) }
                else -> return null
            }
        }
        return current
    }

    /** A copy of this dict containing only [keys]. */
    fun filter(vararg keys: String): Dict {
        val result = Dict()
        for (key in keys) if (backing.containsKey(key)) result[key] = backing[key]
        return result
    }

    /**
     * Removes the entries whose value equals [other]'s value, except for the fields in
     * [withoutNames].
     */
    fun removeEqual(other: Dict?, vararg withoutNames: String): Dict {
        if (other == null) return this
        val protected = withoutNames.toSet()
        val keys = backing.keys.toList()
        for (key in keys) {
            if (protected.contains(key)) continue
            if (backing[key] == other[key]) backing.remove(key)
        }
        return this
    }

    /** A snapshot of this dict as a plain map. */
    fun toMap(): MutableMap<String, Any?> = LinkedHashMap(backing)

    override fun toString(): String = backing.toString()

    companion object {
        /** An empty dict. */
        fun create(): Dict = Dict()

        /** A dict from alternating keys and values: `of("a", 1, "b", 2)`. */
        fun of(vararg keysAndValues: Any?): Dict {
            val dict = Dict()
            var index = 0
            while (index + 1 < keysAndValues.size) {
                dict[keysAndValues[index].toString()] = keysAndValues[index + 1]
                index += 2
            }
            return dict
        }

        /** A dict from the given pairs. */
        fun ofPairs(vararg pairs: Pair<String, Any?>): Dict {
            val dict = Dict()
            for ((key, value) in pairs) dict[key] = value
            return dict
        }
    }
}
