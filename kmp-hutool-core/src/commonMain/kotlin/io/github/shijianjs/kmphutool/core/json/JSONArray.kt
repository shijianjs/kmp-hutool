package io.github.shijianjs.kmphutool.core.json

/**
 * JSON array backed by a Kotlin list.
 *
 * Port of `cn.hutool.json.JSONArray`. `add`/`get` operate on ordinary Kotlin values and nested
 * [JSONObject]/[JSONArray] instances.
 *
 * @hutool cn.hutool.json.JSONArray
 */
class JSONArray() : JSON, Iterable<Any?> {
    private val list = ArrayList<Any?>()

    constructor(values: Iterable<*>) : this() {
        list.addAll(values)
    }

    constructor(json: String) : this(parseArray(json))

    operator fun get(index: Int): Any? = list[index]

    operator fun set(index: Int, value: Any?): Any? = list.set(index, value)

    fun add(value: Any?): JSONArray = apply { list.add(value) }

    fun addAll(values: Iterable<*>): JSONArray = apply { list.addAll(values) }

    fun remove(index: Int): Any? = list.removeAt(index)

    fun getStr(index: Int, defaultValue: String? = null): String? =
        list[index]?.toString() ?: defaultValue

    fun getInt(index: Int, defaultValue: Int? = null): Int? =
        (list[index] as? Number)?.toInt()
            ?: list[index]?.toString()?.toIntOrNull()
            ?: defaultValue

    fun getLong(index: Int, defaultValue: Long? = null): Long? =
        (list[index] as? Number)?.toLong()
            ?: list[index]?.toString()?.toLongOrNull()
            ?: defaultValue

    fun getDouble(index: Int, defaultValue: Double? = null): Double? =
        (list[index] as? Number)?.toDouble()
            ?: list[index]?.toString()?.toDoubleOrNull()
            ?: defaultValue

    fun getBool(index: Int, defaultValue: Boolean? = null): Boolean? =
        when (val value = list[index]) {
            null -> defaultValue
            is Boolean -> value
            else -> value.toString().toBooleanStrictOrNull() ?: defaultValue
        }

    fun getJSONObject(index: Int): JSONObject? = when (val value = list[index]) {
        is JSONObject -> value
        is Map<*, *> -> JSONObject(value.entries.associate { it.key.toString() to it.value })
        else -> null
    }

    fun getJSONArray(index: Int): JSONArray? = when (val value = list[index]) {
        is JSONArray -> value
        is Iterable<*> -> JSONArray(value)
        else -> null
    }

    override fun iterator(): Iterator<Any?> = list.iterator()

    override fun toJSONString(): String = JSONConvert.stringify(this)

    override fun toString(): String = toJSONString()

    override fun toStringPretty(): String = JSONConvert.stringify(this, pretty = true)

    override fun size(): Int = list.size

    override fun isEmpty(): Boolean = list.isEmpty()

    private companion object {
        fun parseArray(json: String): List<Any?> {
            val value = JSONConvert.parse(json) as? JSONArray
                ?: throw JSONException("A JSONArray text must start with '['", null as Throwable?)
            return value.toList()
        }
    }
}
