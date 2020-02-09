package co.uzzu.kortex

import assertk.assertAll
import assertk.assertThat
import assertk.assertions.isEqualTo
import assertk.assertions.isInstanceOf
import assertk.assertions.isNotNull
import assertk.assertions.isNull
import assertk.fail
import co.uzzu.kortex.testing.runTest
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.test.Test

class HotLaunchProcessingTest {

    @Test
    fun basicHotLaunch() = runTest(coroutineContext = hotLaunch()) {
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
            assertThat(jobs.size).isEqualTo(times)
            assertThat(jobs.distinct().size).isEqualTo(1)
            assertThat(count).isEqualTo(1)
        }
    }

    @Test
    fun differentKeys() = runTest(coroutineContext = hotLaunch()) {
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

        launch {
            runCatching { withHot("hot") { suspendFunction() } }
                .onSuccess { fail("Unexpected result.") }
                .onFailure { actual = it }
        }.join()

        assertAll {
            assertThat(actual).isNotNull()
            assertThat(actual).isInstanceOf(IllegalArgumentException::class)
        }
    }
}

class HotLaunchCreationTest {
    @Test
    fun createContext() {
        val hotLaunch = hotLaunch()
        val coroutineScope = CoroutineScope(hotLaunch)
        assertAll {
            assertThat(coroutineScope.coroutineContext[HotLaunch]).isEqualTo(hotLaunch)
        }
    }

    @Test
    fun isNullIfNotSet() {
        assertAll {
            assertThat(GlobalScope.coroutineContext[HotLaunch]).isNull()
        }
    }
}
