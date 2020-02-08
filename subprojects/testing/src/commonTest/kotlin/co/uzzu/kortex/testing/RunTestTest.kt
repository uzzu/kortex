package co.uzzu.kortex.testing

import assertk.assertAll
import assertk.assertThat
import assertk.assertions.isTrue
import kotlinx.coroutines.delay
import kotlin.reflect.typeOf
import kotlin.test.Ignore
import kotlin.test.Test

class RunTestTest {
    @Test
    fun suspendingFunctionSuccess() = runTest {
        delay(100)
        assertThat(true).isTrue()
    }

    @Ignore
    @Test
    fun suspendingFunctionFailure() = runTest {
        throw Exception("Suspending function with failure. Please ignore this test case")
    }
}
