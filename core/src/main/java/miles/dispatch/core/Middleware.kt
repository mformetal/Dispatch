package miles.dispatch.core

/**
 * @author - mbpeele on 12/22/17.
 */
interface Middleware<E : Event, S : State> {

    fun doNext(forwarder: Forwarder<E, S>, event: E) : S

    @Suppress("AddVarianceModifier")
    interface Forwarder<E : Event, S : State> {

        fun forward(event: E) : S
    }
}

object MiddleWares {

    fun <E : Event, S : State> noOp() : Middleware<E, S> {
        return object : Middleware<E, S> {
            override fun doNext(forwarder: Middleware.Forwarder<E, S>, event: E): S {
                return forwarder.forward(event)
            }
        }
    }

    fun <E : Event, S: State> create(lambda: (Middleware.Forwarder<E, S>, E) -> S) : Middleware<E, S> {
        return object : Middleware<E, S> {
            override fun doNext(forwarder: Middleware.Forwarder<E, S>, event: E): S {
                return lambda.invoke(forwarder, event)
            }
        }
    }

    fun <E : Event, S : State> predicate(predicate: (E) -> Boolean,
                                         lambda: (Middleware.Forwarder<E, S>, E) -> S) : Middleware<E, S> {
        return object : Middleware<E, S> {
            override fun doNext(forwarder: Middleware.Forwarder<E, S>, event: E): S {
                return if (predicate.invoke(event)) {
                    lambda.invoke(forwarder, event)
                } else {
                    forwarder.forward(event)
                }
            }
        }
    }
}