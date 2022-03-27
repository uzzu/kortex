package co.uzzu.kortex

import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

suspend inline fun <T> Mutex.withLockContext(owner: Any? = null, action: (MutexContext) -> T): T {
    val context = MutexContextImpl()
    var actionError: Throwable? = null
    val result = try {
        withLock(owner) { action(context) }
    } catch (e: Throwable) {
        actionError = e
        null
    } finally {
        var deferActionsError: Throwable? = null
        for (deferAction in context.deferActions) {
            try {
                deferAction()
            } catch (e: Throwable) {
                deferActionsError = e
            }
        }
        if (actionError != null) {
            throw actionError
        }
        if (deferActionsError != null) {
            throw deferActionsError
        }
    }
    return checkNotNull(result)
}

interface MutexContext {
    fun defer(action: suspend () -> Unit)
}

class MutexContextImpl : MutexContext {
    val deferActions: List<suspend () -> Unit>
        get() = mutableDeferActions.toList()
    private var mutableDeferActions: MutableList<suspend () -> Unit> = mutableListOf()

    override fun defer(action: suspend () -> Unit) {
        mutableDeferActions.add(action)
    }
}
