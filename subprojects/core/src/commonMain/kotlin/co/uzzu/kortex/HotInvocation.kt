package co.uzzu.kortex

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.ObsoleteCoroutinesApi
import kotlinx.coroutines.channels.BroadcastChannel
import kotlinx.coroutines.channels.broadcast
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlin.coroutines.CoroutineContext

/**
 * Coroutine context element by using CoroutineScope#withHot
 */
@ObsoleteCoroutinesApi
@Deprecated("Please migrate to KeyedSingleSharedContext")
interface HotInvocation : CoroutineContext.Element {
    override val key: CoroutineContext.Key<*> get() = Key

    companion object Key : CoroutineContext.Key<HotInvocation>

    val mutex: Mutex

    val map: MutableMap<String, BroadcastChannel<*>>
}

/**
 * Create a new HotInvocation object
 * @param mutex
 * @return A new HotInvocation object
 */
@OptIn(ObsoleteCoroutinesApi::class)
@Deprecated("Please migrate to KeyedSingleSharedContext")
fun hotInvocation(
    mutex: Mutex = Mutex(),
    map: MutableMap<String, BroadcastChannel<*>> = mutableMapOf()
): HotInvocation =
    HotInvocationImpl(mutex, map)

/**
 * Hot-invoke specified suspending function by unique key
 * @param key unique key to use hot-invoke a specified suspending function
 * @param block suspending function to invoke
 * @return same value if specified suspend function was reused
 * @throws IllegalArgumentException if coroutineContext[HotInvocation] was not set.
 */
@OptIn(ExperimentalCoroutinesApi::class, kotlinx.coroutines.ObsoleteCoroutinesApi::class)
@Deprecated("Please migrate to CoroutineScope#withSingleShared")
@Suppress("SuspendFunctionOnCoroutineScope")
suspend fun <T> CoroutineScope.withHot(key: String, block: suspend () -> T): T {
    val invocation = requireNotNull(coroutineContext[HotInvocation]) {
        "Requires HotInvocation to call this function. Please add into your coroutineContext."
    }
    val mutex = invocation.mutex
    val map = invocation.map
    return mutex.withLock {
        if (map.containsKey(key) && !requireNotNull(map[key]).isClosedForSend) {
            @Suppress("unchecked_cast")
            val cached = map[key] as BroadcastChannel<T>
            return@withLock cached.openSubscription()
        }

        map.remove(key)
        @Suppress("unchecked_cast")
        val created = map.getOrPut(key) {
            broadcast {
                val result = block()
                mutex.withLock {
                    send(result)
                    map.remove(key)
                }
            }
        } as BroadcastChannel<T>
        created.openSubscription()
    }.receive()
}

@ObsoleteCoroutinesApi
private class HotInvocationImpl(
    override val mutex: Mutex,
    override val map: MutableMap<String, BroadcastChannel<*>>
) : HotInvocation
