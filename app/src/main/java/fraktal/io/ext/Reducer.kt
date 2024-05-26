package fraktal.io.ext


import com.fraktalio.fmodel.domain.Decider
import com.fraktalio.fmodel.domain.View
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.Channel.Factory.CONFLATED
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.consumeAsFlow
import kotlinx.coroutines.launch


/**
 * Actually, working with YU would make it easier to keep hoist of the state and events in one place.
 *
 * It seems like a TEA pattern or an MVI variation. But it would be friendly for the Android community.
 *
 * Because we need effects that do not require saving (for playing animations or displaying toasts), saving may affect the behavior of the screen when it is recomposed.
 */

class Reducer<C, S, U, E>(
    private val decider: Decider<C, S, E>,
    private val view: View<U, E>,
    scope: CoroutineScope,
    capacity: Int = CONFLATED
    ) {
    private val _deciderState: MutableStateFlow<S> = MutableStateFlow(decider.initialState)
    private val _viewState: MutableStateFlow<U> = MutableStateFlow(view.initialState)
    private val _commands: Channel<C> = Channel(capacity = capacity)
    private val _events: Channel<E?> = Channel(capacity = capacity)

    val uiStates = _viewState.asStateFlow()
    val events = _events.consumeAsFlow()

    init {
        scope.launch {
            // Only Collect LATEST is supported ???
            _commands.consumeAsFlow().collectLatest { command ->
                handle(command)
            }
        }
    }

    suspend fun emit(command: C) {
        _commands.send(command)
    }

    private suspend fun handle(command: C) {
        val decidedEvents = decider.decide.invoke(command, _deciderState.value)

        decidedEvents.collect { event ->
            val deciderState = decider.evolve(_deciderState.value, event)
            val viewState = view.evolve(_viewState.value, event)
            _events.send(event)
            _deciderState.value = deciderState
            _viewState.value = viewState
        }
    }
}

