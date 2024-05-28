package fraktal.io.android.demo

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import fraktal.io.android.demo.theme.DemoAndroidTheme
import fraktal.io.android.demo.timer.ui.TimerView
import fraktal.io.android.demo.timer.ui.TimerViewModel

class MainActivity : ComponentActivity() {

    private val timerViewModel by viewModels<TimerViewModel> {
        DI.timerViewModelFactory
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            DemoAndroidTheme {
                TimerView(timerViewModel)
            }
        }
    }
}

