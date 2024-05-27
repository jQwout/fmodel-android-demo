package fraktal.io.ext

import com.fraktalio.fmodel.domain.Decider
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

/**
 * Aggregate is on the Application layer. It is using Decider (a pure computation, domain layer) to calculate new events,
 * and EventBus(side effects, infrastructure layer) to emit/publish these events downstream. This way we are composing pure computation and effects.
 *
 * This aggregate is not storing events or state. Everything just flows in memory.
 * At least two more flavors could be implemented:
 * - EventSourcedAggregate = stores Events, not Decider state
 * - StateStoredAggregate = stores Decider state, not Events
 */
class Aggregate<C, S, E>(
    private val decider: Decider<C, S, E>,
    private val eventBus: EventBus<E>,
    scope: CoroutineScope,
) {
    /**
     * 1.Another approach could be to have CommandBus as a constructor parameter, and to inject command bus only (not the Aggregate) into the presentation layer to send commands.
     * This can further decouple aggregate from the presentation layer and enable location transparency, for example
     */
    private val _commandBus: CommandBus<C> = CommandBus()
    private val _deciderState: MutableStateFlow<S> = MutableStateFlow(decider.initialState)

    init {
        scope.launch {
            _commandBus.commands.collectLatest { command ->
                handle(command)
            }
        }
    }

    /**
     * Exposing `postCommand` function on the Aggregate.
     *
     * 2.Another approach could be to have CommandBus as a constructor parameter, and to inject command bus only (not the Aggregate) into the presentation layer to send commands.
     */
    suspend fun postCommand(command: C) = _commandBus.postCommand(command)
    private suspend fun handle(command: C) {
        val decidedEvents = decider.decide.invoke(command, _deciderState.value)

        decidedEvents.collect { event ->
            val deciderState = decider.evolve(_deciderState.value, event)
            eventBus.postEvent(event)
            _deciderState.value = deciderState
        }
    }
}

/**
 * Simple Command Bus
 *
 * https://elizarov.medium.com/shared-flows-broadcast-channels-899b675e805c
 */
private class CommandBus<C> {
    private val _commands = Channel<C>()
    val commands = _commands.receiveAsFlow() // expose as flow

    suspend fun postCommand(command: C) {
        _commands.send(command) // suspends on buffer overflow
    }
}