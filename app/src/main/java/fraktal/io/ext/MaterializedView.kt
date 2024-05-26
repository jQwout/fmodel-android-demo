package fraktal.io.ext

import com.fraktalio.fmodel.domain.View
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * MaterializedView is on the Application layer. It is using View (a pure computation, domain layer) to calculate new view state,
 * and EventBus(side effects, infrastructure layer) to subscribe to events upstream. This way we are composing pure computation and effects.
 *
 * This materialized view is not storing the state. Everything just flows in memory.
 * At least one more flavor could be implemented:
 * - MaterializedView = stores state
 */
class MaterializedView<S, E>(
    private val view: View<S, E>,
    private val eventBus: EventBus<E>,
    scope: CoroutineScope,
) {
    private val _viewState: MutableStateFlow<S> = MutableStateFlow(view.initialState)
    val viewStates = _viewState.asStateFlow()

    init {
        scope.launch {
            eventBus.events.collect { event ->
                handle(event)
            }
        }
    }

    private fun handle(event: E) {
        val viewState = view.evolve(_viewState.value, event)
        _viewState.value = viewState
    }
}

