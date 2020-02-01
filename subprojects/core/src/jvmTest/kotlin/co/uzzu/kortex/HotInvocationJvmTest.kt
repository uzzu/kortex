/**
 * Copyright 2019 Hirokazu Uzu. Use of this source code is governed by the Apache 2.0 license.
 */

package co.uzzu.kortex

import assertk.assertAll
import assertk.assertThat
import assertk.assertions.isEqualTo
import assertk.assertions.isInstanceOf
import assertk.assertions.isNotNull
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import kotlin.test.Test
import kotlin.test.fail

class HotInvocationJvmTest {
    class InvocationResult(private val value: Int) {
        override fun equals(other: Any?): Boolean = this === other
    }

    suspend fun suspendResult(value: Int): InvocationResult {
        delay(10)
        return InvocationResult(value * value)
    }

    @Test
    fun basicWithHot() = runBlocking(hotInvocation()) {
        val times = 3
        val results = mutableListOf<InvocationResult>()
        val jobs = mutableListOf<Job>()
        repeat(times) { i ->
            val job = launch {
                runCatching { withContext(Dispatchers.IO) { withHot("hot") { suspendResult(i) } } }
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
            assertThat(results.distinct().size).isEqualTo(1)
        }
    }

    @Test
    fun differentKeys() = runBlocking(hotInvocation()) {
        val times = 3
        val results = mutableListOf<InvocationResult>()
        val jobs = mutableListOf<Job>()
        repeat(times) { i ->
            val job = launch {
                runCatching { withContext(Dispatchers.IO) { withHot("hot$i") { suspendResult(i) } } }
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

    @Test
    fun thrownExceptionIfNotSetContext() = runBlocking {
        lateinit var actual: Throwable

        launch {
            runCatching { withHot("hot") { suspendResult(100) } }
                .onSuccess { fail("Unexpected result.") }
                .onFailure { actual = it }
        }.join()

        assertAll {
            assertThat(actual).isNotNull()
            assertThat(actual).isInstanceOf(IllegalArgumentException::class)
        }
    }
}
