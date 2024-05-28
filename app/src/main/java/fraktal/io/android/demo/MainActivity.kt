package fraktal.io.android.demo

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import fraktal.io.android.demo.timer.domain.timerDecider
import fraktal.io.android.demo.theme.DemoAndroidTheme
import fraktal.io.android.demo.timer.domain.TimerCommand
import fraktal.io.android.demo.timer.domain.TimerEvent
import fraktal.io.android.demo.timer.domain.TimerState
import fraktal.io.android.demo.timer.domain.TimerViewState
import fraktal.io.android.demo.timer.domain.timerView
import fraktal.io.android.demo.timer.ui.TimerView
import fraktal.io.android.demo.timer.ui.TimerViewStateUI
import fraktal.io.android.demo.timer.ui.asTimerViewState
import fraktal.io.android.demo.timer.ui.asTimerViewStateUI
import fraktal.io.ext.EventBus
import fraktal.io.ext.Aggregate
import fraktal.io.ext.MaterializedView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            DemoAndroidTheme {
                TimerView(DI.aggregate, DI.materializedView)
            }
        }
    }
}

object DI {
    private val eventBus: EventBus<TimerEvent> = EventBus()
    val aggregate: Aggregate<TimerCommand, TimerState, TimerEvent> =
        Aggregate(timerDecider(), eventBus, CoroutineScope(Dispatchers.IO))
    val materializedView: MaterializedView<TimerViewStateUI, TimerEvent> = MaterializedView(
        timerView().dimapOnState(
            TimerViewStateUI::asTimerViewState,
            TimerViewState::asTimerViewStateUI
        ), eventBus, CoroutineScope(Dispatchers.IO)
    )
}