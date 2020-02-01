package co.uzzu.kortex

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext

/**
 * Coroutine context element by using CoroutineScope#launchHot
 */
interface HotLaunch : CoroutineContext.Element {
    override val key: CoroutineContext.Key<*> get() = Key

    companion object Key : CoroutineContext.Key<HotLaunch>

    val map: MutableMap<String, Job>
}

/**
 * Create a new HotLaunch object
 * @return A new HotLaunch object
 */
fun hotLaunch(map: MutableMap<String, Job> = mutableMapOf()): HotLaunch = HotLaunchImpl(map)

/**
 * Hot-launch coroutine by unique key
 * @param   key unique key to use hot-invoke a coroutine.
 * @param   context to use CoroutineScope#launch
 * @param   start to use CoroutineScope#launch
 * @param   block to use CoroutineScope#launch
 * @return  Same job if a coroutine was reused
 * @throws  IllegalArgumentException if coroutineContext[HotLaunch] was not set.
 */
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

private class HotLaunchImpl(
    override val map: MutableMap<String, Job>
) : HotLaunch
