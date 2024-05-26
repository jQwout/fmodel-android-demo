package fraktal.io.android.demo.timer.domain

import android.util.Log
import com.fraktalio.fmodel.domain.Decider
import com.fraktalio.fmodel.domain.View
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onEmpty

typealias TimerDecider = Decider<TimerCommand, TimerState, TimerEvent>
typealias TimerView = View<TimerViewState, TimerEvent>

/**
 * API: Commands
 */
sealed interface TimerCommand {
    data object Start : TimerCommand
    data object Resume : TimerCommand
    data object Stop : TimerCommand
    data object Reset : TimerCommand
}

/**
 * API: Events
 */
sealed interface TimerEvent {
    data class OnNewTimerCreated(
        val all: Long,
        val period: Long,
    ) : TimerEvent

    class OnTimerTick(val tick: Long = 21) : TimerEvent

    data object OnTimerStopped : TimerEvent
    data object OnTimerStarted : TimerEvent

    data class OnTimerStartError(val t: Throwable) : TimerEvent

    data object OnTimerSpend : TimerEvent
}

/**
 * API: The state of the TimerDecider
 */
data class TimerState(
    val all: Long,
    val period: Long,
    val isStopped: Boolean
)

/**
 * Decider.
 * Domain layer component that makes the most important decisions/events.
 */
fun timerDecider(): TimerDecider = TimerDecider(
    initialState = TimerState(0, 21, false),
    decide = { command, state ->
        when (command) {
            TimerCommand.Reset -> flow {
                emit(TimerEvent.OnNewTimerCreated(0, state.period))
            }

            TimerCommand.Resume,
            TimerCommand.Start -> flow<TimerEvent> {
                emit(TimerEvent.OnTimerStarted)
                while (true) {
                    delay(state.period)
                    emit(TimerEvent.OnTimerTick(state.period))
                }
            }

            TimerCommand.Stop -> flowOf(TimerEvent.OnTimerStopped)
        }
            .onEmpty {
                Log.d("timerDecider", "decide($command,$state) = empty")
            }
            .onEach {
                Log.d("timerDecider", "decide($command,$state) = $it")
            }
    },
    evolve = { state, event ->
        when (event) {
            is TimerEvent.OnNewTimerCreated -> TimerState(event.all, event.period, false)
            TimerEvent.OnTimerStarted -> state.copy(isStopped = false)
            TimerEvent.OnTimerStopped -> state.copy(isStopped = true)
            is TimerEvent.OnTimerTick -> state.copy(all = state.all + event.tick)
            is TimerEvent.OnTimerStartError -> state
            is TimerEvent.OnTimerSpend -> state
        }.apply {
            Log.d("timerDecider", "evolve($state,$event) = $this")
        }
    }
)

/**
 * API: The state of the TimerView
 */
data class TimerViewState(
    val timer: Long,
    val actions: List<ActionState>
) {
    data class ActionState(
        val text: String,
        val command: TimerCommand
    )
}

/**
 * View.
 * Domain component. It represent the Query side of the CQRS pattern.
 * It evolves the `View State` based on the events published by the decider.
 * It is better to use this View State to render your View on the UI,
 * and you can have many different Timer Views in domain to support different requirements on the Presentation/Ui layer.
 */
fun timerView(): TimerView = TimerView(
    initialState = TimerViewState(
        0,
        listOf(TimerViewState.ActionState("Start", TimerCommand.Start))
    ),
    evolve = { state, event ->
        when (event) {
            is TimerEvent.OnNewTimerCreated -> TimerViewState(
                event.all,
                listOf(TimerViewState.ActionState("Start", TimerCommand.Start))
            )

            TimerEvent.OnTimerStarted -> state.copy(
                actions = listOf(
                    TimerViewState.ActionState("Stop", TimerCommand.Stop),
                )
            )

            TimerEvent.OnTimerStopped -> state.copy(
                actions = listOf(
                    TimerViewState.ActionState("Resume", TimerCommand.Resume),
                    TimerViewState.ActionState("Reset", TimerCommand.Reset),
                )
            )

            is TimerEvent.OnTimerTick -> state.copy(
                timer = state.timer + event.tick
            )

            is TimerEvent.OnTimerStartError -> state
            is TimerEvent.OnTimerSpend -> state
        }.apply {
            Log.d("timerDecider", "evolve($state,$event) = $this")
        }
    }
)
