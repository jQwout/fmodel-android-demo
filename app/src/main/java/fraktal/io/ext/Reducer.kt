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

/**
 * Simple Reducer.
 * It belongs to the application layer.
 * It orchestrates the UI/Infra and the core domain logic (Decider)
 *
 * "This particular Reducer is using only decider, and there is no infrastructure param in the constructor.
 *  This make sense for this demo, as the events/states are published directly from the Decider by emitting the Time/seconds. There is no DB to read from.
 *  For example: `repo: StateRepository<C, S>` "
 *
 */
class Reducer<C, S, E>(
    private val decider: Decider<C, S, E>,
    scope: CoroutineScope,
) {
    private val _states: MutableStateFlow<S> = MutableStateFlow(decider.initialState)
    private val _commandsChannel: Channel<C> = Channel(capacity = CONFLATED)
    private val _eventsChannel: Channel<E?> = Channel(capacity = CONFLATED)

    val states = _states.asStateFlow()
    val events = _eventsChannel.consumeAsFlow()

    init {
        scope.launch {
            _commandsChannel.consumeAsFlow().collectLatest { command ->
                handle(command)
            }
        }
    }

    suspend fun emit(command: C) {
        _commandsChannel.send(command)
    }

    private suspend fun handle(command: C) {
        val decidedEvents = decider.decide.invoke(command, _states.value)

        decidedEvents.collect { event ->
            val evolvedValue = decider.evolve(_states.value, event)
            _eventsChannel.send(event)
            _states.value = evolvedValue
        }
    }
}

