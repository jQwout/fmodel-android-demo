package fraktal.io.ext

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * MaterializedQuery(originally MaterializedView) is on the Application layer. It is using Query(View) (a pure computation, domain layer) to calculate new query state,
 * and EventBus(side effects, infrastructure layer) to subscribe to events upstream. This way we are composing pure computation and effects.
 *
 * This materialized view is not storing the state. Everything just flows in memory.
 * At least one more flavor could be implemented:
 * - MaterializedQuery = stores state
 */
class MaterializedQuery<S, E>(
    private val view: Query<S, E>,
    private val eventBus: EventBus<E>,
    scope: CoroutineScope,
) {
    private val _viewState: MutableStateFlow<S> = MutableStateFlow(view.initialState)
    private val _eventBusProxy = EventBus<E>()
    val viewStates = _viewState.asStateFlow()
    val viewEvents = _eventBusProxy.events

    init {
        scope.launch {
            eventBus.events.collect { event ->
                handle(event)
                _eventBusProxy.postEvent(event)
            }
        }
    }

    private fun handle(event: E) {
        val viewState = view.evolve(_viewState.value, event)
        _viewState.value = viewState
    }

}

