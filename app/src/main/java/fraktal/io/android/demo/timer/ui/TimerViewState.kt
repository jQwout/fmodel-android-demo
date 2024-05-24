package fraktal.io.android.demo.timer.ui

import androidx.compose.runtime.Stable
import fraktal.io.android.demo.timer.domain.TimerCommand

@Stable
data class TimerViewState(
    val timerText: String,
    val buttons: List<ButtonState>
) {

    @Stable
    data class ButtonState(
        val text: String,
        val command: TimerCommand
    )
}