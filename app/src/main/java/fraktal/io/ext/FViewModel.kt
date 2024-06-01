package fraktal.io.ext

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

/**
 * Lightweight template for building viewmodels familiar to Android developers
 * Adapter layer
 * - MaterializedQuery = stores state
 * - Aggregate = command handler
 * - C = command
 * - S = domain-state
 * - Ui = view-state
 * - E = event
 *
 *   TODO add some code for map C,S -> Navigation event for transition between screens.
 *   I will do it in the following ashes. Navi
 *
 *   Should be interface, but had a limitation, cannot be override in other apps(?)
 */
abstract class BaseFViewModel<C, S, UiS, E>(
    private val materializedQuery: MaterializedQuery<UiS, E>,
    private val aggregate: Aggregate<C, S, E>,
) : ViewModel() {

    val state = materializedQuery.viewStates

    fun post(command: C) = viewModelScope.launch {
        aggregate.postCommand(command)
    }
}

class FViewModel<C, S, UiS, E>(
    materializedQuery: MaterializedQuery<UiS, E>,
    aggregate: Aggregate<C, S, E>,
) : BaseFViewModel<C, S, UiS, E>(materializedQuery, aggregate)

fun <C, S, UiS, E> fViewModel(
    materializedQuery: MaterializedQuery<UiS, E>,
    aggregate: Aggregate<C, S, E>,
) = FViewModel(materializedQuery, aggregate)