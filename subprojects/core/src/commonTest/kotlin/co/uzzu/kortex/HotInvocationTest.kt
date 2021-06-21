/**
 * Copyright 2019 Hirokazu Uzu. Use of this source code is governed by the Apache 2.0 license.
 */

package co.uzzu.kortex

import assertk.assertAll
import assertk.assertThat
import assertk.assertions.isEqualTo
import assertk.assertions.isNull
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.flow
import kotlin.test.Test

class HotInvocationTest {

    @Test
    fun createContext() {
        val hotInvocation = hotInvocation()
        val coroutineScope = CoroutineScope(hotInvocation)
        assertAll {
            assertThat(coroutineScope.coroutineContext[HotInvocation]).isEqualTo(hotInvocation)
        }
    }

    @Test
    fun isNullIfNotSet() {
        assertAll {
            assertThat(GlobalScope.coroutineContext[HotInvocation]).isNull()
        }
    }
}
