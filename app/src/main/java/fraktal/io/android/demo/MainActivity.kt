package fraktal.io.android.demo

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import fraktal.io.android.demo.timer.domain.timerDecider
import fraktal.io.android.demo.theme.DemoAndroidTheme
import fraktal.io.android.demo.timer.domain.TimerCommand
import fraktal.io.android.demo.timer.domain.TimerEvent
import fraktal.io.android.demo.timer.ui.TimerStateMapper
import fraktal.io.android.demo.timer.ui.TimerView
import fraktal.io.android.demo.timer.ui.TimerViewState
import fraktal.io.android.demo.timer.ui.TimerViewStateMapper
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
    // Feel free to use Mapper functions on the Decider here.
    // Notice how you need to provide two Map functions, in both direction (from TimerViewState to TimerState, and from TimerState to TimerViewState)
    private val timerDecider =
        timerDecider().dimapOnState(::TimerStateMapper, ::TimerViewStateMapper)

    //At this point the Reducer is aware only of TimerViewState type
    val timerReducer: Reducer<TimerCommand, TimerViewState, TimerEvent> = Reducer(
        decider = timerDecider,
        scope = CoroutineScope(Dispatchers.IO),
    )
}