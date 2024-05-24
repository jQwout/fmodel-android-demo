package fraktal.io.android.demo.timer.ui

import fraktal.io.android.demo.timer.domain.TimerCommand
import fraktal.io.android.demo.timer.domain.TimerState
import java.util.concurrent.TimeUnit

/**
 * Map domain TimerState to UI TimerViewState.
 *
 * Check the MainActivity for usage of this mapping function
 */
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
            ),
            state.all,
            state.period,
            state.isStopped,
        )

        state.isStopped -> TimerViewState(
            formatTime(state.all),
            listOf(
                TimerViewState.ButtonState("Resume", TimerCommand.Resume),
                TimerViewState.ButtonState("Reset", TimerCommand.Reset),
            ),
            state.all,
            state.period,
            state.isStopped,
        )

        else -> TimerViewState(
            formatTime(state.all),
            listOf(TimerViewState.ButtonState("Stop", TimerCommand.Stop)),
            state.all,
            state.period,
            state.isStopped,
        )
    }
}

/**
 * Map TimerViewState to domain TimerState.
 *
 * Check the MainActivity for usage of this mapping function
 *
 * In this example, this direction of mapping from UI state into Domain state is not needed because the Decider is creating/emiting new states/events without reading the current state from DB or UI.
 * In more evolved demos this might be needed.
 */
fun TimerStateMapper(state: TimerViewState): TimerState =
    TimerState(state.all ?: 0, state.period ?: 0, state.isStopped)


private fun formatTime(milliseconds: Long?): String {

    milliseconds ?: return "--:--:--:--"

    val hours = TimeUnit.MILLISECONDS.toHours(milliseconds)
    val minutes = TimeUnit.MILLISECONDS.toMinutes(milliseconds) % 60
    val seconds = TimeUnit.MILLISECONDS.toSeconds(milliseconds) % 60
    val millis = milliseconds % 1000

    return String.format("%02d:%02d:%02d:%03d", hours, minutes, seconds, millis)
}