package fraktal.io.android.demo

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.fraktalio.fmodel.application.EventSourcingAggregate
import com.fraktalio.fmodel.application.handle
import fraktal.io.android.demo.timer.domain.timerDecider
import fraktal.io.android.demo.theme.DemoAndroidTheme
import fraktal.io.android.demo.timer.ui.TimerView
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
    val timerReducer = Reducer(
        decider = timerDecider(),
        scope = CoroutineScope(Dispatchers.IO),
        uiMapper = ::TimerViewStateMapper
    )
}