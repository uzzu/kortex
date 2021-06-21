package co.uzzu.kortex

import assertk.assertAll
import assertk.assertThat
import assertk.assertions.isEqualTo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.single
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Test
import kotlin.test.fail

class KeyedSingleSharedFlowJvmTest {
    private class InvocationResult(val value: Int) {
        override fun equals(other: Any?): Boolean = this === other
    }

    private suspend fun suspendResult(value: Int): InvocationResult {
        delay(10)
        return InvocationResult(value * value)
    }

    private suspend fun suspendError(throwable: Throwable = Throwable()) {
        delay(10)
        throw throwable
    }

    @Test
    fun keyedSharedFlowBasics() = runBlocking(keyedSingleSharedFlow()) {
        val times = 5
        val results = mutableListOf<InvocationResult>()
        val flows = mutableListOf<Flow<InvocationResult>>()
        repeat(times) { index ->
            val flow = withSingleSharedFlow(this.coroutineContext + Dispatchers.IO, "hot") { suspendResult(index) }
            flows.add(flow)
        }
        val combined = combine(flows) { it.toList() }
        results.addAll(combined.single())

        assertAll {
            assertThat(results.size).isEqualTo(times)
            assertThat(results.distinct().size).isEqualTo(1)
        }
    }

    @Test
    fun keyedSharedFlowToSuspendingFunction() = runBlocking(keyedSingleSharedFlow()) {
        val times = 3
        val results = mutableListOf<InvocationResult>()
        val jobs = mutableListOf<Job>()
        repeat(times) { i ->
            val job = launch {
                runCatching {
                    withSingleShared(this.coroutineContext + Dispatchers.IO, "hot") { suspendResult(i) }
                }
                    .onSuccess { results.add(it) }
                    .onFailure { fail("unexpected error") }
            }
            jobs.add(job)
        }
        jobs.forEach {
            it.join()
        }
        assertAll {
            assertThat(results.size).isEqualTo(times)
            assertThat(results.distinct().size).isEqualTo(1)
        }
    }

    @Test
    fun keyedSharedFlowError() = runBlocking(keyedSingleSharedFlow()) {
        val times = 3
        val errors = mutableListOf<Throwable>()
        val jobs = mutableListOf<Job>()
        repeat(times) {
            val job = launch {
                runCatching {
                    withSingleShared(this.coroutineContext + Dispatchers.IO, "hot") { suspendError() }
                }
                    .onSuccess { fail("unreachable") }
                    .onFailure { errors.add(it) }
            }
            jobs.add(job)
        }
        jobs.forEach {
            it.join()
        }
        assertAll {
            assertThat(errors.size).isEqualTo(times)
            assertThat(errors.distinct().size).isEqualTo(1)
        }
    }

    @Test
    fun keyedSingleSharedFlowWithDifferentKeys() = runBlocking(keyedSingleSharedFlow()) {
        val times = 3
        val results = mutableListOf<InvocationResult>()
        val jobs = mutableListOf<Job>()
        repeat(times) { i ->
            val job = launch {
                runCatching {
                    withSingleShared(this.coroutineContext + Dispatchers.IO, "hot$i") { suspendResult(i) }
                }
                    .onSuccess { results.add(it) }
                    .onFailure { fail("[$i] Exception occurred: $it") }
            }
            jobs.add(job)
        }
        jobs.forEach {
            it.join()
        }
        assertAll {
            assertThat(results.size).isEqualTo(times)
            assertThat(results.distinct().size).isEqualTo(times)
        }
    }
}
