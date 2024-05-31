package fraktal.io.android.demo.workers.profile.domain

import com.fraktalio.fmodel.domain.Decider
import fraktal.io.android.demo.shared.models.Gender
import fraktal.io.android.demo.shared.models.Position
import fraktal.io.android.demo.shared.models.worker.Worker
import fraktal.io.android.demo.shared.repository.WorkerRepository
import fraktal.io.ext.Query
import kotlinx.coroutines.flow.flow
import kotlinx.datetime.LocalDate


typealias WorkerDecider = Decider<WorkerCommand, Worker?, WorkerEvent>
typealias WorkerQuery = Query<WorkerQueryState?, WorkerEvent>

sealed interface WorkerCommand {

    data class LoadById(val id: Long) : WorkerCommand

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
}

sealed interface WorkerEvent {

    class OnLoaded(val worker: Worker) : WorkerEvent

    class OnNumberExists(val worker: Worker) : WorkerEvent

    class OnSavedById(val worker: Worker) : WorkerEvent

    class OnUserCreated(val worker: Worker) : WorkerEvent
}

fun workerDecider(
    workerRepository: WorkerRepository,
): WorkerDecider = WorkerDecider(
    initialState = null,
    decide = { workerCommand, workerState ->
        when (workerCommand) {

            is WorkerCommand.LoadById -> flow {
                val worker = workerRepository.get(workerCommand.id)
                if (worker != null) {
                    emit(WorkerEvent.OnLoaded(worker))
                }
            }

            is WorkerCommand.TrySave -> flow {
                val worker = Worker(
                    workerState?.id ?: generateNewId(workerCommand.email),
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
                    workerRepository.put(worker)
                    if (workerState?.id == null) {
                        emit(WorkerEvent.OnUserCreated(worker))
                    } else {
                        emit(WorkerEvent.OnSavedById(worker))
                    }
                } catch (e: Throwable) {
                    emit(WorkerEvent.OnNumberExists(worker))
                }
            }
        }
    },
    evolve = { _, workerEvent ->
        when (workerEvent) {
            is WorkerEvent.OnLoaded -> workerEvent.worker
            is WorkerEvent.OnUserCreated -> workerEvent.worker
            is WorkerEvent.OnSavedById -> workerEvent.worker
            else -> null
        }
    }
)

private fun generateNewId(email: String) = email.hashCode().toLong()

data class WorkerQueryState(
    val worker: Worker?,
    val hasPhoneNumberError: Boolean,
)

fun workerQuery(): WorkerQuery = WorkerQuery(
    initialState = null,
    evolve = { state, workerEvent ->
        when (workerEvent) {
            is WorkerEvent.OnLoaded -> WorkerQueryState(workerEvent.worker, false)
            is WorkerEvent.OnNumberExists -> WorkerQueryState(workerEvent.worker, true)
            is WorkerEvent.OnSavedById -> WorkerQueryState(workerEvent.worker, false)
            is WorkerEvent.OnUserCreated -> WorkerQueryState(workerEvent.worker, false)
        }
    },
)