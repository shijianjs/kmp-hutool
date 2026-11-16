package io.github.shijianjs.kmphutool.core.lang

/**
 * A small container that makes "value or empty" explicit without `null` propagation.
 *
 * Port of `cn.hutool.core.lang.Opt`, rewritten for Kotlin: Java's `Optional`/`Stream` bridges,
 * `toOptional` and the `Func0`/`VoidFunc0` wrappers are replaced by ordinary lambdas. Kotlin code
 * will usually prefer `?.`/`?:`; this type remains for APIs that need an explicit empty state.
 *
 * @hutool cn.hutool.core.lang.Opt
 */
class Opt<T> private constructor(
    private val value: T?,
    private val exception: Throwable? = null,
) {
    /** Whether a value is present. */
    fun isPresent(): Boolean = value != null

    /** Whether this is empty. */
    fun isEmpty(): Boolean = value == null

    /** Whether [ofTry] swallowed an exception. */
    fun isFail(): Boolean = exception != null

    /** The captured exception of [ofTry], if any. */
    fun getException(): Throwable? = exception

    /** The value; throws [NoSuchElementException] when empty. */
    fun get(): T = value ?: throw NoSuchElementException("No value present")

    /** Runs [action] when a value is present. */
    fun ifPresent(action: (T) -> Unit): Opt<T> {
        value?.let(action)
        return this
    }

    /** Runs one of the two actions. */
    fun ifPresentOrElse(action: (T) -> Unit, emptyAction: () -> Unit): Opt<T> {
        if (value != null) action(value) else emptyAction()
        return this
    }

    /** Maps the value, or runs [emptyAction] and returns empty. */
    fun <U> mapOrElse(mapper: (T) -> U, emptyAction: () -> Unit): Opt<U> =
        if (value != null) Opt.ofNullable(mapper(value)) else { emptyAction(); Opt.empty() }

    /** Keeps the value only when [predicate] passes. */
    fun filter(predicate: (T) -> Boolean): Opt<T> =
        if (value != null && predicate(value)) this else empty()

    /** Maps the value; a `null` result becomes empty. */
    fun <U> map(mapper: (T) -> U?): Opt<U> = if (value != null) ofNullable(mapper(value)) else empty()

    /** Maps to another [Opt]. */
    fun <U> flatMap(mapper: (T) -> Opt<U>): Opt<U> = if (value != null) mapper(value) else empty()

    /** Runs [action] when present and returns this; throws when empty. */
    fun peek(action: (T) -> Unit): Opt<T> {
        action(get())
        return this
    }

    /** Runs all [actions] when present and returns this. */
    fun peeks(vararg actions: (T) -> Unit): Opt<T> {
        val v = get()
        actions.forEach { it(v) }
        return this
    }

    /** Returns this when non-empty, otherwise [supplier]'s value. */
    fun or(supplier: () -> Opt<T>): Opt<T> = if (value != null) this else supplier()

    /** The value or [other]. */
    fun orElse(other: T): T = value ?: other

    /** The value, or [other] when [ofTry] failed. */
    fun exceptionOrElse(other: T): T = if (exception == null) get() else other

    /** The value or [supplier]'s value. */
    fun orElseGet(supplier: () -> T): T = value ?: supplier()

    /** The value; throws when empty. */
    fun orElseThrow(): T = get()

    /** The value; throws the supplied exception when empty. */
    fun <X : Throwable> orElseThrow(exceptionSupplier: () -> X): T = value ?: throw exceptionSupplier()

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Opt<*>) return false
        return value == other.value && exception == other.exception
    }

    override fun hashCode(): Int = 31 * (value?.hashCode() ?: 0) + (exception?.hashCode() ?: 0)

    override fun toString(): String = if (value != null) "Opt[$value]" else "Opt.empty"

    companion object {
        /** An empty instance. */
        fun <T> empty(): Opt<T> = Opt(null)

        /** Wraps a non-null [value]; throws when it is `null`. */
        fun <T : Any> of(value: T): Opt<T> = Opt(value)

        /** Wraps a nullable [value]. */
        fun <T> ofNullable(value: T?): Opt<T> = Opt(value)

        /** Treats a blank string as empty. */
        fun ofBlankAble(value: String?): Opt<String> =
            if (value.isNullOrBlank()) Opt(null) else Opt(value)

        /** Treats an empty collection as empty. */
        fun <T, R : Collection<T>> ofEmptyAble(value: R?): Opt<R> =
            if (value == null || value.isEmpty()) Opt(null) else Opt(value)

        /** Runs [supplier], capturing any exception instead of propagating it. */
        fun <T> ofTry(supplier: () -> T): Opt<T> = try {
            ofNullable(supplier())
        } catch (e: Throwable) {
            Opt(null, e)
        }
    }
}
