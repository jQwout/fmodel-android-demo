package fraktal.io.ext


import com.fraktalio.fmodel.domain.Decider
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.Channel.Factory.CONFLATED
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.consumeAsFlow
import kotlinx.coroutines.launch

fun <C, S, E> Reducer(
    decider: Decider<C, S, E>,
    scope: CoroutineScope
) = Reducer(decider, scope, { it })

/**
 * Actually, working with YU would make it easier to keep hoist of the state and events in one place.
 *
 * It seems like a TEA pattern or an MVI variation. But it would be friendly for the Android community.
 *
 * Because we need effects that do not require saving (for playing animations or displaying toasts), saving may affect the behavior of the screen when it is recomposed.
 */

class Reducer<C, S, U, E>(
    private val decider: Decider<C, S, E>,
    private val scope: CoroutineScope,
    private val uiMapper: (S) -> U,

    private val _states: MutableStateFlow<S> = MutableStateFlow(decider.initialState),
    private val _uiStates: MutableStateFlow<U> = MutableStateFlow(uiMapper(decider.initialState)),
    private val commandChannel: Channel<C> = Channel<C>(capacity = CONFLATED),

    private val _events: Channel<E?> = Channel(capacity = CONFLATED),
) {
    val uiStates = _uiStates.asStateFlow()
    val events = _events.consumeAsFlow()

    init {
        scope.launch {
            commandChannel.consumeAsFlow().collectLatest { command ->
                handle(command)
            }
        }
    }

    suspend fun emit(command: C) {
        commandChannel.send(command)
    }

    private suspend fun handle(command: C) {
        val decidedEvents = decider.decide.invoke(command, _states.value)

        decidedEvents.collect { event ->
            val evolvedValue = decider.evolve(_states.value, event)
            _events.send(event)
            _states.value = evolvedValue
            _uiStates.value = uiMapper(evolvedValue)
        }
    }
}

