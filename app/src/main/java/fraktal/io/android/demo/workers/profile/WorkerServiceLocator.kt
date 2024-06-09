package fraktal.io.android.demo.workers.profile

import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import fraktal.io.android.demo.nav.NavLocator
import fraktal.io.android.demo.shared.db.DbLocator
import fraktal.io.android.demo.shared.repository.WorkerRepository
import fraktal.io.android.demo.workers.profile.domain.WorkerCommand
import fraktal.io.android.demo.workers.profile.domain.WorkerDeciderState
import fraktal.io.android.demo.workers.profile.domain.WorkerEvent
import fraktal.io.android.demo.workers.profile.domain.WorkerQueryState
import fraktal.io.android.demo.workers.profile.domain.workerDecider
import fraktal.io.android.demo.workers.profile.domain.workerQuery
import fraktal.io.android.demo.workers.profile.ui.WorkerViewModel
import fraktal.io.android.demo.workers.profile.ui.WorkerViewStateUI
import fraktal.io.android.demo.workers.profile.ui.asWorkerDataUI
import fraktal.io.android.demo.workers.profile.ui.asWorkerQueryState
import fraktal.io.ext.Aggregate
import fraktal.io.ext.EventBus
import fraktal.io.ext.MaterializedQuery
import fraktal.io.ext.fViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers

object WorkerServiceLocator {

    private val workerRepository: WorkerRepository
            by lazy {
                WorkerRepository.Db(
                    DbLocator.database.workersQueries
                )
            }
    // can use it for debug
    //= WorkerRepository.InMemory(
    //    MutableStateFlow(
    //        listOf(
    //            Worker(
    //                1, "aaa", "bb", "", "email@rt.com", "89256044473",
    //                Position.DRIVER, Gender.MALE, LocalDate(2000, 3, 4)
    //            )
    //        )
    //    )
    //)

    private val eventBus: EventBus<WorkerEvent> = EventBus()
    private val workerDecider = workerDecider(workerRepository)
    private val coroutineScope = CoroutineScope(Dispatchers.IO)
    private val profileWorkerAggregate: Aggregate<WorkerCommand, WorkerDeciderState, WorkerEvent> =
        Aggregate(workerDecider, eventBus, NavLocator.navManager, coroutineScope)
    private val materializedView: MaterializedQuery<WorkerViewStateUI, WorkerEvent> = MaterializedQuery(
        workerQuery().dimapOnState(WorkerViewStateUI::asWorkerQueryState, WorkerQueryState?::asWorkerDataUI),
        eventBus,
        coroutineScope
    )

    val workerProfile: ViewModelProvider.Factory = viewModelFactory {
        initializer<WorkerViewModel> {
            fViewModel(materializedView, profileWorkerAggregate)
        }
    }
}