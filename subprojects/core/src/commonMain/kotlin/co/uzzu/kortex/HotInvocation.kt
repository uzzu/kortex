package co.uzzu.kortex

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.BroadcastChannel
import kotlinx.coroutines.channels.broadcast
import kotlinx.coroutines.sync.Semaphore
import kotlinx.coroutines.sync.withPermit
import kotlin.coroutines.CoroutineContext

/**
 * Coroutine context element by using CoroutineScope#withHot
 */
@ExperimentalCoroutinesApi
interface HotInvocation : CoroutineContext.Element {
    override val key: CoroutineContext.Key<*> get() = Key

    companion object Key : CoroutineContext.Key<HotInvocation>

    val semaphore: Semaphore

    val map: MutableMap<String, BroadcastChannel<*>>
}

/**
 * Create a new HotInvocation object
 * @param semaphore
 * @return A new HotInvocation object
 */
@ExperimentalCoroutinesApi
fun hotInvocation(
    semaphore: Semaphore = Semaphore(1),
    map: MutableMap<String, BroadcastChannel<*>> = mutableMapOf()
): HotInvocation =
    HotInvocationImpl(semaphore, map)

/**
 * Hot-invoke specified suspending function by unique key
 * @param   key unique key to use hot-invoke a specified suspending function
 * @param   block suspending function to invoke
 * @return  same value if specified suspend function was reused
 * @throws  IllegalArgumentException if coroutineContext[HotInvocation] was not set.
 */
@ExperimentalCoroutinesApi
@Suppress("SuspendFunctionOnCoroutineScope")
suspend fun <T> CoroutineScope.withHot(key: String, block: suspend () -> T): T {
    val invocation = requireNotNull(coroutineContext[HotInvocation]) {
        "Requires HotInvocation to call this function. Please add into your coroutineContext."
    }
    val semaphore = invocation.semaphore
    val map = invocation.map
    return semaphore.withPermit {
        if (map.containsKey(key) && !requireNotNull(map[key]).isClosedForSend) {
            @Suppress("unchecked_cast")
            val cached = map[key] as BroadcastChannel<T>
            return@withPermit cached.openSubscription()
        }

        map.remove(key)
        @Suppress("unchecked_cast")
        val created = map.getOrPut(key) {
            broadcast {
                val result = block()
                semaphore.withPermit {
                    send(result)
                    map.remove(key)
                }
            }
        } as BroadcastChannel<T>
        created.openSubscription()
    }.receive()
}

@ExperimentalCoroutinesApi
private class HotInvocationImpl(
    override val semaphore: Semaphore,
    override val map: MutableMap<String, BroadcastChannel<*>>
) : HotInvocation {
    init {
//        ensureNeverFrozen()
    }
}
