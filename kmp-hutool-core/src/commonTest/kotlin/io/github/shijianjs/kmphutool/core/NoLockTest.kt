package io.github.shijianjs.kmphutool.core

import io.github.shijianjs.kmphutool.core.thread.lock.NoLock
import io.github.shijianjs.kmphutool.core.thread.lock.NoReadWriteLock
import kotlin.test.Test
import kotlin.test.assertTrue

class NoLockTest {
    @Test
    fun alwaysAcquires() {
        val lock = NoLock.INSTANCE
        lock.lock()
        assertTrue(lock.tryLock())
        lock.unlock()
        val readWrite = NoReadWriteLock()
        assertTrue(readWrite.readLock() === lock)
        assertTrue(readWrite.writeLock() === lock)
    }
}
