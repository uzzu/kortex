package com.github.uzzu.kortex

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext

interface HotLaunch : CoroutineContext.Element {
    override val key: CoroutineContext.Key<*> get() = Key

    companion object Key : CoroutineContext.Key<HotLaunch>

    val map: MutableMap<String, Job>
}

fun hotLaunch(): HotLaunch = HotLaunchImpl()

fun CoroutineScope.launchHot(
    key: String,
    context: CoroutineContext = EmptyCoroutineContext,
    start: CoroutineStart = CoroutineStart.DEFAULT,
    block: suspend CoroutineScope.() -> Unit
): Job {
    val hotLaunch = requireNotNull(coroutineContext[HotLaunch]) {
        "Requires HotLaunch to call this function. Please add into your coroutineContext."
    }
    val map = hotLaunch.map
    if (map.containsKey(key)) {
        val job = requireNotNull(map[key])
        if (!job.isCompleted && !job.isCancelled) {
            return job
        }
    }

    map.remove(key)
    return hotLaunch.map.getOrPut(key) {
        launch(context, start, block).also {
            it.invokeOnCompletion { hotLaunch.map.remove(key) }
        }
    }
}

private class HotLaunchImpl : HotLaunch {
    override val map: MutableMap<String, Job> = mutableMapOf()
}
