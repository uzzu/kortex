package co.uzzu.kortex

import assertk.assertThat
import assertk.assertions.isEqualTo
import assertk.assertions.isTrue
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.test.runTest
import kotlin.test.Test

class MutexContextDeferTest {

    @Test
    fun deferAction() = runTest {
        val mutex = Mutex()
        val builder = StringBuilder()
        val actual = mutex.withLockContext { context ->
            builder.append("a")
            context.defer { builder.append("b") }
            context.defer { builder.append("c") }
            builder.append("d")
            builder
        }
        assertThat(actual.toString()).isEqualTo("adbc")
    }

    @Test
    fun ensureInvokeAfterUnlocked() = runTest {
        val mutex = Mutex()
        var actual1Called = false
        var actual2Called = false
        try {
            mutex.withLockContext { context ->
                context.defer { actual1Called = true }
                context.defer { actual2Called = true }
                throw Throwable()
            }
        } catch (_: Throwable) {
        }
        assertThat(actual1Called).isTrue()
        assertThat(actual2Called).isTrue()
    }

    @Test
    fun ensureInvokeIfThrownOnDefer() = runTest {
        val mutex = Mutex()
        var actualCalled = false
        try {
            mutex.withLockContext { context ->
                context.defer { throw Throwable() }
                context.defer { actualCalled = true }
            }
        } catch (_: Throwable) {
        }
        assertThat(actualCalled).isTrue()
    }
}
