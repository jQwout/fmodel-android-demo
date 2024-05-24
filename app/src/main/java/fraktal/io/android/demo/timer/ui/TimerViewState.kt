package fraktal.io.android.demo.timer.ui

import androidx.compose.runtime.Stable
import fraktal.io.android.demo.timer.domain.TimerCommand

@Stable
data class TimerViewState(
    val timerText: String,
    val buttons: List<ButtonState>,
    val all: Long? = null,
    val period: Long? = null,
    val isStopped: Boolean = false,
) {

    @Stable
    data class ButtonState(
        val text: String,
        val command: TimerCommand
    )
}