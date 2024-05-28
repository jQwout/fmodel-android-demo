package fraktal.io.android.demo

import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import fraktal.io.android.demo.timer.domain.TimerCommand
import fraktal.io.android.demo.timer.domain.TimerEvent
import fraktal.io.android.demo.timer.domain.TimerState
import fraktal.io.android.demo.timer.domain.TimerQueryState
import fraktal.io.android.demo.timer.domain.timerDecider
import fraktal.io.android.demo.timer.domain.timerQuery
import fraktal.io.android.demo.timer.ui.TimerViewStateUI
import fraktal.io.android.demo.timer.ui.asTimerViewState
import fraktal.io.android.demo.timer.ui.asTimerViewStateUI
import fraktal.io.ext.Aggregate
import fraktal.io.ext.EventBus
import fraktal.io.ext.MaterializedQuery
import fraktal.io.ext.fViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers

object DI {
    private val eventBus: EventBus<TimerEvent> = EventBus()
    private val aggregate: Aggregate<TimerCommand, TimerState, TimerEvent> =
        Aggregate(timerDecider(), eventBus, CoroutineScope(Dispatchers.IO))
    private val materializedView: MaterializedQuery<TimerViewStateUI, TimerEvent> = MaterializedQuery(
        timerQuery().dimapOnState(
            TimerViewStateUI::asTimerViewState,
            TimerQueryState::asTimerViewStateUI
        ), eventBus, CoroutineScope(Dispatchers.IO)
    )


    val timerViewModelFactory: ViewModelProvider.Factory = viewModelFactory {
        initializer {
            fViewModel(materializedView, aggregate)
        }
    }
}