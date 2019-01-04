package com.github.uzzu.kortex

import assertk.assert
import assertk.assertAll
import assertk.assertions.isEqualTo
import assertk.assertions.isNull
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.GlobalScope
import kotlin.test.Test

class HotInvocationTest {

    @Test
    fun createContext() {
        val hotInvocation = hotInvocation()
        val coroutineScope = CoroutineScope(hotInvocation)
        assertAll {
            assert(coroutineScope.coroutineContext[HotInvocation]).isEqualTo(hotInvocation)
        }
    }

    @Test
    fun isNullIfNotSet() {
        assertAll {
            assert(GlobalScope.coroutineContext[HotInvocation]).isNull()
        }
    }
}