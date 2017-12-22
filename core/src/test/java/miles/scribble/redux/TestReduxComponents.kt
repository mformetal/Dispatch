package miles.scribble.redux

import miles.dispatch.core.Event
import miles.dispatch.core.Reducer
import miles.dispatch.core.State

/**
 * Created by mbpeele on 8/12/17.
 */
class SimpleState(val ordinal: Int) : State

sealed class SimpleEvents : Event {
    class EventOne : SimpleEvents()
    class EventTWo : SimpleEvents()
    class AddEvent(var term: Int = 0) : SimpleEvents()
    class SubtractEvent(var term: Int = 0) : SimpleEvents()
}

class SimpleReducer : Reducer<SimpleEvents, SimpleState> {
    override fun reduce(event: SimpleEvents, state: SimpleState): SimpleState {
        return when (event) {
            is SimpleEvents.EventOne -> SimpleState(1)
            is SimpleEvents.EventTWo -> SimpleState(2)
            is SimpleEvents.AddEvent -> SimpleState(state.ordinal + event.term)
            is SimpleEvents.SubtractEvent -> SimpleState(state.ordinal - event.term)
        }
    }
}