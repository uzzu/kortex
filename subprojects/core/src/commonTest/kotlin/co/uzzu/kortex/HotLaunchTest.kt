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
import kotlin.test.Test

class HotLaunchTest {

    @Test
    fun createContext() {
        val hotLaunch = hotLaunch()
        val coroutineScope = CoroutineScope(hotLaunch)
        assertAll {
            assertThat(coroutineScope.coroutineContext[HotLaunch]).isEqualTo(hotLaunch)
        }
    }

    @Test
    fun isNullIfNotSet() {
        assertAll {
            assertThat(GlobalScope.coroutineContext[HotLaunch]).isNull()
        }
    }
}