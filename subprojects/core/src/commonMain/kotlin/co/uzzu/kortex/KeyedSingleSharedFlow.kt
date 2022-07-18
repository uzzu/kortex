package co.uzzu.kortex

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.single
import kotlinx.coroutines.isActive
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlin.coroutines.CoroutineContext

/**
 * Coroutine context element by using CoroutineScope#withSingleShared or CoroutineScope#withSingleSharedFlow
 */
interface KeyedSingleSharedFlowContext : CoroutineContext.Element {
    override val key: CoroutineContext.Key<*> get() = Key

    companion object Key : CoroutineContext.Key<KeyedSingleSharedFlowContext>

    val mutex: Mutex

    val map: MutableMap<String, KeyedSingleSharedFlowContainer<*>>
}

/**
 * Create a new KeyedSingleSharedFlowContext object
 * @param mutex
 * @return A new KeyedSingleSharedFlowContext object
 */
fun keyedSingleSharedFlow(
    mutex: Mutex = Mutex(),
    map: MutableMap<String, KeyedSingleSharedFlowContainer<*>> = mutableMapOf()
): KeyedSingleSharedFlowContext =
    KeyedSingleSharedFlowContextImpl(mutex, map)

/**
 * Hot-invoke specified suspending function by unique key
 * @param context [CoroutineContext] to execute sharing flow
 * @param key unique key to use hot-invoke a specified suspending function
 * @param block suspending function to invoke
 * @return same value if specified suspend function was reused
 * @throws IllegalArgumentException if coroutineContext[KeyedSingleSharedFlowContext] was not set.
 */
@OptIn(FlowPreview::class)
@Suppress("SuspendFunctionOnCoroutineScope")
suspend fun <T> withSingleShared(context: CoroutineContext, key: String, block: suspend () -> T): T =
    singleSharedFlow(context, key, block.asFlow()).single()

/**
 * Hot-invoke specified suspending function with convert to flow by unique key
 * @param context [CoroutineContext] to execute sharing flow
 * @param key unique key to use hot-invoke a specified flow
 * @param block suspending function to invoke
 * @return same flow if specified suspend function was reused
 * @throws IllegalArgumentException if coroutineContext[[KeyedSingleSharedFlowContext] was not set.
 */
@Deprecated(
    "Use Flow<T>.singleShareIn",
    ReplaceWith(
        "block.asFlow().singleShareBy(context, key)",
        "kotlinx.coroutines.flow.asFlow"
    )
)
@OptIn(FlowPreview::class)
suspend fun <T> withSingleSharedFlow(context: CoroutineContext, key: String, block: suspend () -> T): Flow<T> =
    block.asFlow().shareSingleBy(context, key)

/**
 * Hot-invoke original flow by unique key
 * @param context [CoroutineContext] to execute sharing flow
 * @param key unique key to use hot-invoke a specified flow
 * @return flow which emits result of original flow if running
 * @throws IllegalArgumentException if coroutineContext[[KeyedSingleSharedFlowContext] was not set.
 */
suspend fun <T> Flow<T>.shareSingleBy(context: CoroutineContext, key: String): Flow<T> {
    requireNotNull(context[KeyedSingleSharedFlowContext]) {
        "Requires KeyedSingleSharedFlowContext to call this function. Please add into your coroutineContext."
    }
    return flow {
        val cachedFlow = singleSharedFlow(context, key, this@shareSingleBy)
        val result = cachedFlow.single()
        emit(result)
    }
}

@Suppress("SuspendFunctionOnCoroutineScope")
private suspend fun <T> singleSharedFlow(context: CoroutineContext, key: String, flow: Flow<T>): Flow<T> {
    val singleSharedContext = requireNotNull(context[KeyedSingleSharedFlowContext]) {
        "Requires KeyedSingleSharedFlowContext to call this function. Please add into your coroutineContext."
    }
    val mutex = singleSharedContext.mutex
    val map = singleSharedContext.map
    return mutex.withLock {
        if (map.containsKey(key) && !requireNotNull(map[key]).isCompleted()) {
            @Suppress("unchecked_cast")
            val cached = map[key] as KeyedSingleSharedFlowContainer<T>
            return@withLock cached.openSubscription()
        }

        map.remove(key)
        @Suppress("unchecked_cast")
        val created = map.getOrPut(key) {
            val container = KeyedSingleSharedFlowContainer(context, flow) {
                mutex.withLock {
                    map.remove(key)
                }
            }
            container
        } as KeyedSingleSharedFlowContainer<T>
        created.openSubscription()
    }
}

private class KeyedSingleSharedFlowContextImpl(
    override val mutex: Mutex,
    override val map: MutableMap<String, KeyedSingleSharedFlowContainer<*>>
) : KeyedSingleSharedFlowContext

class KeyedSingleSharedFlowContainer<T>
internal constructor(
    parentContext: CoroutineContext,
    flow: Flow<T>,
    private val preCompletion: suspend () -> Unit,
) {
    private val sharingScope: CoroutineScope = CoroutineScope(parentContext + SupervisorJob())
    private val sharingFlow: Flow<T> = flow
        .onEach {
            resultMutex.withLock {
                check(unsafeReturnValue == null) { "return values emitted as twice" }
                check(unsafeError == null) { "already emitted error" }
                unsafeReturnValue = it
            }
        }
        .catch {
            resultMutex.withLock {
                check(unsafeReturnValue == null) { "already emitted return value" }
                check(unsafeError == null) { "error emitted as twice" }
                unsafeError = it
            }
        }
    private val runningMutex: Mutex = Mutex()
    private val resultMutex: Mutex = Mutex()
    private val preCompletionMutex: Mutex = Mutex()
    private val refCountMutex: Mutex = Mutex()
    private var unsafeReturnValue: T? = null
    private var unsafeError: Throwable? = null
    private var unsafeIsRunningFlow: Boolean = false
    private var unsafeIsCompleted: Boolean = false
    private var unsafePreCompletionCalled: Boolean = false
    private var unsafeRefCount = 0

    fun openSubscription(): Flow<T> {
        return flow<T> {
            while (true) {
                if (!unsafeIsRunningFlow) {
                    break
                }
                val (returnValue, error, isCompleted) = resultMutex.withLock {
                    val isCompleted = unsafeReturnValue != null || unsafeError != null
                    unsafeIsCompleted = isCompleted
                    Triple(unsafeReturnValue, unsafeError, isCompleted)
                }
                val preCompletion = preCompletionMutex.withLock {
                    if (isCompleted && !unsafePreCompletionCalled) {
                        unsafePreCompletionCalled = true
                        this@KeyedSingleSharedFlowContainer.preCompletion
                    } else {
                        null
                    }
                }
                preCompletion?.invoke()

                if (error != null) {
                    throw error
                }
                if (returnValue != null) {
                    emit(returnValue)
                    break
                }
                delay(1)
            }
        }
            .onStart {
                ensureRunning()
                refCountMutex.withLock {
                    unsafeRefCount++
                }
            }
            .onCompletion {
                refCountMutex.withLock {
                    unsafeRefCount--
                    if (unsafeRefCount <= 0 && sharingScope.isActive) {
                        sharingScope.cancel()
                    }
                }
            }
    }

    suspend fun isCompleted(): Boolean = resultMutex.withLock {
        unsafeIsCompleted
    }

    private suspend fun ensureRunning() {
        runningMutex.withLock {
            if (unsafeIsRunningFlow) {
                return
            }
            unsafeIsRunningFlow = true
            sharingFlow.launchIn(sharingScope)
        }
    }
}
