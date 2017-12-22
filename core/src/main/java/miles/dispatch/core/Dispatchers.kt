package miles.dispatch.core

@Suppress("AddVarianceModifier")
interface Dispatcher<E : Event, A> {

    fun dispatch(event: E) : A

}

class SimpleDispatcher<E : Event, S: State>(private val store: Store<S>,
                                            private val reducer: Reducer<E, S>,
                                            private val middleWares: List<Middleware<E, S>>) : Dispatcher<E, E> {

    override fun dispatch(event: E) : E {
        val iterator = middleWares.iterator()
        if (iterator.hasNext()) {
            while (iterator.hasNext()) {
                val middleWare = iterator.next()
                val forwarder = object : Middleware.Forwarder<E, S> {
                    override fun forward(event: E): S {
                        return reducer.reduce(event, store.state)
                    }
                }
                store.state = middleWare.doNext(forwarder, event)
            }
        } else {
            store.state = reducer.reduce(event, store.state)
        }

        return event
    }
}

object Dispatchers {

    fun <E: Event, S: State> create(store: Store<S>, reducer: Reducer<E, S>,
                                    vararg middleware: Middleware<E, S>) : Dispatcher<E, E> {
        return SimpleDispatcher(store, reducer, middleware.toList())
    }
}