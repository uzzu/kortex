package com.github.uzzu.kortex

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.channels.BroadcastChannel
import kotlinx.coroutines.channels.broadcast
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlin.coroutines.CoroutineContext

interface HotInvocation : CoroutineContext.Element {
    override val key: CoroutineContext.Key<*> get() = Key

    companion object Key : CoroutineContext.Key<HotInvocation>

    val mutex: Mutex
    val map: MutableMap<String, BroadcastChannel<*>>
}

fun hotInvocation(): HotInvocation = HotInvocationImpl()

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

private class HotInvocationImpl : HotInvocation {
    override val mutex: Mutex = Mutex()
    override val map: MutableMap<String, BroadcastChannel<*>> = mutableMapOf()
}
