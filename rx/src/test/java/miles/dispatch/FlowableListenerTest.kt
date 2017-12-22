package miles.dispatch

import com.nhaarman.mockito_kotlin.any
import com.nhaarman.mockito_kotlin.verify
import com.nhaarman.mockito_kotlin.verifyZeroInteractions
import io.reactivex.subscribers.TestSubscriber
import miles.dispatch.core.SimpleStore
import miles.dispatch.core.State
import miles.dispatch.core.Store
import miles.dispatch.rx.toFlowable
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.junit.MockitoJUnitRunner

/**
 * Created by mbpeele on 8/12/17.
 */
@RunWith(MockitoJUnitRunner::class)
class FlowableListenerTest {

    @Mock lateinit var store : Store<SimpleState>

    @Test
    fun testCreatingFlowableDoesNotSubscribeToStore() {
        toFlowable(store)
        verifyZeroInteractions(store)
    }

    @Test
    fun testFlowableSubscribesToStore() {
        toFlowable(store).subscribe()
        verify(store).subscribe(any())
    }

    @Test
    fun testFlowableReceivesStoreUpdates() {
        val simpleStore = SimpleStore(SimpleState(0))
        val state = SimpleState(1)
        val testSubscriber = TestSubscriber<SimpleState>()
        toFlowable(simpleStore).subscribe(testSubscriber)
        simpleStore.state = state
        testSubscriber.assertValueCount(1)
        testSubscriber.assertValue(state)
    }

    @Test
    fun testDisposingFlowableStopsStoreUpdates() {
        val simpleStore = SimpleStore(SimpleState(0))
        val state = SimpleState(1)
        val testSubscriber = TestSubscriber<SimpleState>()
        toFlowable(simpleStore).subscribe(testSubscriber)
        simpleStore.state = state
        testSubscriber.assertValueCount(1)
        testSubscriber.assertValue(state)

        testSubscriber.dispose()
        simpleStore.state = SimpleState(2)
        testSubscriber.assertValueCount(1)
        assert(testSubscriber.isCancelled)
    }

    class SimpleState(val ordinal: Int) : State
}