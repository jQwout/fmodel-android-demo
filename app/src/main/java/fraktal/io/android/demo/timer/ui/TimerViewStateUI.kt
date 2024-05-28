package fraktal.io.android.demo.timer.ui

import androidx.compose.runtime.Stable
import fraktal.io.android.demo.timer.domain.TimerCommand
import fraktal.io.android.demo.timer.domain.TimerEvent
import fraktal.io.android.demo.timer.domain.TimerQueryState
import fraktal.io.android.demo.timer.domain.TimerState
import fraktal.io.ext.FViewModel
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toPersistentList
import java.util.concurrent.TimeUnit

typealias TimerViewModel = FViewModel<TimerCommand,TimerState, TimerViewStateUI, TimerEvent>

@Stable
data class TimerViewStateUI(
    val timerText: String,
    val timer: Long,
    val buttons: ImmutableList<ButtonState>,
    val isNewTimerCreated: Boolean
) {

    @Stable
    data class ButtonState(
        val text: String,
        val command: TimerCommand
    )
}

fun TimerViewStateUI.asTimerViewState() = TimerQueryState(
    timer,
    buttons.map { TimerQueryState.ActionState(it.text, it.command) },
    isNewTimerCreated
)

fun TimerQueryState.asTimerViewStateUI() = TimerViewStateUI(
    formatTime(timer),
    timer,
    actions.map { TimerViewStateUI.ButtonState(it.text, it.command) }.toPersistentList(),
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