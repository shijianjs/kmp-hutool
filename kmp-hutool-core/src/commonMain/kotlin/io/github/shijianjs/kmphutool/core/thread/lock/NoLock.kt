package io.github.shijianjs.kmphutool.core.thread.lock

/**
 * A no-op lock, useful as a strategy object where locking must be disabled.
 *
 * Port of `cn.hutool.core.thread.lock.NoLock`. `java.util.concurrent.locks.Lock` and `Condition`
 * are JVM-only; this class keeps the same methods that are meaningful in common code.
 *
 * @hutool cn.hutool.core.thread.lock.NoLock
 */
class NoLock {
    fun lock() = Unit

    fun lockInterruptibly() = Unit

    fun tryLock(): Boolean = true

    fun unlock() = Unit

    fun newCondition(): Nothing =
        throw UnsupportedOperationException("NoLock's newCondition method is unsupported")

    companion object {
        val INSTANCE = NoLock()
    }
}
