package co.uzzu.kortex

import assertk.assertAll
import assertk.assertThat
import assertk.assertions.isEqualTo
import assertk.assertions.isNull
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.GlobalScope
import kotlin.test.Test

class KeyedSingleSharedFlowTest {

    @Test
    fun createContext() {
        val keyedSingleSharedFlowContext = keyedSingleSharedFlow()
        val coroutineScope = CoroutineScope(keyedSingleSharedFlowContext)
        assertAll {
            assertThat(coroutineScope.coroutineContext[KeyedSingleSharedFlowContext]).isEqualTo(keyedSingleSharedFlowContext)
        }
    }

    @Test
    fun isNullIfNotSet() {
        assertAll {
            assertThat(GlobalScope.coroutineContext[KeyedSingleSharedFlowContext]).isNull()
        }
    }
}
