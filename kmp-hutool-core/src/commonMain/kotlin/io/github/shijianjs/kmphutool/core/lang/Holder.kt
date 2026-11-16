package io.github.shijianjs.kmphutool.core.lang

/**
 * A mutable box holding one value, used where a lambda must write back a local variable.
 *
 * A holder type Hutool code samples use; Hutool 5.8.48 does not ship it as a class, so this is an
 * original addition. In Kotlin a lambda can usually capture a `var` directly, so it stays for parity
 * with Hutool-style code and for Java interop.
 */
class Holder<T> {
    /** The held value; starts as `null` when not supplied. */
    var value: T? = null

    /** A holder holding [value]. */
    constructor(value: T) {
        this.value = value
    }

    /** An empty holder. */
    constructor()

    override fun toString(): String = "Holder[value=$value]"
}
