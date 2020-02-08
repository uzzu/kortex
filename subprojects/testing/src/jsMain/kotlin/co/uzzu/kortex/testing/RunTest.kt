package co.uzzu.kortex.testing

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.promise
import kotlinx.coroutines.withTimeout
import kotlin.coroutines.CoroutineContext

actual fun runTest(
    coroutineContext: CoroutineContext,
    timeout: Long,
    block: suspend CoroutineScope.() -> Unit
) {
    GlobalScope.promise(coroutineContext) {
        withTimeout(timeout) {
            block()
        }
    }
}
