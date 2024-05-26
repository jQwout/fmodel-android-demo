package fraktal.io.android.demo

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import fraktal.io.android.demo.timer.domain.timerDecider
import fraktal.io.android.demo.theme.DemoAndroidTheme
import fraktal.io.android.demo.timer.domain.TimerViewState
import fraktal.io.android.demo.timer.domain.timerView
import fraktal.io.android.demo.timer.ui.TimerView
import fraktal.io.android.demo.timer.ui.TimerViewStateUI
import fraktal.io.android.demo.timer.ui.asTimerViewState
import fraktal.io.android.demo.timer.ui.asTimerViewStateUI
import fraktal.io.ext.Reducer
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            DemoAndroidTheme {
                TimerView(reducer = DI.timerReducer)
            }
        }
    }
}


object DI {
    val timerReducer = Reducer(
        decider = timerDecider(),
        // We don't expose domain `TimerViewState` to the UI, rather we map it to the `@Stable TimerViewStateUI`
        // Two map functions are needed (it is a Dimap), mapping states from both directions. It only make sense to do so and maintain relationship from both directions: UI->domain, domain->UI.
        view = timerView().dimapOnState(TimerViewStateUI::asTimerViewState, TimerViewState::asTimerViewStateUI),
        scope = CoroutineScope(Dispatchers.IO),
    )
}