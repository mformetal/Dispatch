package miles.dispatch.rx

import io.reactivex.BackpressureStrategy
import io.reactivex.Flowable
import io.reactivex.FlowableEmitter
import io.reactivex.functions.Cancellable
import miles.dispatch.core.State
import miles.dispatch.core.StateChangeListener
import miles.dispatch.core.Store

/**
 * Created by mbpeele on 8/12/17.
 */
fun <S : State> toFlowable(store: Store<S>) : Flowable<S> {
    return Flowable.create({
        // Should I emit an initial state here, or just wait for new states?
        val listener = FlowableListener(store, it)
        store.subscribe(listener)
    }, BackpressureStrategy.LATEST)
}

fun <S : State> Store<S>.asFlowable() : Flowable<S> {
    return toFlowable(this)
}

private class FlowableListener<in S : State>(
        private val store: Store<S>,
        private val emitter: FlowableEmitter<S>) : StateChangeListener<S>, Cancellable {

    override fun onStateChanged(state: S) {
        emitter.onNext(state)
    }

    override fun cancel() {
        store.unsubscribe(this)
    }
}