package io.github.shijianjs.kmphutool.core.lang

import io.github.shijianjs.kmphutool.core.exceptions.ValidateException
import io.github.shijianjs.kmphutool.core.util.ObjectUtil
import io.github.shijianjs.kmphutool.core.util.StrUtil

/**
 * Assertions that throw [ValidateException] with a `{}`-templated message, for argument checks at
 * the top of a public function.
 *
 * Port of `cn.hutool.core.lang.Assert`. Hutool throws `IllegalArgumentException`; the port throws
 * [ValidateException], which *is* an `IllegalArgumentException`, so existing `catch` blocks keep
 * working. The overloads that create a message from a `Supplier` or a `Class` are not ported —
 * Kotlin's lambdas and `require(...)` cover them.
 *
 * @hutool cn.hutool.core.lang.Assert
 */
object Assert {
    /** Throws unless [value] is `true`; returns `true` so it can be used inline. */
    fun assertTrue(value: Boolean, errorMsgTemplate: String = "The value must be true", vararg params: Any?): Boolean {
        if (!value) throw exception(errorMsgTemplate, *params)
        return true
    }

    /** Throws unless [value] is `false`. */
    fun assertFalse(value: Boolean, errorMsgTemplate: String = "The value must be false", vararg params: Any?) {
        if (value) throw exception(errorMsgTemplate, *params)
    }

    /** Throws unless [value] is `null`. */
    fun <T> isNull(value: T?, errorMsgTemplate: String = "The value must be null", vararg params: Any?) {
        if (value != null) throw exception(errorMsgTemplate, *params)
    }

    /** Throws when [value] is `null`, and returns the non-null value otherwise. */
    fun <T : Any> notNull(value: T?, errorMsgTemplate: String = "The value must not be null", vararg params: Any?): T {
        if (value == null) throw exception(errorMsgTemplate, *params)
        return value
    }

    /** Throws when [value] is empty (`null`, blank text, empty collection or array). */
    fun <T> notEmpty(value: T?, errorMsgTemplate: String = "The value must not be empty", vararg params: Any?) {
        if (ObjectUtil.isEmpty(value)) throw exception(errorMsgTemplate, *params)
    }

    /** Throws when [value] is blank text. */
    fun notBlank(value: CharSequence?, errorMsgTemplate: String = "The value must not be blank", vararg params: Any?) {
        if (StrUtil.isBlank(value)) throw exception(errorMsgTemplate, *params)
    }

    /** Throws when the two values differ. */
    fun equals(value1: Any?, value2: Any?, errorMsgTemplate: String = "The value must be equal to {}", vararg params: Any?) {
        if (value1 != value2) throw exception(errorMsgTemplate, *params)
    }

    /** Throws when the two values are equal. */
    fun notEqual(value1: Any?, value2: Any?, errorMsgTemplate: String = "The value must not be equal to {}", vararg params: Any?) {
        if (value1 == value2) throw exception(errorMsgTemplate, *params)
    }

    /** Throws when [array] is `null` or has a `null` element. */
    fun noNullElements(array: Array<*>?, errorMsgTemplate: String = "The array must not contain null elements", vararg params: Any?) {
        if (array == null) throw exception(errorMsgTemplate, *params)
        if (array.any { it == null }) throw exception(errorMsgTemplate, *params)
    }

    /** Throws when [value] is not an instance of a type accepted by [matcher]. */
    fun <T> isInstanceOfType(value: T?, matcher: (T) -> Boolean, errorMsgTemplate: String = "The value {} is not of the expected type", vararg params: Any?): T {
        if (value == null || !matcher(value)) throw exception(errorMsgTemplate, *params)
        return value
    }

    /** Throws when [index] is outside `0..size`, the check every `sub`/`insert` needs. */
    fun checkIndex(index: Int, size: Int) {
        if (index < 0 || index > size) throw exception("Index must be in [0, {}], but was {}", size, index)
    }

    private fun exception(template: String, vararg params: Any?): ValidateException =
        ValidateException(StrUtil.format(template, *params))
}
