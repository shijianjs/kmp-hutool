package io.github.shijianjs.kmphutool.core.lang

/**
 * Contract for enums that carry a stable integer value.
 *
 * Port of `cn.hutool.core.lang.EnumItem`. Kotlin-ified into properties: `name` is the enum's built-in
 * name and [intVal] must be supplied by the constant. Persist [intVal] rather than `ordinal` so
 * reordering constants does not change stored data.
 *
 * @hutool cn.hutool.core.lang.EnumItem
 */
interface EnumItem<E : EnumItem<E>> {
    /** Stable numeric value for persistence. */
    val intVal: Int

    /** All constants of the implementing enum. */
    fun items(): List<E>

    /** Human-readable label, defaulting to the enum name. */
    fun text(): String = (this as Enum<*>).name

    /** Looks up a constant by [intVal], or `null`. */
    fun fromInt(intVal: Int?): E? {
        if (intVal == null) return null
        return items().firstOrNull { it.intVal == intVal }
    }

    /** Looks up a constant by name (case-insensitive), or `null`. */
    fun fromStr(strVal: String?): E? {
        if (strVal == null) return null
        return items().firstOrNull { (it as Enum<*>).name.equals(strVal, ignoreCase = true) }
    }
}
