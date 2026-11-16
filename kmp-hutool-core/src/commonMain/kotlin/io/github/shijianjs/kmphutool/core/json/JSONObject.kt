package io.github.shijianjs.kmphutool.core.json

/**
 * Insertion-ordered JSON object with Hutool's convenience getters.
 *
 * Port of `cn.hutool.json.JSONObject`. Values are ordinary Kotlin values (`String`, `Boolean`,
 * numbers, [JSONObject], [JSONArray], `null`), so no reflection is involved.
 *
 * @hutool cn.hutool.json.JSONObject
 */
class JSONObject() : JSON {
    private val map = LinkedHashMap<String, Any?>()

    constructor(map: Map<String, Any?>) : this() {
        this.map.putAll(map)
    }

    constructor(json: String) : this(parseObject(json))

    val entries: Set<Map.Entry<String, Any?>>
        get() = map.entries

    fun keySet(): Set<String> = map.keys

    operator fun get(key: String): Any? = map[key]

    operator fun set(key: String, value: Any?) {
        map[key] = value
    }

    fun put(key: String, value: Any?): JSONObject = apply { map[key] = value }

    fun setValue(key: String, value: Any?): JSONObject = put(key, value)

    /** Adds [value], turning repeated keys into a [JSONArray]. */
    fun accumulate(key: String, value: Any?): JSONObject {
        when (val current = map[key]) {
            null -> map[key] = value
            is JSONArray -> current.add(value)
            else -> map[key] = JSONArray(listOf(current, value))
        }
        return this
    }

    fun remove(key: String): Any? = map.remove(key)

    fun containsKey(key: String): Boolean = map.containsKey(key)

    fun getStr(key: String, defaultValue: String? = null): String? =
        map[key]?.toString() ?: defaultValue

    fun getInt(key: String, defaultValue: Int? = null): Int? =
        when (val value = map[key]) {
            null -> defaultValue
            is Number -> value.toInt()
            else -> value.toString().toIntOrNull() ?: defaultValue
        }

    fun getLong(key: String, defaultValue: Long? = null): Long? =
        when (val value = map[key]) {
            null -> defaultValue
            is Number -> value.toLong()
            else -> value.toString().toLongOrNull() ?: defaultValue
        }

    fun getDouble(key: String, defaultValue: Double? = null): Double? =
        when (val value = map[key]) {
            null -> defaultValue
            is Number -> value.toDouble()
            else -> value.toString().toDoubleOrNull() ?: defaultValue
        }

    fun getBool(key: String, defaultValue: Boolean? = null): Boolean? =
        when (val value = map[key]) {
            null -> defaultValue
            is Boolean -> value
            else -> value.toString().toBooleanStrictOrNull() ?: defaultValue
        }

    fun getJSONObject(key: String): JSONObject? = when (val value = map[key]) {
        is JSONObject -> value
        is Map<*, *> -> JSONObject(value.entries.associate { it.key.toString() to it.value })
        else -> null
    }

    fun getJSONArray(key: String): JSONArray? = when (val value = map[key]) {
        is JSONArray -> value
        is Iterable<*> -> JSONArray(value)
        else -> null
    }

    override fun toJSONString(): String = JSONConvert.stringify(this)

    override fun toString(): String = toJSONString()

    override fun toStringPretty(): String = JSONConvert.stringify(this, pretty = true)

    override fun size(): Int = map.size

    override fun isEmpty(): Boolean = map.isEmpty()

    private companion object {
        fun parseObject(json: String): Map<String, Any?> {
            val value = JSONConvert.parse(json) as? JSONObject
                ?: throw JSONException("A JSONObject text must begin with '{'", null as Throwable?)
            return value.entries.associate { it.key to it.value }
        }
    }
}
