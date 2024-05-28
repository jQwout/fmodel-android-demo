package fraktal.io.android.demo.timer.ui

import androidx.compose.runtime.Stable
import fraktal.io.android.demo.timer.domain.TimerCommand
import fraktal.io.android.demo.timer.domain.TimerViewState
import java.util.concurrent.TimeUnit

@Stable
data class TimerViewStateUI(
    val timerText: String,
    val timer: Long,
    val buttons: List<ButtonState>,
    val isNewTimerCreated: Boolean
) {

    @Stable
    data class ButtonState(
        val text: String,
        val command: TimerCommand
    )
}

fun TimerViewStateUI.asTimerViewState() = TimerViewState(
    timer,
    buttons.map { TimerViewState.ActionState(it.text, it.command) },
    isNewTimerCreated
)

fun TimerViewState.asTimerViewStateUI() = TimerViewStateUI(
    formatTime(timer),
    timer,
    actions.map { TimerViewStateUI.ButtonState(it.text, it.command) },
    isNewTimerCreated
)

private fun formatTime(milliseconds: Long?): String {

    milliseconds ?: return "--:--:--:--"

    val hours = TimeUnit.MILLISECONDS.toHours(milliseconds)
    val minutes = TimeUnit.MILLISECONDS.toMinutes(milliseconds) % 60
    val seconds = TimeUnit.MILLISECONDS.toSeconds(milliseconds) % 60
    val millis = milliseconds % 1000

    return String.format("%02d:%02d:%02d:%03d", hours, minutes, seconds, millis)
}