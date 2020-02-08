package co.uzzu.kortex.testing

import kotlinx.coroutines.CoroutineScope
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext

/**
 * To use test suspending function
 * @param coroutineContext additional coroutine contexts
 * @param timeout suspending function with timeout
 * @param block testing body
 */
expect fun runTest(
    coroutineContext: CoroutineContext = EmptyCoroutineContext,
    timeout: Long = 5000L,
    block: suspend CoroutineScope.() -> Unit
)
