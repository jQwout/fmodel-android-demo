package fraktal.io.android.demo.timer.domain

import com.fraktalio.fmodel.domain.Decider
import fraktal.io.ext.Query
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf

typealias TimerDecider = Decider<TimerCommand, TimerState, TimerEvent>
typealias TimerQuery = Query<TimerQueryState, TimerEvent>

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
    },
    evolve = { state, event ->
        when (event) {
            is TimerEvent.OnNewTimerCreated -> TimerState(event.all, event.period, false)
            TimerEvent.OnTimerStarted -> state.copy(isStopped = false)
            TimerEvent.OnTimerStopped -> state.copy(isStopped = true)
            is TimerEvent.OnTimerTick -> state.copy(all = state.all + event.tick)
            is TimerEvent.OnTimerStartError -> state
            is TimerEvent.OnTimerSpend -> state
        }
    }
)

/**
 * API: The state of the TimerQuery
 */
data class TimerQueryState(
    val timer: Long,
    val actions: List<ActionState>,
    val isNewTimerCreated: Boolean
) {
    data class ActionState(
        val text: String,
        val command: TimerCommand
    )
}

/**
 * Query(originally View) .
 * Domain component. It represent the Query side of the CQRS pattern.
 * It evolves the `View State` based on the events published by the decider.
 * It is better to use this View State to render your View on the UI,
 * and you can have many different Timer Views in domain to support different requirements on the Presentation/Ui layer.
 */
fun timerQuery(): TimerQuery = TimerQuery(
    initialState = TimerQueryState(
        0,
        listOf(TimerQueryState.ActionState("Start", TimerCommand.Start)),
        false
    ),
    evolve = { state, event ->
        when (event) {
            is TimerEvent.OnNewTimerCreated -> TimerQueryState(
                event.all,
                listOf(TimerQueryState.ActionState("Start", TimerCommand.Start)),
                true
            )

            TimerEvent.OnTimerStarted -> state.copy(
                actions = listOf(
                    TimerQueryState.ActionState("Stop", TimerCommand.Stop),
                ),
                isNewTimerCreated = false
            )

            TimerEvent.OnTimerStopped -> state.copy(
                actions = listOf(
                    TimerQueryState.ActionState("Resume", TimerCommand.Resume),
                    TimerQueryState.ActionState("Reset", TimerCommand.Reset),
                ),
                isNewTimerCreated = false
            )

            is TimerEvent.OnTimerTick -> state.copy(
                timer = state.timer + event.tick,
                isNewTimerCreated = false
            )

            is TimerEvent.OnTimerStartError -> state
            is TimerEvent.OnTimerSpend -> state
        }
    }
)
