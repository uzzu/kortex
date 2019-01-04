package com.github.uzzu.kortex

import assertk.assert
import assertk.assertAll
import assertk.assertions.isEqualTo
import assertk.assertions.isInstanceOf
import assertk.assertions.isNotNull
import assertk.fail
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlin.test.Test

class HotLaunchJvmTest {
    @Test
    fun basicHotLaunch() = runBlocking(hotLaunch()) {
        var count = 0
        val times = 3
        val jobs = mutableListOf<Job>()
        val suspendFunction = suspend {
            count++
            delay(10)
        }

        repeat(times) {
            val job = launchHot("hot") {
                suspendFunction()
            }
            jobs.add(job)
        }
        jobs.forEach { it.join() }

        assertAll {
            assert(jobs.size).isEqualTo(times)
            assert(jobs.distinct().size).isEqualTo(1)
            assert(count).isEqualTo(1)
        }
    }

    @Test
    fun differentKeys() = runBlocking(hotLaunch()) {
        var count = 0
        val times = 3
        val jobs = mutableListOf<Job>()
        val suspendFunction = suspend {
            count++
            delay(10)
        }

        repeat(times) { i ->
            val job = launchHot("hot$i") {
                suspendFunction()
            }
            jobs.add(job)
        }
        jobs.forEach { it.join() }

        assertAll {
            assert(jobs.size).isEqualTo(times)
            assert(jobs.distinct().size).isEqualTo(times)
            assert(count).isEqualTo(times)
        }
    }

    @Test
    fun thrownExceptionIfNotSetContext() = runBlocking {
        lateinit var actual: Throwable

        val suspendFunction = suspend {
            delay(10)
        }

        launch {
            runCatching { withHot("hot") { suspendFunction() } }
                .onSuccess { fail("Unexpected result.") }
                .onFailure { actual = it }
        }.join()

        assertAll {
            assert(actual).isNotNull()
            assert(actual).isInstanceOf(IllegalArgumentException::class)
        }
    }
}