package miles.scribble.redux

import assertk.assert
import assertk.assertions.isEqualTo
import miles.dispatch.core.*
import org.junit.Test

/**
 * @author - mbpeele on 12/22/17.
 */
class MiddleWareTest {

    val reducer : Reducer<SimpleEvents, SimpleState> = SimpleReducer()

    @Test
    fun testNoOpMiddleWareDoesNotModifyState() {
        val store = SimpleStore(SimpleState(0))
        val testMiddleWare = MiddleWares.noOp<SimpleEvents, SimpleState>()
        val dispatcher = Dispatchers.create(store, reducer, testMiddleWare)
        dispatcher.dispatch(SimpleEvents.AddEvent(5))

        assert(store.state.ordinal).isEqualTo(5)
    }

    @Test
    fun testModifyingEventFromMiddleware() {
        val store = SimpleStore(SimpleState(0))
        val modifyingAddEventMiddleware = MiddleWares.create<SimpleEvents, SimpleState> { forwarder, e ->
            if (e is SimpleEvents.AddEvent) {
                e.term = e.term + 1
            }
            forwarder.forward(e)
        }
        val dispatcher = Dispatchers.create(store, reducer, modifyingAddEventMiddleware)
        dispatcher.dispatch(SimpleEvents.AddEvent(5))

        assert(store.state.ordinal).isEqualTo(6)
    }

    @Test
    fun testModifyingEventFromMultipleMiddleWares() {
        val store = SimpleStore(SimpleState(0))

        val modifyingAddEventMiddleware1 = MiddleWares.create<SimpleEvents, SimpleState> { forwarder, e ->
            if (e is SimpleEvents.AddEvent) {
                e.term = e.term + 4
            }
            forwarder.forward(e)
        }
        val modifyingAddEventMiddleware2 = MiddleWares.create<SimpleEvents, SimpleState> { forwarder, e ->
            if (e is SimpleEvents.AddEvent) {
                e.term = e.term - 2
            }
            forwarder.forward(e)
        }

        val dispatcher = Dispatchers.create(store, reducer, modifyingAddEventMiddleware1, modifyingAddEventMiddleware2)
        val event = SimpleEvents.AddEvent(5)
        dispatcher.dispatch(event)

        assert(event.term).isEqualTo(7)
        assert(store.state.ordinal).isEqualTo(16)
    }

    @Test
    fun testNonMatchingPredicateMiddleWareDoesNotModifyState() {
        val store = SimpleStore(SimpleState(0))

        val predicateMiddleWare = MiddleWares.predicate({ event ->
            event is SimpleEvents.AddEvent
        }, { _: Middleware.Forwarder<SimpleEvents, SimpleState>, _: SimpleEvents ->
            SimpleState(27)
        })

        val dispatcher = Dispatchers.create(store, reducer, predicateMiddleWare)
        val event = SimpleEvents.SubtractEvent(5)
        dispatcher.dispatch(event)

        assert(store.state.ordinal).isEqualTo(-5)
    }
}