package co.uzzu.kortex.testing

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withTimeout
import kotlin.coroutines.CoroutineContext

actual fun runTest(
    coroutineContext: CoroutineContext,
    timeout: Long,
    block: suspend CoroutineScope.() -> Unit
) {
    runBlocking(coroutineContext) {
        withTimeout(timeout) {
            block()
        }
    }
}

