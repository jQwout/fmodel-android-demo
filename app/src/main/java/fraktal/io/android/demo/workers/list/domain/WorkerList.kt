package fraktal.io.android.demo.workers.list.domain

import com.fraktalio.fmodel.domain.Decider
import fraktal.io.android.demo.shared.models.worker.Worker
import fraktal.io.android.demo.shared.repository.WorkerRepository
import fraktal.io.ext.NavigationAR
import fraktal.io.ext.Query
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf

typealias WorkerList = List<Worker>
typealias WorkerListDecider = Decider<WorkerListCommand, WorkerList, WorkerListEvent>
typealias WorkerListQuery = Query<WorkerListQueryState, WorkerListEvent>

sealed interface WorkerListCommand {

    object Load : WorkerListCommand

    class OnEdit(val workerId: Long) : WorkerListCommand

    object OnCreate : WorkerListCommand
}


sealed interface WorkerListEvent {

    data object Loading : WorkerListEvent

    class ListLoaded(val workers: List<Worker>) : WorkerListEvent

    class EditNav(val workerId: Long) : WorkerListEvent, NavigationAR

    object CreateNav : WorkerListEvent, NavigationAR

    data object UserNotFound : WorkerListEvent

    data object ListNotLoaded : WorkerListEvent
}


fun workerListDecider(workerRepository: WorkerRepository): WorkerListDecider = WorkerListDecider(
    initialState = emptyList(),
    decide = { command, state ->
        when (command) {

            is WorkerListCommand.Load -> flow {
                emit(WorkerListEvent.Loading)
                delay(1000) // synthetic waiting
                val all = workerRepository.getAll()
                emit(WorkerListEvent.ListLoaded(all))
            }

            WorkerListCommand.OnCreate -> flowOf(WorkerListEvent.CreateNav)
            is WorkerListCommand.OnEdit -> flowOf(WorkerListEvent.EditNav(command.workerId))
        }
    },
    evolve = { state, event ->
        when (event) {
            WorkerListEvent.CreateNav -> state
            is WorkerListEvent.EditNav -> state
            is WorkerListEvent.ListLoaded -> event.workers
            WorkerListEvent.ListNotLoaded -> state
            WorkerListEvent.Loading -> state
            WorkerListEvent.UserNotFound -> state
        }
    }
)

data class WorkerListQueryState(
    val workers: WorkerList = emptyList(),
    val isLoading: Boolean = false,
    val hasError: String? = null
)

fun workerListQuery(): WorkerListQuery = WorkerListQuery(
    initialState = WorkerListQueryState(),
    evolve = { state, event ->
        when (event) {
            WorkerListEvent.CreateNav -> state.copy(isLoading = false, hasError = null)
            is WorkerListEvent.EditNav -> state.copy(isLoading = false, hasError = null)
            is WorkerListEvent.ListLoaded -> WorkerListQueryState(event.workers, false, null)
            WorkerListEvent.ListNotLoaded -> WorkerListQueryState(state.workers, false, "worker cannot be loaded!")
            WorkerListEvent.Loading -> state.copy(isLoading = true, hasError = null)
            WorkerListEvent.UserNotFound -> state.copy(isLoading = false, hasError = null)
        }
    }
)