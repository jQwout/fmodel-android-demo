package fraktal.io.android.demo.workers.profile.domain

import com.fraktalio.fmodel.domain.Decider
import fraktal.io.android.demo.shared.models.Gender
import fraktal.io.android.demo.shared.models.Position
import fraktal.io.android.demo.shared.models.worker.Worker
import fraktal.io.android.demo.shared.repository.WorkerRepository
import fraktal.io.ext.NavigationAR
import fraktal.io.ext.Query
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.datetime.LocalDate


typealias WorkerDecider = Decider<WorkerCommand, WorkerDeciderState, WorkerEvent>
typealias WorkerQuery = Query<WorkerQueryState, WorkerEvent>

sealed interface WorkerCommand {

    data class LoadById(val id: Long) : WorkerCommand

    data object LoadClearView : WorkerCommand

    class TrySave(
        val firstName: String,
        val lastName: String,
        val middleName: String,
        val email: String,
        val phoneNumber: String,
        val position: Position,
        val gender: Gender,
        val date: LocalDate
    ) : WorkerCommand

    data object Back : WorkerCommand
}

sealed interface WorkerEvent {

    data object OnClearForm : WorkerEvent

    class OnLoaded(val worker: Worker) : WorkerEvent

    class OnNumberExists(val worker: Worker) : WorkerEvent

    class OnSavedById(val worker: Worker) : WorkerEvent

    class OnUserCreated(val worker: Worker) : WorkerEvent

    class OnBackPressed(val hasChanged: Boolean) : WorkerEvent, NavigationAR
}

data class WorkerDeciderState(
    val worker: Worker?,
    val hasChanged: Boolean,
)

fun workerDecider(
    workerRepository: WorkerRepository,
): WorkerDecider = WorkerDecider(
    initialState = WorkerDeciderState(null, false),
    decide = { workerCommand, workerState ->
        when (workerCommand) {

            is WorkerCommand.LoadClearView -> flowOf(WorkerEvent.OnClearForm)
            is WorkerCommand.LoadById -> flow {
                val worker = workerRepository.get(workerCommand.id)
                if (worker != null) {
                    emit(WorkerEvent.OnLoaded(worker))
                }
            }

            is WorkerCommand.TrySave -> flow {
                val worker = Worker(
                    workerState.worker?.id ?: generateNewId(workerCommand.phoneNumber),
                    workerCommand.firstName,
                    workerCommand.lastName,
                    workerCommand.middleName,
                    workerCommand.email,
                    workerCommand.phoneNumber,
                    workerCommand.position,
                    workerCommand.gender,
                    workerCommand.date
                )

                try {
                    if (workerState.worker?.id == null) {
                        workerRepository.put(worker)
                        emit(WorkerEvent.OnUserCreated(worker))
                    } else {
                        workerRepository.update(worker)
                        emit(WorkerEvent.OnSavedById(worker))
                    }
                } catch (e: Throwable) {
                    emit(WorkerEvent.OnNumberExists(worker))
                }
            }

            is WorkerCommand.Back -> flow {
                emit(WorkerEvent.OnBackPressed(workerState.hasChanged))
            }
        }
    },
    evolve = { state, workerEvent ->
        when (workerEvent) {
            is WorkerEvent.OnClearForm -> state.copy(worker = null, hasChanged = false)
            is WorkerEvent.OnLoaded -> state.copy(worker = workerEvent.worker, hasChanged = false)
            is WorkerEvent.OnUserCreated -> state.copy(worker = state.worker, hasChanged = true)
            is WorkerEvent.OnSavedById -> state.copy(worker = state.worker, hasChanged = true)
            else -> state
        }
    }
)

private fun generateNewId(email: String) = email.hashCode().toLong()

data class WorkerQueryState(
    val worker: Worker?,
    val hasPhoneNumberError: Boolean,
    val isEditMode: Boolean = false,
)

fun workerQuery(): WorkerQuery = WorkerQuery(
    initialState = WorkerQueryState(null, false, false),
    evolve = { state, workerEvent ->
        when (workerEvent) {
            is WorkerEvent.OnClearForm -> state.copy(worker = null, hasPhoneNumberError = false)
            is WorkerEvent.OnLoaded -> state.copy(worker = workerEvent.worker, hasPhoneNumberError = false, isEditMode = true)
            is WorkerEvent.OnNumberExists -> state.copy(worker = workerEvent.worker, hasPhoneNumberError = true, isEditMode = false)
            is WorkerEvent.OnSavedById -> state.copy(worker = workerEvent.worker, hasPhoneNumberError = false, isEditMode = true)
            is WorkerEvent.OnUserCreated -> state.copy(worker = workerEvent.worker, hasPhoneNumberError = false, isEditMode = true)
            is WorkerEvent.OnBackPressed -> state
        }
    },
)