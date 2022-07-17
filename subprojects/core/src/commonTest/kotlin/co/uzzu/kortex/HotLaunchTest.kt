/**
 * Copyright 2019 Hirokazu Uzu. Use of this source code is governed by the Apache 2.0 license.
 */

package co.uzzu.kortex

import assertk.assertAll
import assertk.assertThat
import assertk.assertions.isEqualTo
import assertk.assertions.isInstanceOf
import assertk.assertions.isNotNull
import assertk.assertions.isNull
import assertk.fail
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.test.runTest
import kotlin.test.Test

@OptIn(ExperimentalCoroutinesApi::class)
class HotLaunchTest {

    @Test
    fun createContext() {
        val hotLaunch = hotLaunch()
        val coroutineScope = CoroutineScope(hotLaunch)
        assertAll {
            assertThat(coroutineScope.coroutineContext[HotLaunch]).isEqualTo(hotLaunch)
        }
    }

    @Test
    fun isNullIfNotSet() = runTest {
        assertAll {
            assertThat(coroutineContext[HotLaunch]).isNull()
        }
    }

    @Test
    fun basicHotLaunch() = runTest(hotLaunch()) {
        var count = 0
        val times = 3
        val jobs = mutableListOf<Job>()
        val suspendFunction: suspend (Int) -> Unit = {
            count++
            println(it)
            delay(10)
        }

        repeat(times) {
            val job = backgroundScope.launchHot("hot") {
                suspendFunction(it)
            }
            jobs.add(job)
        }
        jobs.forEach { it.join() }

        assertAll {
            assertThat(jobs.size).isEqualTo(times)
            assertThat(jobs.distinct().size).isEqualTo(1)
            assertThat(count).isEqualTo(1)
        }
    }

    @Test
    fun differentKeys() = runTest(hotLaunch()) {
        var count = 0
        val times = 3
        val jobs = mutableListOf<Job>()
        val suspendFunction: suspend (Int) -> Unit = {
            count++
            println(it)
            delay(10)
        }

        repeat(times) { i ->
            val job = backgroundScope.launchHot("hot$i") {
                suspendFunction(i)
            }
            jobs.add(job)
        }
        jobs.forEach { it.join() }

        assertAll {
            assertThat(jobs.size).isEqualTo(times)
            assertThat(jobs.distinct().size).isEqualTo(times)
            assertThat(count).isEqualTo(times)
        }
    }

    @Test
    fun thrownExceptionIfNotSetContext() = runTest {
        lateinit var actual: Throwable

        val suspendFunction = suspend {
            delay(10)
        }

        runCatching { backgroundScope.launchHot("hot") { suspendFunction() }.join() }
            .onSuccess { fail("Unexpected result.") }
            .onFailure { actual = it }

        assertAll {
            assertThat(actual).isNotNull()
            assertThat(actual).isInstanceOf(IllegalArgumentException::class)
        }
    }
}
