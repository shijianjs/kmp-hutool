package io.github.shijianjs.kmphutool.core.convert

/**
 * The primitive type names Hutool uses as conversion targets.
 *
 * Port of `cn.hutool.core.convert.BasicType`. Upstream's enum implements `Converter` because it
 * carries a `Class` per constant; Kotlin has no `Class` in common code, so the port keeps the
 * constants and their names (which is what configuration files and `Dict` lookups use) and points
 * conversions at the explicit `Convert.toXxx` functions instead.
 *
 * @hutool cn.hutool.core.convert.BasicType
 */
enum class BasicType {
    /** Any object. */
    OBJECT,

    /** [`kotlin.String`]. */
    STRING,

    /** [`kotlin.Boolean`]. */
    BOOLEAN,

    /** [`kotlin.Char`]. */
    CHAR,

    /** [`kotlin.Byte`]. */
    BYTE,

    /** [`kotlin.Short`]. */
    SHORT,

    /** [`kotlin.Int`]. */
    INT,

    /** [`kotlin.Long`]. */
    LONG,

    /** [`kotlin.Float`]. */
    FLOAT,

    /** [`kotlin.Double`]. */
    DOUBLE,
    ;

    /** The lower-case form used by Hutool configuration (`"int"`, `"double"`, ...). */
    val code: String
        get() = name.lowercase()
}
