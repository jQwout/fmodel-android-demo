package fraktal.io.android.demo.timer.ui

import fraktal.io.android.demo.timer.domain.TimerCommand
import fraktal.io.android.demo.timer.domain.TimerState
import java.util.concurrent.TimeUnit

fun TimerViewStateMapper(state: TimerState?): TimerViewState {
    return when {
        state == null -> TimerViewState(
            formatTime(null),
            listOf(TimerViewState.ButtonState("Start", TimerCommand.Start))
        )

        (state.all == 0L) && state.isStopped.not() -> TimerViewState(
            formatTime(state.all),
            listOf(
                TimerViewState.ButtonState("Start", TimerCommand.Start)
            )
        )

        state.isStopped -> TimerViewState(
            formatTime(state.all),
            listOf(
                TimerViewState.ButtonState("Resume", TimerCommand.Resume),
                TimerViewState.ButtonState("Reset", TimerCommand.Reset),
            )
        )

        else -> TimerViewState(formatTime(state.all), listOf(TimerViewState.ButtonState("Stop", TimerCommand.Stop)))
    }
}


private fun formatTime(milliseconds: Long?): String {

    milliseconds ?: return "--:--:--:--"

    val hours = TimeUnit.MILLISECONDS.toHours(milliseconds)
    val minutes = TimeUnit.MILLISECONDS.toMinutes(milliseconds) % 60
    val seconds = TimeUnit.MILLISECONDS.toSeconds(milliseconds) % 60
    val millis = milliseconds % 1000

    return String.format("%02d:%02d:%02d:%03d", hours, minutes, seconds, millis)
}