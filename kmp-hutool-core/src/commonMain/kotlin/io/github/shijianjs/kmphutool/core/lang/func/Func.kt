package io.github.shijianjs.kmphutool.core.lang.func

/**
 * A function object, callable with any number of arguments.
 *
 * Port of `cn.hutool.core.lang.func.Func`. Kotlin has function types, but wrapping the call in an
 * object keeps it usable where Hutool's API expects a `Func`; [callWithRuntimeException] avoids
 * checked-exception ceremony, which Kotlin does not need but upstream callers rely on.
 *
 * @hutool cn.hutool.core.lang.func.Func
 */
fun interface Func<P, R> {
    /** Runs the function; exceptions are the function's own. */
    @Throws(Exception::class)
    fun call(vararg parameters: P): R

    /** Runs the function, wrapping any exception in a [RuntimeException]. */
    fun callWithRuntimeException(vararg parameters: P): R =
        try {
            call(*parameters)
        } catch (e: Exception) {
            throw RuntimeException(e)
        }
}

/**
 * Zero-argument function object.
 *
 * Port of `cn.hutool.core.lang.func.Func0`.
 *
 * @hutool cn.hutool.core.lang.func.Func0
 */
fun interface Func0<R> {
    @Throws(Exception::class)
    fun call(): R

    fun callWithRuntimeException(): R =
        try {
            call()
        } catch (e: Exception) {
            throw RuntimeException(e)
        }
}

/**
 * One-argument function object.
 *
 * Port of `cn.hutool.core.lang.func.Func1`.
 *
 * @hutool cn.hutool.core.lang.func.Func1
 */
fun interface Func1<P, R> {
    @Throws(Exception::class)
    fun call(parameter: P): R

    fun callWithRuntimeException(parameter: P): R =
        try {
            call(parameter)
        } catch (e: Exception) {
            throw RuntimeException(e)
        }
}

/**
 * Void function object with varargs.
 *
 * Port of `cn.hutool.core.lang.func.VoidFunc`.
 *
 * @hutool cn.hutool.core.lang.func.VoidFunc
 */
fun interface VoidFunc<P> {
    @Throws(Exception::class)
    fun call(vararg parameters: P)

    fun callWithRuntimeException(vararg parameters: P) {
        try {
            call(*parameters)
        } catch (e: Exception) {
            throw RuntimeException(e)
        }
    }
}

/**
 * Zero-argument void function object.
 *
 * Port of `cn.hutool.core.lang.func.VoidFunc0`.
 *
 * @hutool cn.hutool.core.lang.func.VoidFunc0
 */
fun interface VoidFunc0 {
    @Throws(Exception::class)
    fun call()

    fun callWithRuntimeException() {
        try {
            call()
        } catch (e: Exception) {
            throw RuntimeException(e)
        }
    }
}

/**
 * One-argument void function object.
 *
 * Port of `cn.hutool.core.lang.func.VoidFunc1`.
 *
 * @hutool cn.hutool.core.lang.func.VoidFunc1
 */
fun interface VoidFunc1<P> {
    @Throws(Exception::class)
    fun call(parameter: P)

    fun callWithRuntimeException(parameter: P) {
        try {
            call(parameter)
        } catch (e: Exception) {
            throw RuntimeException(e)
        }
    }
}

/**
 * Accepts three arguments.
 *
 * Port of `cn.hutool.core.lang.func.Consumer3`.
 *
 * @hutool cn.hutool.core.lang.func.Consumer3
 */
fun interface Consumer3<P1, P2, P3> {
    fun accept(p1: P1, p2: P2, p3: P3)
}
