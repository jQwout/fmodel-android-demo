package fraktal.io.android.demo.workers.profile

import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import fraktal.io.android.demo.shared.models.Gender
import fraktal.io.android.demo.shared.models.Position
import fraktal.io.android.demo.shared.models.worker.Worker
import fraktal.io.android.demo.shared.repository.WorkerRepository
import fraktal.io.android.demo.workers.profile.domain.WorkerCommand
import fraktal.io.android.demo.workers.profile.domain.WorkerEvent
import fraktal.io.android.demo.workers.profile.domain.WorkerQueryState
import fraktal.io.android.demo.workers.profile.domain.workerDecider
import fraktal.io.android.demo.workers.profile.domain.workerQuery
import fraktal.io.ext.Aggregate
import fraktal.io.ext.EventBus
import fraktal.io.ext.MaterializedQuery
import fraktal.io.ext.fViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.datetime.LocalDate

object WorkerServiceLocator {

    private val workerRepository: WorkerRepository = WorkerRepository.InMemory(
        MutableStateFlow(
            listOf(
                Worker(
                    1, "aaa", "bb", "", "email@rt.com", "89256044473",
                    Position.DRIVER, Gender.MALE, LocalDate(2000, 3, 4)
                )
            )
        )
    )

    private val eventBus: EventBus<WorkerEvent> = EventBus()
    private val workerDecider = workerDecider(workerRepository)
    private val coroutineScope = CoroutineScope(Dispatchers.IO)
    private val profileWorkerAggregate: Aggregate<WorkerCommand, Worker?, WorkerEvent> =
        Aggregate(workerDecider, eventBus, coroutineScope)
    private val materializedView: MaterializedQuery<WorkerDataUI, WorkerEvent> = MaterializedQuery(
        workerQuery().dimapOnState(WorkerDataUI::asWorkerQueryState, WorkerQueryState?::asWorkerDataUI),
        eventBus,
        coroutineScope
    )

    val workerProfile: ViewModelProvider.Factory = viewModelFactory {
        initializer {
            fViewModel(materializedView, profileWorkerAggregate)
        }
    }
}