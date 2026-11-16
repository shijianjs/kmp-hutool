package io.github.shijianjs.kmphutool.core.thread.lock

/**
 * A read/write lock whose read and write sides are both [NoLock].
 *
 * Port of `cn.hutool.core.thread.lock.NoReadWriteLock`. The JVM `ReadWriteLock` interface is
 * replaced by these two factory methods.
 *
 * @hutool cn.hutool.core.thread.lock.NoReadWriteLock
 */
class NoReadWriteLock {
    fun readLock(): NoLock = NoLock.INSTANCE

    fun writeLock(): NoLock = NoLock.INSTANCE
}
