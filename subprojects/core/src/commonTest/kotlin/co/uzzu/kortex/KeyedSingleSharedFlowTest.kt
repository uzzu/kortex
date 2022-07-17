package co.uzzu.kortex

import assertk.assertAll
import assertk.assertThat
import assertk.assertions.isBetween
import assertk.assertions.isEqualTo
import assertk.assertions.isNull
import assertk.fail
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.single
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.runTest
import kotlin.test.Test

@OptIn(ExperimentalCoroutinesApi::class)
class KeyedSingleSharedFlowTest {

    private class ExecutionResult(val value: Int) {
        override fun equals(other: Any?): Boolean = this === other

        override fun toString(): String = value.toString()
    }

    private data class InvocationResult(
        val index: Int,
        val executionResult: ExecutionResult,
    )

    private suspend fun suspendResult(value: Int): ExecutionResult {
        debugPrint("$value: started")
        delay(10)
        debugPrint("$value: delayed")
        return ExecutionResult(value * value)
    }

    private suspend fun suspendError(throwable: Throwable = Throwable()) {
        delay(10)
        throw throwable
    }

    @Test
    fun createContext() {
        val keyedSingleSharedFlowContext = keyedSingleSharedFlow()
        val coroutineScope = CoroutineScope(keyedSingleSharedFlowContext)
        assertAll {
            assertThat(coroutineScope.coroutineContext[KeyedSingleSharedFlowContext]).isEqualTo(keyedSingleSharedFlowContext)
        }
    }

    @Test
    fun isNullIfNotSet() = runTest {
        assertAll {
            assertThat(coroutineContext[KeyedSingleSharedFlowContext]).isNull()
        }
    }

    @Test
    fun keyedSharedFlowToSuspendingFunction() = runTest(keyedSingleSharedFlow()) {
        val times = 3
        val results = mutableListOf<ExecutionResult>()
        val jobs = mutableListOf<Job>()
        repeat(times) { i ->
            val job = backgroundScope.launch {
                runCatching {
                    withSingleShared(coroutineContext, "hot") { suspendResult(i) }
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
    fun keyedSharedFlowError() = runTest(keyedSingleSharedFlow()) {
        val times = 3
        val errors = mutableListOf<Throwable>()
        val jobs = mutableListOf<Job>()
        repeat(times) {
            val job = backgroundScope.launch {
                runCatching {
                    withSingleShared(this.coroutineContext, "hot") { suspendError() }
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
    fun keyedSingleSharedFlowWithDifferentKeys() = runTest(keyedSingleSharedFlow()) {
        val times = 3
        val results = mutableListOf<ExecutionResult>()
        val jobs = mutableListOf<Job>()
        repeat(times) { i ->
            val job = backgroundScope.launch {
                runCatching {
                    withSingleShared(coroutineContext, "hot$i") { suspendResult(i) }
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

    @OptIn(FlowPreview::class)
    @Test
    fun shareSingleBy() = runTest(keyedSingleSharedFlow()) {
        val times = 100
        val results = mutableListOf<InvocationResult>()
        val flows = mutableListOf<Flow<InvocationResult>>()
        val reuseStarted = mutableListOf<Int>()
        repeat(times) { index ->
            val suspendFun: suspend () -> ExecutionResult = { suspendResult(index) }
            val flow = suspendFun
                .asFlow()
                .onStart {
                    debugPrint("$index: Execution started")
                    reuseStarted.add(index)
                }
                .shareSingleBy(backgroundScope.coroutineContext, "hot")
                .map { InvocationResult(index, it) }
                .onStart { debugPrint("$index: Invocation started") }
                .onEach { debugPrint("$index: result $it") }
            flows.add(flow)
        }
        val combined = combine(flows) { it.toList() }
        results.addAll(combined.single())
        results.sortBy { it.index }
        assertAll {
            assertThat(results.size).isEqualTo(times)
            reuseStarted.forEachIndexed { index, start ->
                val end = if (index >= reuseStarted.size - 1) {
                    results.lastIndex
                } else {
                    reuseStarted[index + 1]
                }
                for (j in start..end) {
                    assertThat(results[j].executionResult.value).isEqualTo(start * start)
                }
            }
        }
    }

    @Test
    fun keyedSharedFlow() = runTest(keyedSingleSharedFlow()) {
        val times = 100
        val results = mutableListOf<ExecutionResult>()
        val flows = mutableListOf<Flow<ExecutionResult>>()
        repeat(times) { index ->
            val flow = withSingleSharedFlow(backgroundScope.coroutineContext, "hot") { suspendResult(index) }
                .onStart { debugPrint("$index: start") }
                .onEach { debugPrint("$index: result $it") }
            flows.add(flow)
        }
        val combined = combine(flows) { it.toList() }
        results.addAll(combined.single())
        assertAll {
            assertThat(results.size).isEqualTo(times)
            assertThat(results.distinct().size).isBetween(1, 3)
        }
    }

    @Suppress("ConstantConditionIf")
    private fun debugPrint(value: String) {
        if (false) {
            println(value)
        }
    }
}
