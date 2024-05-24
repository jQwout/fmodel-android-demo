package fraktal.io.android.demo.timer.domain

import android.util.Log
import com.fraktalio.fmodel.application.EventSourcingAggregate
import com.fraktalio.fmodel.application.StateStoredAggregate
import com.fraktalio.fmodel.domain.Decider
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onEmpty

typealias TimerDecider = Decider<TimerCommand, TimerState, TimerEvent>

fun timerDecider() : TimerDecider = TimerDecider(
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

sealed interface TimerCommand {
    data object Start : TimerCommand
    data object Resume : TimerCommand
    data object Stop : TimerCommand
    data object Reset : TimerCommand
}

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


data class TimerState(
    val all: Long,
    val period: Long,
    val isStopped: Boolean
)